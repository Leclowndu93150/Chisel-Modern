package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Random;

public class RandomProcessor implements ChiselQuadProcessor {

    private static final ThreadLocal<Random> THREAD_RANDOM = ThreadLocal.withInitial(Random::new);
    private final int size;

    public RandomProcessor(int size) {
        this.size = size;
    }

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        Random rand = THREAD_RANDOM.get();
        rand.setSeed(Mth.getSeed(pos));
        int tileX = rand.nextInt(size);
        int tileY = rand.nextInt(size);

        return PatternProcessor.remapToTile(quad, tileX, tileY, size, size);
    }
}
