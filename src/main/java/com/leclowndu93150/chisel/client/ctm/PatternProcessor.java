package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;

public class PatternProcessor implements ChiselQuadProcessor {

    private final int size;

    public PatternProcessor(int size) {
        this.size = size;
    }

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        int tileX = Math.floorMod(pos.getX(), size);
        int tileY = Math.floorMod(pos.getY() + pos.getZ(), size);

        return remapToTile(quad, tileX, tileY, size, size);
    }

    static List<BakedQuad> remapToTile(BakedQuad quad, int tileX, int tileY, int cols, int rows) {
        TextureAtlasSprite sprite = quad.getSprite();

        float intervalU = 1f / cols;
        float intervalV = 1f / rows;
        float subMinU = intervalU * tileX;
        float subMinV = intervalV * tileY;

        ChiselMutableQuad mutable = new ChiselMutableQuad();
        mutable.fillFromBakedQuad(quad);

        for (int i = 0; i < 4; i++) {
            float normU = inverseLerp(sprite.getU0(), sprite.getU1(), mutable.u(i));
            float normV = inverseLerp(sprite.getV0(), sprite.getV1(), mutable.v(i));

            float mappedU = subMinU + normU * intervalU;
            float mappedV = subMinV + normV * intervalV;

            float atlasU = lerp(sprite.getU0(), sprite.getU1(), mappedU);
            float atlasV = lerp(sprite.getV0(), sprite.getV1(), mappedV);

            mutable.uv(i, atlasU, atlasV);
        }

        return Collections.singletonList(mutable.toBakedQuad());
    }

    static float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    static float inverseLerp(float a, float b, float x) {
        if (a == b) return 0.5f;
        return (x - a) / (b - a);
    }
}
