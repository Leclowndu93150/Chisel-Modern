package com.leclowndu93150.chisel.client.gui;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

/**
 * A fake BlockAndTintGetter for rendering block previews with connected textures.
 * Reports that all positions in the preview grid contain the same block,
 * allowing CTM mods to render connected textures properly.
 */
public class PreviewBlockGetter implements BlockAndTintGetter {

    private final BlockState previewState;
    private final Set<BlockPos> filledPositions;

    public PreviewBlockGetter(BlockState state, int[][] positions) {
        this.previewState = state;
        this.filledPositions = new HashSet<>();
        for (int[] pos : positions) {
            filledPositions.add(new BlockPos(pos[0], pos[1], pos[2]));
        }
    }

    @Override
    public BlockState getBlockState(BlockPos pos) {
        if (filledPositions.contains(pos)) {
            return previewState;
        }
        return Blocks.AIR.defaultBlockState();
    }

    @Override
    public FluidState getFluidState(BlockPos pos) {
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public int getHeight() {
        return 256;
    }

    @Override
    public int getMinBuildHeight() {
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public int getBrightness(LightLayer lightLayer, BlockPos pos) {
        return 15;
    }

    @Override
    public int getRawBrightness(BlockPos pos, int amount) {
        return 15;
    }

    @Override
    public boolean canSeeSky(BlockPos pos) {
        return true;
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
        return 0x7CBD6B;
    }

    @Override
    public float getShade(Direction direction, boolean shade) {
        if (!shade) {
            return 1.0F;
        }
        return switch (direction) {
            case DOWN -> 0.5F;
            case UP -> 1.0F;
            case NORTH, SOUTH -> 0.8F;
            case WEST, EAST -> 0.6F;
        };
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return null;
    }
}
