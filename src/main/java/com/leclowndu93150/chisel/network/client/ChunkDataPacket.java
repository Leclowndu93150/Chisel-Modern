package com.leclowndu93150.chisel.network.client;

import com.leclowndu93150.chisel.api.chunkdata.ChunkData;
import com.leclowndu93150.chisel.api.chunkdata.OffsetData;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet for syncing chunk offset data to clients.
 */
public class ChunkDataPacket {

    private final int chunkX;
    private final int chunkZ;
    private final CompoundTag data;

    public ChunkDataPacket(int chunkX, int chunkZ, CompoundTag data) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.data = data != null ? data : new CompoundTag();
    }

    public static void encode(ChunkDataPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.chunkX);
        buf.writeInt(packet.chunkZ);
        buf.writeNbt(packet.data);
    }

    public static ChunkDataPacket decode(FriendlyByteBuf buf) {
        int chunkX = buf.readInt();
        int chunkZ = buf.readInt();
        CompoundTag data = buf.readNbt();
        return new ChunkDataPacket(chunkX, chunkZ, data);
    }

    @OnlyIn(Dist.CLIENT)
    public static void handleClient(ChunkDataPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.level != null) {
                ChunkPos chunkPos = new ChunkPos(packet.chunkX, packet.chunkZ);

                if (packet.data.isEmpty()) {
                    ChunkData.removeData(mc.level, chunkPos);
                } else {
                    OffsetData offsetData = new OffsetData();
                    offsetData.readFromNBT(packet.data);
                    ChunkData.setData(mc.level, chunkPos, offsetData);
                }

                int minSection = mc.level.getMinSection();
                int maxSection = mc.level.getMaxSection();

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        for (int sectionY = minSection; sectionY <= maxSection; sectionY++) {
                            mc.levelRenderer.setSectionDirty(packet.chunkX + dx, sectionY, packet.chunkZ + dz);
                        }
                    }
                }
            }
        });
    }
}
