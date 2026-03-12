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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;

public class PillarTextureType implements TextureType<CTMTextureData>, ChiselQuadProcessor {

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

        Direction face = quad.getDirection();
        if (face == null) return Collections.singletonList(quad);

        if (face == Direction.UP || face == Direction.DOWN) {
            return Collections.singletonList(quad);
        }

        boolean connectedUp = level.getBlockState(pos.above()) == state;
        boolean connectedDown = level.getBlockState(pos.below()) == state;

        int tileU;
        int tileV;

        if (connectedUp && connectedDown) {
            tileU = 0;
            tileV = 1;
        } else if (connectedUp) {
            tileU = 1;
            tileV = 1;
        } else if (connectedDown) {
            tileU = 1;
            tileV = 0;
        } else {
            tileU = 0;
            tileV = 0;
        }

        if (tileU == 0 && tileV == 0) return Collections.singletonList(quad);

        float offsetU = tileU * grid.getTileWidth();
        float offsetV = tileV * grid.getTileHeight();

        MutableQuad mutable = new MutableQuad();
        mutable.fillFromBakedQuad(quad);
        for (int i = 0; i < 4; i++) {
            mutable.uv(i, mutable.u(i) + offsetU, mutable.v(i) + offsetV);
        }
        return Collections.singletonList(mutable.toBakedQuad());
    }
}
