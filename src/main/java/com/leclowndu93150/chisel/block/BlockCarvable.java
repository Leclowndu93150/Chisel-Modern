package com.leclowndu93150.chisel.block;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ICarvable;
import com.leclowndu93150.chisel.api.block.VariationData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

/**
 * Base block class for all chiselable blocks.
 */
public class BlockCarvable extends Block implements ICarvable {
    private final VariationData variation;
    private final String blockType;
    private final ResourceLocation carvingGroup;

    public BlockCarvable(Properties properties, VariationData variation, String blockType) {
        super(properties);
        this.variation = variation;
        this.blockType = blockType;
        this.carvingGroup = Chisel.id("carving/" + blockType);
    }

    @Override
    public VariationData getVariation() {
        return variation;
    }

    @Override
    public ResourceLocation getCarvingGroup() {
        return carvingGroup;
    }

    @Override
    public String getBlockType() {
        return blockType;
    }
}
