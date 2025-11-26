package com.leclowndu93150.chisel.network;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.chunkdata.ChunkData;
import com.leclowndu93150.chisel.api.chunkdata.OffsetData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Payload for syncing chunk offset data to clients.
 */
public record ChunkDataPayload(int chunkX, int chunkZ, CompoundTag data) implements CustomPacketPayload {

    public static final Type<ChunkDataPayload> TYPE = new Type<>(Chisel.id("chunk_data"));

    public static final StreamCodec<FriendlyByteBuf, ChunkDataPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ChunkDataPayload decode(FriendlyByteBuf buf) {
            int chunkX = buf.readInt();
            int chunkZ = buf.readInt();
            CompoundTag data = buf.readNbt();
            return new ChunkDataPayload(chunkX, chunkZ, data != null ? data : new CompoundTag());
        }

        @Override
        public void encode(FriendlyByteBuf buf, ChunkDataPayload payload) {
            buf.writeInt(payload.chunkX);
            buf.writeInt(payload.chunkZ);
            buf.writeNbt(payload.data);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.level != null) {
                ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);

                if (data.isEmpty()) {
                    ChunkData.removeData(mc.level, chunkPos);
                } else {
                    OffsetData offsetData = new OffsetData();
                    offsetData.readFromNBT(data);
                    ChunkData.setData(mc.level, chunkPos, offsetData);
                }

                // Force chunk section rebuilds - mark this chunk and all neighbors dirty
                int minSection = mc.level.getMinSection();
                int maxSection = mc.level.getMaxSection();

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        for (int sectionY = minSection; sectionY <= maxSection; sectionY++) {
                            mc.levelRenderer.setSectionDirty(chunkX + dx, sectionY, chunkZ + dz);
                        }
                    }
                }
            }
        });
    }
}
