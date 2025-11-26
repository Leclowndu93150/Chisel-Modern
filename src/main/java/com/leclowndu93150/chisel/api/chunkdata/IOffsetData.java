package com.leclowndu93150.chisel.api.chunkdata;

import net.minecraft.core.BlockPos;

/**
 * Interface for accessing block offset data.
 */
public interface IOffsetData {

    /**
     * Gets the offset for a specific block position.
     * @param pos The block position to get the offset for
     * @return The offset, or BlockPos.ZERO if none set
     */
    BlockPos getOffset(BlockPos pos);
}
