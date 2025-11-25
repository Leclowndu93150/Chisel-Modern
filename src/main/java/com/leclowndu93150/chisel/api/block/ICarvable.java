package com.leclowndu93150.chisel.api.block;

import net.minecraft.resources.ResourceLocation;

/**
 * Interface for blocks that can be chiseled between variants.
 */
public interface ICarvable {
    /**
     * Gets the variation data for this block.
     */
    VariationData getVariation();

    /**
     * Gets the carving group ID this block belongs to.
     */
    ResourceLocation getCarvingGroup();

    /**
     * Gets the block type name (e.g., "marble", "andesite").
     */
    String getBlockType();
}
