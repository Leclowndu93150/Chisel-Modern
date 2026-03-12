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
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class AlterRTextureType implements TextureType<CTMTextureData>, ChiselQuadProcessor {

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
        return Pair.of(context.getTextureWidth(), context.getTextureHeight());
    }

    @Override
    public TextureAtlasSprite createSprite(SpriteCreationContext context, CTMTextureData data) {
        TextureAtlasSprite original = context.createOriginalSprite();
        return new GridSprite(original, 2, 2);
    }

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        TextureAtlasSprite sprite = quad.getSprite();
        if (!(sprite instanceof GridSprite grid)) return Collections.singletonList(quad);

        Random rand = threadRandom.get();
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

        if (tileX == 0 && tileY == 0) return Collections.singletonList(quad);

        float offsetU = tileX * grid.getTileWidth();
        float offsetV = tileY * grid.getTileHeight();

        MutableQuad mutable = new MutableQuad();
        mutable.fillFromBakedQuad(quad);
        for (int i = 0; i < 4; i++) {
            mutable.uv(i, mutable.u(i) + offsetU, mutable.v(i) + offsetV);
        }
        return Collections.singletonList(mutable.toBakedQuad());
    }
}
