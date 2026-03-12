package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;

public class PillarProcessor implements ChiselQuadProcessor {

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        Direction face = quad.getDirection();
        if (face == null || face == Direction.UP || face == Direction.DOWN) {
            return PatternProcessor.remapToTile(quad, 0, 0, 2, 2);
        }

        boolean connectedUp = level.getBlockState(pos.above()).getBlock() == state.getBlock();
        boolean connectedDown = level.getBlockState(pos.below()).getBlock() == state.getBlock();

        int tileX, tileY;
        if (connectedUp && connectedDown) {
            tileX = 0; tileY = 1;
        } else if (connectedUp) {
            tileX = 1; tileY = 1;
        } else if (connectedDown) {
            tileX = 1; tileY = 0;
        } else {
            tileX = 0; tileY = 0;
        }

        return PatternProcessor.remapToTile(quad, tileX, tileY, 2, 2);
    }
}
