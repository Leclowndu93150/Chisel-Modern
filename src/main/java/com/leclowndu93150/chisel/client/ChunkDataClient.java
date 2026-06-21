package com.leclowndu93150.chisel.client;

import com.leclowndu93150.chisel.api.chunkdata.IOffsetData;
import com.leclowndu93150.chisel.init.ChiselRegistries;
import com.leclowndu93150.chisel.mixin.RenderChunkRegionAccessor;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.chunk.RenderSectionRegion;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;

public class ChunkDataClient {

    private static final IOffsetData DUMMY = pos -> BlockPos.ZERO;

    public static IOffsetData getOffsetData(BlockAndTintGetter world, BlockPos pos) {
        if (world == null || pos == null) {
            return DUMMY;
        }

        Level level = null;

        if (world instanceof Level lvl) {
            level = lvl;
        } else if (world instanceof RenderSectionRegion region) {
            level = ((RenderChunkRegionAccessor) region).chisel$getLevel();
        }

        if (level == null) {
            return DUMMY;
        }

        ChunkAccess chunk = level.getChunk(pos);
        if (chunk == null) {
            return DUMMY;
        }

        if (chunk.hasData(ChiselRegistries.OFFSET_DATA.get())) {
            return chunk.getData(ChiselRegistries.OFFSET_DATA.get());
        }

        return DUMMY;
    }
}
