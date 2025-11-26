package com.leclowndu93150.chisel.api.chunkdata;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores per-block texture offset data for a chunk.
 * Each block position can have its own offset for CTM alignment.
 */
public class OffsetData implements IOffsetData {

    private final Map<BlockPos, BlockPos> offsets = new HashMap<>();

    public OffsetData() {
    }

    /**
     * Gets the offset for a specific block position.
     * Returns BlockPos.ZERO if no offset is set.
     */
    @Override
    public BlockPos getOffset(BlockPos pos) {
        return offsets.getOrDefault(toChunkLocal(pos), BlockPos.ZERO);
    }

    /**
     * Sets the offset for a specific block position.
     */
    public void setOffset(BlockPos pos, BlockPos offset) {
        BlockPos local = toChunkLocal(pos);
        if (offset.equals(BlockPos.ZERO)) {
            offsets.remove(local);
        } else {
            offsets.put(local, wrap(offset));
        }
    }

    /**
     * Moves the offset for a specific block position in the given direction.
     */
    public void move(BlockPos pos, Direction dir) {
        BlockPos local = toChunkLocal(pos);
        BlockPos current = offsets.getOrDefault(local, BlockPos.ZERO);
        BlockPos newOffset = wrap(current.relative(dir.getOpposite()));
        if (newOffset.equals(BlockPos.ZERO)) {
            offsets.remove(local);
        } else {
            offsets.put(local, newOffset);
        }
    }

    /**
     * Sets the same offset for multiple block positions.
     */
    public void setOffsetForAll(Set<BlockPos> positions, BlockPos offset) {
        BlockPos wrapped = wrap(offset);
        for (BlockPos pos : positions) {
            BlockPos local = toChunkLocal(pos);
            if (wrapped.equals(BlockPos.ZERO)) {
                offsets.remove(local);
            } else {
                offsets.put(local, wrapped);
            }
        }
    }

    /**
     * Moves the offset for multiple block positions in the given direction.
     */
    public void moveAll(Set<BlockPos> positions, Direction dir) {
        // All connected blocks should share the same offset, so get the first one
        BlockPos firstLocal = null;
        for (BlockPos pos : positions) {
            firstLocal = toChunkLocal(pos);
            break;
        }
        if (firstLocal == null) return;

        BlockPos current = offsets.getOrDefault(firstLocal, BlockPos.ZERO);
        BlockPos newOffset = wrap(current.relative(dir.getOpposite()));

        for (BlockPos pos : positions) {
            BlockPos local = toChunkLocal(pos);
            if (newOffset.equals(BlockPos.ZERO)) {
                offsets.remove(local);
            } else {
                offsets.put(local, newOffset);
            }
        }
    }

    public boolean isEmpty() {
        return offsets.isEmpty();
    }

    /**
     * Converts world position to chunk-local position (0-15 range).
     */
    private BlockPos toChunkLocal(BlockPos pos) {
        return new BlockPos(pos.getX() & 15, pos.getY(), pos.getZ() & 15);
    }

    private int positiveModulo(int num, int denom) {
        return ((num % denom) + denom) % denom;
    }

    private BlockPos wrap(BlockPos pos) {
        return new BlockPos(
                positiveModulo(pos.getX(), 16),
                positiveModulo(pos.getY(), 16),
                positiveModulo(pos.getZ(), 16)
        );
    }

    /**
     * Writes this offset data to NBT.
     */
    public void writeToNBT(CompoundTag tag) {
        if (offsets.isEmpty()) {
            return;
        }

        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, BlockPos> entry : offsets.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            BlockPos pos = entry.getKey();
            BlockPos offset = entry.getValue();

            // Pack position: x(4 bits) + y(full) + z(4 bits)
            entryTag.putInt("pos", (pos.getX() << 28) | ((pos.getY() & 0xFFFFFF) << 4) | pos.getZ());
            // Pack offset: x(4 bits) + y(4 bits) + z(4 bits) = 12 bits
            entryTag.putShort("off", (short) ((offset.getX() << 8) | (offset.getY() << 4) | offset.getZ()));

            list.add(entryTag);
        }
        tag.put("offsets", list);
    }

    /**
     * Reads offset data from NBT.
     */
    public void readFromNBT(CompoundTag tag) {
        offsets.clear();

        if (!tag.contains("offsets", Tag.TAG_LIST)) {
            return;
        }

        ListTag list = tag.getList("offsets", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entryTag = list.getCompound(i);

            int packedPos = entryTag.getInt("pos");
            int x = (packedPos >> 28) & 0xF;
            int y = (packedPos >> 4) & 0xFFFFFF;
            int z = packedPos & 0xF;
            // Handle sign extension for Y coordinate
            if ((y & 0x800000) != 0) {
                y |= 0xFF000000;
            }
            BlockPos pos = new BlockPos(x, y, z);

            short packedOffset = entryTag.getShort("off");
            BlockPos offset = new BlockPos(
                    (packedOffset >> 8) & 0xF,
                    (packedOffset >> 4) & 0xF,
                    packedOffset & 0xF
            );

            offsets.put(pos, offset);
        }
    }

    public OffsetData copy() {
        OffsetData copy = new OffsetData();
        copy.offsets.putAll(this.offsets);
        return copy;
    }
}
