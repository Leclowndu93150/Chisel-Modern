package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collections;
import java.util.List;

public class PatternProcessor implements ChiselQuadProcessor {

    private final int size;
    private final ResourceLocation targetSprite;

    public PatternProcessor(int size) {
        this(size, null);
    }

    public PatternProcessor(int size, ResourceLocation targetSprite) {
        this.size = size;
        this.targetSprite = targetSprite;
    }

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        int tileX = Math.floorMod(pos.getX(), size);
        int tileY = Math.floorMod(pos.getY() + pos.getZ(), size);

        return remapToTile(quad, tileX, tileY, size, size, false, targetSprite);
    }

    static List<BakedQuad> remapToTile(BakedQuad quad, int tileX, int tileY, int cols, int rows) {
        return remapToTile(quad, tileX, tileY, cols, rows, false, null);
    }

    static List<BakedQuad> remapToTile(BakedQuad quad, int tileX, int tileY, int cols, int rows, boolean derotate) {
        return remapToTile(quad, tileX, tileY, cols, rows, derotate, null);
    }

    static List<BakedQuad> remapToTile(BakedQuad quad, int tileX, int tileY, int cols, int rows, boolean derotate, ResourceLocation targetSprite) {
        TextureAtlasSprite sprite = targetSprite != null ? ChiselSpriteResolver.resolve(targetSprite) : quad.getSprite();
        if (sprite == null) {
            sprite = quad.getSprite();
        }
        float intervalU = 1f / cols;
        float intervalV = 1f / rows;

        ChiselMutableQuad mutable = new ChiselMutableQuad();
        mutable.fillFromBakedQuad(quad);
        if (derotate) {
            mutable.derotate();
        }
        mutable.transformUVs(sprite, intervalU * tileX, intervalV * tileY, intervalU, intervalV);

        return Collections.singletonList(mutable.toBakedQuad());
    }
}
