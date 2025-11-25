package com.leclowndu93150.chisel.block;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ICarvable;
import com.leclowndu93150.chisel.api.block.VariationData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CarpetBlock;

import javax.annotation.Nullable;

/**
 * Carvable carpet block.
 * Extends CarpetBlock for the thin carpet behavior.
 */
public class BlockCarvableCarpet extends CarpetBlock implements ICarvable {

    private final VariationData variation;
    private final String blockType;

    public BlockCarvableCarpet(Properties properties, @Nullable VariationData variation, String blockType) {
        super(properties);
        this.variation = variation;
        this.blockType = blockType;
    }

    public BlockCarvableCarpet(Properties properties, String blockType) {
        this(properties, null, blockType);
    }

    @Override
    @Nullable
    public VariationData getVariation() {
        return variation;
    }

    @Override
    public ResourceLocation getCarvingGroup() {
        return Chisel.id("carving/" + blockType);
    }

    @Override
    public String getBlockType() {
        return blockType;
    }
}
