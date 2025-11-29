package com.leclowndu93150.chisel.block;

import com.leclowndu93150.chisel.api.block.VariationData;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Glass block variant that properly culls adjacent faces of the same block type.
 * This makes glass blocks transparent like vanilla glass when placed next to each other.
 */
public class BlockCarvableGlass extends BlockCarvable {

    public BlockCarvableGlass(Properties properties, VariationData variation, String blockType) {
        super(properties, variation, blockType);
    }

    @Override
    public boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        return adjacentBlockState.is(this) || super.skipRendering(state, adjacentBlockState, side);
    }
}
