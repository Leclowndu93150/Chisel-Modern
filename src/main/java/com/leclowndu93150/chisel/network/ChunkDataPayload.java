package com.leclowndu93150.chisel.network;

import com.leclowndu93150.chisel.Chisel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.Level;

/**
 * Payload for syncing chunk offset data to clients.
 */
public record ChunkDataPayload(ResourceKey<Level> dimension, int chunkX, int chunkZ, CompoundTag data) implements CustomPacketPayload {

    public static final Type<ChunkDataPayload> TYPE = new Type<>(Chisel.id("chunk_data"));

    public static final StreamCodec<FriendlyByteBuf, ChunkDataPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ChunkDataPayload decode(FriendlyByteBuf buf) {
            ResourceKey<Level> dimension = buf.readResourceKey(Registries.DIMENSION);
            int chunkX = buf.readInt();
            int chunkZ = buf.readInt();
            CompoundTag data = buf.readNbt();
            return new ChunkDataPayload(dimension, chunkX, chunkZ, data != null ? data : new CompoundTag());
        }

        @Override
        public void encode(FriendlyByteBuf buf, ChunkDataPayload payload) {
            buf.writeResourceKey(payload.dimension());
            buf.writeInt(payload.chunkX());
            buf.writeInt(payload.chunkZ());
            buf.writeNbt(payload.data());
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
