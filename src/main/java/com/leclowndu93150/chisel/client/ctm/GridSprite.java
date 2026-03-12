package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public class GridSprite extends TextureAtlasSprite {

    private final int columns;
    private final int rows;
    private final float tileWidth;
    private final float tileHeight;
    private final float startU;
    private final float startV;

    public GridSprite(TextureAtlasSprite original, int columns, int rows) {
        super(
            original.atlasLocation(),
            original.contents(),
            1,
            1,
            original.getX(),
            original.getY()
        );
        this.columns = columns;
        this.rows = rows;
        float fullU = original.getU1() - original.getU0();
        float fullV = original.getV1() - original.getV0();
        this.tileWidth = fullU / columns;
        this.tileHeight = fullV / rows;
        this.startU = original.getU0();
        this.startV = original.getV0();
        this.u0 = original.getU0();
        this.v0 = original.getV0();
        this.u1 = original.getU0() + this.tileWidth;
        this.v1 = original.getV0() + this.tileHeight;
    }

    public int getColumns() {
        return columns;
    }

    public int getRows() {
        return rows;
    }

    public float getTileWidth() {
        return tileWidth;
    }

    public float getTileHeight() {
        return tileHeight;
    }

    public float getStartU() {
        return startU;
    }

    public float getStartV() {
        return startV;
    }
}
