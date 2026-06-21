package com.leclowndu93150.chisel.api.chunkdata;

import com.leclowndu93150.chisel.init.ChiselRegistries;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ChunkData {

    public static final String NBT_KEY = "chisel:offset_data";

    public static OffsetData getOrCreateData(Level level, ChunkPos chunkPos) {
        ChunkAccess chunk = level.getChunk(chunkPos.x(), chunkPos.z());
        return chunk.getData(ChiselRegistries.OFFSET_DATA.get());
    }

    public static OffsetData getData(Level level, ChunkPos chunkPos) {
        ChunkAccess chunk = level.getChunk(chunkPos.x(), chunkPos.z());
        if (chunk.hasData(ChiselRegistries.OFFSET_DATA.get())) {
            return chunk.getData(ChiselRegistries.OFFSET_DATA.get());
        }
        return null;
    }

    public static void setData(Level level, ChunkPos chunkPos, OffsetData data) {
        ChunkAccess chunk = level.getChunk(chunkPos.x(), chunkPos.z());
        if (data == null || data.isEmpty()) {
            chunk.removeData(ChiselRegistries.OFFSET_DATA.get());
        } else {
            chunk.setData(ChiselRegistries.OFFSET_DATA.get(), data);
        }
    }

    public static void removeData(Level level, ChunkPos chunkPos) {
        ChunkAccess chunk = level.getChunk(chunkPos.x(), chunkPos.z());
        chunk.removeData(ChiselRegistries.OFFSET_DATA.get());
    }
}
