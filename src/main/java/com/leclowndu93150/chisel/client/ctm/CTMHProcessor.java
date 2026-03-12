package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class CTMHProcessor implements ChiselQuadProcessor {

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        Direction face = quad.getDirection();
        if (face == null) return PatternProcessor.remapToTile(quad, 0, 0, 2, 2);

        BlockPos leftPos = pos.offset(getLeftOffset(face));
        BlockPos rightPos = pos.offset(getRightOffset(face));

        boolean left = level.getBlockState(leftPos).getBlock() == state.getBlock();
        boolean right = level.getBlockState(rightPos).getBlock() == state.getBlock();

        int tileX = left ? 1 : 0;
        int tileV = (left == right) ? 0 : 1;

        return PatternProcessor.remapToTile(quad, tileX, tileV, 2, 2);
    }

    private static BlockPos getLeftOffset(Direction face) {
        return switch (face) {
            case SOUTH -> new BlockPos(-1, 0, 0);
            case NORTH -> new BlockPos(1, 0, 0);
            case EAST -> new BlockPos(0, 0, -1);
            case WEST -> new BlockPos(0, 0, 1);
            case UP, DOWN -> new BlockPos(-1, 0, 0);
        };
    }

    private static BlockPos getRightOffset(Direction face) {
        return switch (face) {
            case SOUTH -> new BlockPos(1, 0, 0);
            case NORTH -> new BlockPos(-1, 0, 0);
            case EAST -> new BlockPos(0, 0, 1);
            case WEST -> new BlockPos(0, 0, -1);
            case UP, DOWN -> new BlockPos(1, 0, 0);
        };
    }
}
