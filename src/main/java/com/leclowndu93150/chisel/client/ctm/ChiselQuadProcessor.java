package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface ChiselQuadProcessor {
    List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state);
}
