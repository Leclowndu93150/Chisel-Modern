package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EldritchProcessor implements ChiselQuadProcessor {

    private static final ThreadLocal<Random> THREAD_RANDOM = ThreadLocal.withInitial(Random::new);

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        Direction face = quad.getDirection();
        if (face == null) return List.of(quad);

        ChiselMutableQuad original = new ChiselMutableQuad();
        original.fillFromBakedQuad(quad);

        float midX = 0, midY = 0, midZ = 0, midU = 0, midV = 0;
        for (int i = 0; i < 4; i++) {
            midX += original.x(i);
            midY += original.y(i);
            midZ += original.z(i);
            midU += original.u(i);
            midV += original.v(i);
        }
        midX /= 4f; midY /= 4f; midZ /= 4f; midU /= 4f; midV /= 4f;

        float emU01 = (original.u(0) + original.u(1)) / 2f;
        float emV01 = (original.v(0) + original.v(1)) / 2f;
        float emX01 = (original.x(0) + original.x(1)) / 2f;
        float emY01 = (original.y(0) + original.y(1)) / 2f;
        float emZ01 = (original.z(0) + original.z(1)) / 2f;

        float emU12 = (original.u(1) + original.u(2)) / 2f;
        float emV12 = (original.v(1) + original.v(2)) / 2f;
        float emX12 = (original.x(1) + original.x(2)) / 2f;
        float emY12 = (original.y(1) + original.y(2)) / 2f;
        float emZ12 = (original.z(1) + original.z(2)) / 2f;

        float emU23 = (original.u(2) + original.u(3)) / 2f;
        float emV23 = (original.v(2) + original.v(3)) / 2f;
        float emX23 = (original.x(2) + original.x(3)) / 2f;
        float emY23 = (original.y(2) + original.y(3)) / 2f;
        float emZ23 = (original.z(2) + original.z(3)) / 2f;

        float emU30 = (original.u(3) + original.u(0)) / 2f;
        float emV30 = (original.v(3) + original.v(0)) / 2f;
        float emX30 = (original.x(3) + original.x(0)) / 2f;
        float emY30 = (original.y(3) + original.y(0)) / 2f;
        float emZ30 = (original.z(3) + original.z(0)) / 2f;

        Random rand = THREAD_RANDOM.get();
        rand.setSeed(Mth.getSeed(pos) + face.ordinal());
        float cOffU = midU + (float) (rand.nextGaussian() * 0.08);
        float cOffV = midV + (float) (rand.nextGaussian() * 0.08);

        List<BakedQuad> result = new ArrayList<>(4);
        result.add(buildSubQuad(quad,
                original.x(0), original.y(0), original.z(0), original.u(0), original.v(0),
                emX01, emY01, emZ01, emU01, emV01,
                midX, midY, midZ, cOffU, cOffV,
                emX30, emY30, emZ30, emU30, emV30));
        result.add(buildSubQuad(quad,
                emX01, emY01, emZ01, emU01, emV01,
                original.x(1), original.y(1), original.z(1), original.u(1), original.v(1),
                emX12, emY12, emZ12, emU12, emV12,
                midX, midY, midZ, cOffU, cOffV));
        result.add(buildSubQuad(quad,
                midX, midY, midZ, cOffU, cOffV,
                emX12, emY12, emZ12, emU12, emV12,
                original.x(2), original.y(2), original.z(2), original.u(2), original.v(2),
                emX23, emY23, emZ23, emU23, emV23));
        result.add(buildSubQuad(quad,
                emX30, emY30, emZ30, emU30, emV30,
                midX, midY, midZ, cOffU, cOffV,
                emX23, emY23, emZ23, emU23, emV23,
                original.x(3), original.y(3), original.z(3), original.u(3), original.v(3)));
        return result;
    }

    private BakedQuad buildSubQuad(BakedQuad source,
                                    float x0, float y0, float z0, float u0, float v0,
                                    float x1, float y1, float z1, float u1, float v1,
                                    float x2, float y2, float z2, float u2, float v2,
                                    float x3, float y3, float z3, float u3, float v3) {
        ChiselMutableQuad sub = new ChiselMutableQuad();
        sub.fillFromBakedQuad(source);
        sub.pos(0, x0, y0, z0); sub.uv(0, u0, v0);
        sub.pos(1, x1, y1, z1); sub.uv(1, u1, v1);
        sub.pos(2, x2, y2, z2); sub.uv(2, u2, v2);
        sub.pos(3, x3, y3, z3); sub.uv(3, u3, v3);
        return sub.toBakedQuad();
    }
}
