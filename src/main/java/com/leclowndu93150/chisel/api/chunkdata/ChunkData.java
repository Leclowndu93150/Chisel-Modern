package com.leclowndu93150.chisel.api.chunkdata;

import com.leclowndu93150.chisel.mixin.RenderChunkRegionAccessor;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkData {

    public static final String NBT_KEY = "chisel:offset_data";

    private static final IOffsetData DUMMY = pos -> BlockPos.ZERO;
    private static final Map<Long, OffsetData> serverDataCache = new ConcurrentHashMap<>();
    private static final Map<Long, OffsetData> clientDataCache = new ConcurrentHashMap<>();

    private static Map<Long, OffsetData> getCache(Level level) {
        return level.isClientSide() ? clientDataCache : serverDataCache;
    }

    public static IOffsetData getOffsetData(BlockAndTintGetter world, BlockPos pos) {
        if (world == null || pos == null) {
            return DUMMY;
        }

        Level level = null;

        if (world instanceof Level lvl) {
            level = lvl;
        } else if (world instanceof RenderChunkRegion region) {
            level = ((RenderChunkRegionAccessor) region).chisel$getLevel();
        }

        if (level == null) {
            return DUMMY;
        }

        ChunkPos chunkPos = new ChunkPos(pos);
        long key = getKey(chunkPos);

        OffsetData data = getCache(level).get(key);
        return data != null ? data : DUMMY;
    }

    public static OffsetData getOrCreateData(Level level, ChunkPos chunkPos) {
        long key = getKey(chunkPos);
        return getCache(level).computeIfAbsent(key, k -> new OffsetData());
    }

    public static OffsetData getData(Level level, ChunkPos chunkPos) {
        long key = getKey(chunkPos);
        return getCache(level).get(key);
    }

    public static void setData(Level level, ChunkPos chunkPos, OffsetData data) {
        long key = getKey(chunkPos);
        if (data == null || data.isEmpty()) {
            getCache(level).remove(key);
        } else {
            getCache(level).put(key, data);
        }
    }

    public static void removeData(Level level, ChunkPos chunkPos) {
        long key = getKey(chunkPos);
        getCache(level).remove(key);
    }

    public static void clearDataForLevel(Level level) {
        getCache(level).clear();
    }

    public static void clearAll() {
        serverDataCache.clear();
        clientDataCache.clear();
    }

    private static long getKey(ChunkPos pos) {
        return pos.toLong();
    }
}
