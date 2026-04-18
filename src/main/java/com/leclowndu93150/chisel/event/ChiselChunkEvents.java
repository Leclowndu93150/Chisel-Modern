package com.leclowndu93150.chisel.event;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.chunkdata.ChunkData;
import com.leclowndu93150.chisel.api.chunkdata.OffsetData;
import com.leclowndu93150.chisel.network.ChunkDataPayload;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkWatchEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = Chisel.MODID)
public class ChiselChunkEvents {

    @SubscribeEvent
    public static void onChunkSentToClient(ChunkWatchEvent.Sent event) {
        ServerLevel level = event.getLevel();
        LevelChunk chunk = event.getChunk();
        ChunkPos chunkPos = chunk.getPos();

        OffsetData data = ChunkData.getData(level, chunkPos);
        if (data != null && !data.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            data.writeToNBT(tag);

            PacketDistributor.sendToPlayer(
                    event.getPlayer(),
                    new ChunkDataPayload(level.dimension(), chunkPos.x(), chunkPos.z(), tag)
            );
        }
    }
}
