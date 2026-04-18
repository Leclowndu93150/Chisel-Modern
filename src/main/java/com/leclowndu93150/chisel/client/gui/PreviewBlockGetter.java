package com.leclowndu93150.chisel.client.gui;

import net.minecraft.core.BlockPos;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.world.level.CardinalLighting;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

import org.jspecify.annotations.Nullable;
import java.util.HashSet;
import java.util.Set;

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
    public int getMinY() {
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos pos) {
        return null;
    }

    @Override
    public int getBlockTint(BlockPos pos, ColorResolver colorResolver) {
        return 0x7CBD6B;
    }

    @Override
    public CardinalLighting cardinalLighting() {
        return CardinalLighting.DEFAULT;
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return LevelLightEngine.EMPTY;
    }
}
