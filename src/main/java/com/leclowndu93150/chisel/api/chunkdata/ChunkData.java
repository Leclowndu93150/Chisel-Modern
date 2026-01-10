package com.leclowndu93150.chisel.api.chunkdata;

import com.leclowndu93150.chisel.mixin.RenderChunkRegionAccessor;
import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChunkData {

    public static final String NBT_KEY = "chisel:offset_data";

    private static final IOffsetData DUMMY = pos -> BlockPos.ZERO;
    private static final Map<LevelChunkKey, OffsetData> serverDataCache = new ConcurrentHashMap<>();
    private static final Map<LevelChunkKey, OffsetData> clientDataCache = new ConcurrentHashMap<>();

    private static Map<LevelChunkKey, OffsetData> getCache(Level level) {
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
        LevelChunkKey key = getKey(level, chunkPos);

        OffsetData data = getCache(level).get(key);
        return data != null ? data : DUMMY;
    }

    public static OffsetData getOrCreateData(Level level, ChunkPos chunkPos) {
        LevelChunkKey key = getKey(level, chunkPos);
        return getCache(level).computeIfAbsent(key, k -> new OffsetData());
    }

    public static OffsetData getData(Level level, ChunkPos chunkPos) {
        LevelChunkKey key = getKey(level, chunkPos);
        return getCache(level).get(key);
    }

    public static void setData(Level level, ChunkPos chunkPos, OffsetData data) {
        LevelChunkKey key = getKey(level, chunkPos);
        if (data == null || data.isEmpty()) {
            getCache(level).remove(key);
        } else {
            getCache(level).put(key, data);
        }
    }

    public static void removeData(Level level, ChunkPos chunkPos) {
        LevelChunkKey key = getKey(level, chunkPos);
        getCache(level).remove(key);
    }

    public static void clearDataForLevel(Level level) {
        Map<LevelChunkKey, OffsetData> cache = getCache(level);
        ResourceKey<Level> dimension = level.dimension();
        cache.keySet().removeIf(key -> key.dimension().equals(dimension));
    }

    public static void clearAll() {
        serverDataCache.clear();
        clientDataCache.clear();
    }

    private static LevelChunkKey getKey(Level level, ChunkPos pos) {
        return new LevelChunkKey(level.dimension(), pos.toLong());
    }

    private record LevelChunkKey(ResourceKey<Level> dimension, long chunkPos) {
    }
}
