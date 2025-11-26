package com.leclowndu93150.chisel.item;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.leclowndu93150.chisel.api.chunkdata.ChunkData;
import com.leclowndu93150.chisel.api.chunkdata.OffsetData;
import com.leclowndu93150.chisel.network.ChunkDataPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.awt.geom.Line2D;
import java.util.*;

import static net.minecraft.core.Direction.*;

public class ItemOffsetTool extends Item {

    private static final int MAX_CONNECTED_BLOCKS = 512;

    public ItemOffsetTool(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockState clickedState = world.getBlockState(pos);
        Block clickedBlock = clickedState.getBlock();

        Set<BlockPos> connectedBlocks = findConnectedBlocks(world, pos, clickedBlock);

        Vec3 hitVec = context.getClickLocation();
        Direction moveDir = getMoveDir(context.getClickedFace(), hitVec.subtract(pos.getX(), pos.getY(), pos.getZ()));

        Map<ChunkPos, Set<BlockPos>> blocksByChunk = new HashMap<>();
        for (BlockPos blockPos : connectedBlocks) {
            ChunkPos chunkPos = new ChunkPos(blockPos);
            blocksByChunk.computeIfAbsent(chunkPos, k -> new HashSet<>()).add(blockPos);
        }

        for (Map.Entry<ChunkPos, Set<BlockPos>> entry : blocksByChunk.entrySet()) {
            ChunkPos chunkPos = entry.getKey();
            Set<BlockPos> blocksInChunk = entry.getValue();

            OffsetData data = ChunkData.getOrCreateData(world, chunkPos);
            data.moveAll(blocksInChunk, moveDir);

            LevelChunk chunk = world.getChunk(chunkPos.x, chunkPos.z);
            chunk.setUnsaved(true);

            if (world instanceof ServerLevel serverLevel) {
                CompoundTag tag = new CompoundTag();
                data.writeToNBT(tag);

                PacketDistributor.sendToPlayersTrackingChunk(
                        serverLevel,
                        chunkPos,
                        new ChunkDataPayload(chunkPos.x, chunkPos.z, tag)
                );
            }
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Finds all connected blocks of the same type using flood fill.
     */
    private Set<BlockPos> findConnectedBlocks(Level world, BlockPos start, Block targetBlock) {
        Set<BlockPos> connected = new HashSet<>();
        Queue<BlockPos> toCheck = new LinkedList<>();

        toCheck.add(start);
        connected.add(start);

        while (!toCheck.isEmpty() && connected.size() < MAX_CONNECTED_BLOCKS) {
            BlockPos current = toCheck.poll();

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);

                if (!connected.contains(neighbor)) {
                    BlockState neighborState = world.getBlockState(neighbor);
                    if (neighborState.getBlock() == targetBlock) {
                        connected.add(neighbor);
                        toCheck.add(neighbor);
                    }
                }
            }
        }

        return connected;
    }

    public Direction getMoveDir(Direction face, Vec3 hitVec) {
        Map<Double, Direction> map = Maps.newHashMap();
        if (face.getStepX() != 0) {
            fillMap(map, hitVec.z - (int) hitVec.z, hitVec.y - (int) hitVec.y, DOWN, UP, NORTH, SOUTH);
        } else if (face.getStepY() != 0) {
            fillMap(map, hitVec.x - (int) hitVec.x, hitVec.z - (int) hitVec.z, NORTH, SOUTH, WEST, EAST);
        } else if (face.getStepZ() != 0) {
            fillMap(map, hitVec.x - (int) hitVec.x, hitVec.y - (int) hitVec.y, DOWN, UP, WEST, EAST);
        }
        List<Double> keys = Lists.newArrayList(map.keySet());
        Collections.sort(keys);
        return map.get(keys.get(0));
    }

    private void fillMap(Map<Double, Direction> map, double x, double y, Direction... dirs) {
        map.put(Line2D.ptLineDistSq(0, 0, 1, 0, x, y), dirs[0]);
        map.put(Line2D.ptLineDistSq(0, 1, 1, 1, x, y), dirs[1]);
        map.put(Line2D.ptLineDistSq(0, 0, 0, 1, x, y), dirs[2]);
        map.put(Line2D.ptLineDistSq(1, 0, 1, 1, x, y), dirs[3]);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("chisel.tooltip.offset_tool.1")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("chisel.tooltip.offset_tool.2")
                .withStyle(ChatFormatting.GRAY));
    }
}
