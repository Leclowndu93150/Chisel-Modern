package com.leclowndu93150.chisel.event;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.chunkdata.ChunkData;
import com.leclowndu93150.chisel.api.chunkdata.OffsetData;
import com.leclowndu93150.chisel.network.client.ChunkDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import com.leclowndu93150.chisel.network.ChiselNetwork;
import net.minecraftforge.event.level.ChunkDataEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Chisel.MODID)
public class ChiselChunkEvents {

    @SubscribeEvent
    public static void onChunkSave(ChunkDataEvent.Save event) {
        ChunkAccess chunk = event.getChunk();
        if (!(event.getLevel() instanceof Level level)) return;
        if (level.isClientSide()) return;

        ChunkPos chunkPos = chunk.getPos();
        OffsetData data = ChunkData.getData(level, chunkPos);

        if (data != null && !data.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            data.writeToNBT(tag);
            event.getData().put(ChunkData.NBT_KEY, tag);
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkDataEvent.Load event) {
        ChunkAccess chunk = event.getChunk();
        Level level = (Level) event.getLevel();
        if (level == null || level.isClientSide()) return;

        CompoundTag tag = event.getData().getCompound(ChunkData.NBT_KEY);
        if (!tag.isEmpty()) {
            OffsetData data = new OffsetData();
            data.readFromNBT(tag);
            ChunkData.setData(level, chunk.getPos(), data);
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        ChunkAccess chunk = event.getChunk();
        Level level = (Level) event.getLevel();
        if (level == null) return;

        if (level.isClientSide()) {
            ChunkData.removeData(level, chunk.getPos());
        }
    }

    @SubscribeEvent
    public static void onChunkSentToClient(ChunkWatchEvent.Watch event) {
        // TODO not sure of this event (used to be Sent)
        ServerLevel level = event.getLevel();
        LevelChunk chunk = event.getChunk();
        ChunkPos chunkPos = chunk.getPos();

        OffsetData data = ChunkData.getData(level, chunkPos);
        if (data != null && !data.isEmpty()) {
            CompoundTag tag = new CompoundTag();
            data.writeToNBT(tag);

            ChiselNetwork.sendToPlayer(
                    event.getPlayer(),
                    new ChunkDataPacket(chunkPos.x, chunkPos.z, tag)
            );
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof Level level) {
            ChunkData.clearDataForLevel(level);
        }
    }
}
