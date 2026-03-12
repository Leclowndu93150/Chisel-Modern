package com.leclowndu93150.chisel.client.ctm;

import com.google.gson.JsonObject;
import com.supermartijn642.fusion.api.texture.SpriteCreationContext;
import com.supermartijn642.fusion.api.texture.SpritePreparationContext;
import com.supermartijn642.fusion.api.texture.TextureType;
import com.supermartijn642.fusion.api.util.Pair;
import com.supermartijn642.fusion.model.MutableQuad;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class EldritchTextureType implements TextureType<CTMTextureData>, ChiselQuadProcessor {

    private final ThreadLocal<Random> threadRandom = ThreadLocal.withInitial(Random::new);

    @Override
    public CTMTextureData deserialize(JsonObject json) {
        return CTMTextureData.fromJson(json);
    }

    @Override
    public JsonObject serialize(CTMTextureData data) {
        return new JsonObject();
    }

    @Override
    public Pair<Integer, Integer> getFrameSize(SpritePreparationContext context, CTMTextureData data) {
        return context.getOriginalFrameSize();
    }

    @Override
    public TextureAtlasSprite createSprite(SpriteCreationContext context, CTMTextureData data) {
        return context.createOriginalSprite();
    }

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        Direction face = quad.getDirection();
        if (face == null) return List.of(quad);

        MutableQuad original = new MutableQuad();
        original.fillFromBakedQuad(quad);

        float midX = 0, midY = 0, midZ = 0;
        float midU = 0, midV = 0;
        for (int i = 0; i < 4; i++) {
            midX += original.x(i);
            midY += original.y(i);
            midZ += original.z(i);
            midU += original.u(i);
            midV += original.v(i);
        }
        midX /= 4f;
        midY /= 4f;
        midZ /= 4f;
        midU /= 4f;
        midV /= 4f;

        float edgeMidU01 = (original.u(0) + original.u(1)) / 2f;
        float edgeMidV01 = (original.v(0) + original.v(1)) / 2f;
        float edgeMidX01 = (original.x(0) + original.x(1)) / 2f;
        float edgeMidY01 = (original.y(0) + original.y(1)) / 2f;
        float edgeMidZ01 = (original.z(0) + original.z(1)) / 2f;

        float edgeMidU12 = (original.u(1) + original.u(2)) / 2f;
        float edgeMidV12 = (original.v(1) + original.v(2)) / 2f;
        float edgeMidX12 = (original.x(1) + original.x(2)) / 2f;
        float edgeMidY12 = (original.y(1) + original.y(2)) / 2f;
        float edgeMidZ12 = (original.z(1) + original.z(2)) / 2f;

        float edgeMidU23 = (original.u(2) + original.u(3)) / 2f;
        float edgeMidV23 = (original.v(2) + original.v(3)) / 2f;
        float edgeMidX23 = (original.x(2) + original.x(3)) / 2f;
        float edgeMidY23 = (original.y(2) + original.y(3)) / 2f;
        float edgeMidZ23 = (original.z(2) + original.z(3)) / 2f;

        float edgeMidU30 = (original.u(3) + original.u(0)) / 2f;
        float edgeMidV30 = (original.v(3) + original.v(0)) / 2f;
        float edgeMidX30 = (original.x(3) + original.x(0)) / 2f;
        float edgeMidY30 = (original.y(3) + original.y(0)) / 2f;
        float edgeMidZ30 = (original.z(3) + original.z(0)) / 2f;

        Random rand = threadRandom.get();
        rand.setSeed(Mth.getSeed(pos) + face.ordinal());
        float centerOffsetU = (float) (rand.nextGaussian() * 0.08);
        float centerOffsetV = (float) (rand.nextGaussian() * 0.08);
        float offsetCenterU = midU + centerOffsetU;
        float offsetCenterV = midV + centerOffsetV;

        List<BakedQuad> result = new ArrayList<>(4);

        result.add(buildSubQuad(original, 0,
                original.x(0), original.y(0), original.z(0), original.u(0), original.v(0),
                edgeMidX01, edgeMidY01, edgeMidZ01, edgeMidU01, edgeMidV01,
                midX, midY, midZ, offsetCenterU, offsetCenterV,
                edgeMidX30, edgeMidY30, edgeMidZ30, edgeMidU30, edgeMidV30));

        result.add(buildSubQuad(original, 1,
                edgeMidX01, edgeMidY01, edgeMidZ01, edgeMidU01, edgeMidV01,
                original.x(1), original.y(1), original.z(1), original.u(1), original.v(1),
                edgeMidX12, edgeMidY12, edgeMidZ12, edgeMidU12, edgeMidV12,
                midX, midY, midZ, offsetCenterU, offsetCenterV));

        result.add(buildSubQuad(original, 2,
                midX, midY, midZ, offsetCenterU, offsetCenterV,
                edgeMidX12, edgeMidY12, edgeMidZ12, edgeMidU12, edgeMidV12,
                original.x(2), original.y(2), original.z(2), original.u(2), original.v(2),
                edgeMidX23, edgeMidY23, edgeMidZ23, edgeMidU23, edgeMidV23));

        result.add(buildSubQuad(original, 3,
                edgeMidX30, edgeMidY30, edgeMidZ30, edgeMidU30, edgeMidV30,
                midX, midY, midZ, offsetCenterU, offsetCenterV,
                edgeMidX23, edgeMidY23, edgeMidZ23, edgeMidU23, edgeMidV23,
                original.x(3), original.y(3), original.z(3), original.u(3), original.v(3)));

        return result;
    }

    private BakedQuad buildSubQuad(MutableQuad source, int cornerIndex,
                                   float x0, float y0, float z0, float u0, float v0,
                                   float x1, float y1, float z1, float u1, float v1,
                                   float x2, float y2, float z2, float u2, float v2,
                                   float x3, float y3, float z3, float u3, float v3) {
        MutableQuad sub = new MutableQuad();
        sub.fillFromBakedQuad(source.toBakedQuad());

        sub.pos(0, x0, y0, z0);
        sub.uv(0, u0, v0);
        sub.pos(1, x1, y1, z1);
        sub.uv(1, u1, v1);
        sub.pos(2, x2, y2, z2);
        sub.uv(2, u2, v2);
        sub.pos(3, x3, y3, z3);
        sub.uv(3, u3, v3);

        return sub.toBakedQuad();
    }
}
