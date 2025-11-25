package com.leclowndu93150.chisel.block;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ICarvable;
import com.leclowndu93150.chisel.api.block.VariationData;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

/**
 * Carvable bookshelf block.
 * Provides enchanting power like vanilla bookshelves.
 */
public class BlockCarvableBookshelf extends Block implements ICarvable {

    private final VariationData variation;
    private final String blockType;

    public BlockCarvableBookshelf(Properties properties, @Nullable VariationData variation, String blockType) {
        super(properties);
        this.variation = variation;
        this.blockType = blockType;
    }

    public BlockCarvableBookshelf(Properties properties, String blockType) {
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

    @Override
    public float getEnchantPowerBonus(BlockState state, LevelReader level, BlockPos pos) {
        return 1.0F;
    }
}
