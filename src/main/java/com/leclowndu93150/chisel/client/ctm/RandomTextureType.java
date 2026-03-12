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

public class RandomTextureType implements TextureType<CTMTextureData>, ChiselQuadProcessor {

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
        int size = data.getSize();
        return new GridSprite(original, size, size);
    }

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        TextureAtlasSprite sprite = quad.getSprite();
        if (!(sprite instanceof GridSprite grid)) return Collections.singletonList(quad);

        int xSize = grid.getColumns();
        int ySize = grid.getRows();

        Random rand = threadRandom.get();
        rand.setSeed(Mth.getSeed(pos));
        int tileX = rand.nextInt(xSize);
        int tileY = rand.nextInt(ySize);

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
