package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class SCTMProcessor implements ChiselQuadProcessor {

    private final ResourceLocation targetSprite;

    public SCTMProcessor() {
        this(null);
    }

    public SCTMProcessor(ResourceLocation targetSprite) {
        this.targetSprite = targetSprite;
    }

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        Direction face = quad.getDirection();
        if (face == null) return PatternProcessor.remapToTile(quad, 0, 0, 2, 2, true, targetSprite);

        BlockPos topOffset = getTopOffset(face);
        BlockPos bottomOffset = getBottomOffset(face);
        BlockPos leftOffset = getLeftOffset(face);
        BlockPos rightOffset = getRightOffset(face);

        boolean top = level.getBlockState(pos.offset(topOffset)).getBlock() == state.getBlock();
        boolean bottom = level.getBlockState(pos.offset(bottomOffset)).getBlock() == state.getBlock();
        boolean left = level.getBlockState(pos.offset(leftOffset)).getBlock() == state.getBlock();
        boolean right = level.getBlockState(pos.offset(rightOffset)).getBlock() == state.getBlock();

        int tileU = 0;
        int tileV = 0;

        if (top || bottom || left || right) {
            if (!top || !bottom) {
                tileU = (left && right) ? 1 : 0;
            } else if (!left || !right) {
                tileV = 1;
            } else {
                boolean topLeft = level.getBlockState(pos.offset(topOffset).offset(leftOffset)).getBlock() == state.getBlock();
                boolean topRight = level.getBlockState(pos.offset(topOffset).offset(rightOffset)).getBlock() == state.getBlock();
                boolean bottomLeft = level.getBlockState(pos.offset(bottomOffset).offset(leftOffset)).getBlock() == state.getBlock();
                boolean bottomRight = level.getBlockState(pos.offset(bottomOffset).offset(rightOffset)).getBlock() == state.getBlock();

                if (topLeft && topRight && bottomLeft && bottomRight) {
                    tileU = 1;
                    tileV = 1;
                } else {
                    tileV = 1;
                }
            }
        }

        return PatternProcessor.remapToTile(quad, tileU, tileV, 2, 2, true, targetSprite);
    }

    private static BlockPos getTopOffset(Direction face) {
        return switch (face) {
            case UP, DOWN -> new BlockPos(0, 0, 1);
            default -> new BlockPos(0, 1, 0);
        };
    }

    private static BlockPos getBottomOffset(Direction face) {
        return switch (face) {
            case UP, DOWN -> new BlockPos(0, 0, -1);
            default -> new BlockPos(0, -1, 0);
        };
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
