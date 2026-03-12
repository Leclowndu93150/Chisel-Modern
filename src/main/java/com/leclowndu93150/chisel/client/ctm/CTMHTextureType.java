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

public class CTMHTextureType implements TextureType<CTMTextureData>, ChiselQuadProcessor {

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

        BlockPos leftPos = pos.offset(getLeftOffset(face));
        BlockPos rightPos = pos.offset(getRightOffset(face));

        boolean left = level.getBlockState(leftPos) == state;
        boolean right = level.getBlockState(rightPos) == state;

        int tileU = left ? 1 : 0;
        int tileV = (left == right) ? 0 : 1;

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

    private static BlockPos getLeftOffset(Direction face) {
        return switch (face) {
            case SOUTH -> new BlockPos(-1, 0, 0);
            case NORTH -> new BlockPos(1, 0, 0);
            case EAST -> new BlockPos(0, 0, -1);
            case WEST -> new BlockPos(0, 0, 1);
            case UP, DOWN -> new BlockPos(-1, 0, 0);
        };
    }

    private static BlockPos getRightOffset(Direction face) {
        return switch (face) {
            case SOUTH -> new BlockPos(1, 0, 0);
            case NORTH -> new BlockPos(-1, 0, 0);
            case EAST -> new BlockPos(0, 0, 1);
            case WEST -> new BlockPos(0, 0, -1);
            case UP, DOWN -> new BlockPos(1, 0, 0);
        };
    }
}
