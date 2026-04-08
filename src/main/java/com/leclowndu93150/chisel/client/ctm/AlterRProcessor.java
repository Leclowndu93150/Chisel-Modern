package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Random;

public class AlterRProcessor implements ChiselQuadProcessor {

    private static final ThreadLocal<Random> THREAD_RANDOM = ThreadLocal.withInitial(Random::new);
    private final ResourceLocation targetSprite;

    public AlterRProcessor() {
        this(null);
    }

    public AlterRProcessor(ResourceLocation targetSprite) {
        this.targetSprite = targetSprite;
    }

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        Random rand = THREAD_RANDOM.get();
        rand.setSeed(Mth.getSeed(pos));
        rand.nextBoolean();
        int num = rand.nextInt(2) * 2;

        boolean type = true;
        if (pos.getX() % 2 == 0) type = !type;
        if (pos.getY() % 2 == 0) type = !type;
        if (pos.getZ() % 2 == 0) type = !type;
        num += type ? 0 : 1;

        int tileX = num % 2;
        int tileY = num / 2;

        return PatternProcessor.remapToTile(quad, tileX, tileY, 2, 2, false, targetSprite);
    }
}
