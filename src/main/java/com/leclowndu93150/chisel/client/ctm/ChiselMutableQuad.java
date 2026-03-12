package com.leclowndu93150.chisel.client.ctm;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;

import java.util.Arrays;

public class ChiselMutableQuad {

    private static final int VERTEX_SIZE;
    private static final int UV_OFFSET;
    private static final int POSITION_OFFSET;

    static {
        VertexFormat format = DefaultVertexFormat.BLOCK;
        VERTEX_SIZE = format.getVertexSize() / 4;
        UV_OFFSET = format.getOffset(VertexFormatElement.UV) / 4;
        POSITION_OFFSET = format.getOffset(VertexFormatElement.POSITION) / 4;
    }

    private final int[] vertices;
    private int tintIndex;
    private Direction direction;
    private TextureAtlasSprite sprite;
    private boolean shade;
    private boolean hasAmbientOcclusion;

    public ChiselMutableQuad() {
        this.vertices = new int[VERTEX_SIZE * 4];
    }

    public void fillFromBakedQuad(BakedQuad quad) {
        System.arraycopy(quad.getVertices(), 0, this.vertices, 0, this.vertices.length);
        this.tintIndex = quad.getTintIndex();
        this.direction = quad.getDirection();
        this.sprite = quad.getSprite();
        this.shade = quad.isShade();
        this.hasAmbientOcclusion = quad.hasAmbientOcclusion();
    }

    public void uv(int vertexIndex, float u, float v) {
        int offset = vertexIndex * VERTEX_SIZE + UV_OFFSET;
        this.vertices[offset] = Float.floatToRawIntBits(u);
        this.vertices[offset + 1] = Float.floatToRawIntBits(v);
    }

    public float u(int vertexIndex) {
        return Float.intBitsToFloat(this.vertices[vertexIndex * VERTEX_SIZE + UV_OFFSET]);
    }

    public float v(int vertexIndex) {
        return Float.intBitsToFloat(this.vertices[vertexIndex * VERTEX_SIZE + UV_OFFSET + 1]);
    }

    public void pos(int vertexIndex, float x, float y, float z) {
        int offset = vertexIndex * VERTEX_SIZE + POSITION_OFFSET;
        this.vertices[offset] = Float.floatToRawIntBits(x);
        this.vertices[offset + 1] = Float.floatToRawIntBits(y);
        this.vertices[offset + 2] = Float.floatToRawIntBits(z);
    }

    public float x(int vertexIndex) {
        return Float.intBitsToFloat(this.vertices[vertexIndex * VERTEX_SIZE + POSITION_OFFSET]);
    }

    public float y(int vertexIndex) {
        return Float.intBitsToFloat(this.vertices[vertexIndex * VERTEX_SIZE + POSITION_OFFSET + 1]);
    }

    public float z(int vertexIndex) {
        return Float.intBitsToFloat(this.vertices[vertexIndex * VERTEX_SIZE + POSITION_OFFSET + 2]);
    }

    public void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    public BakedQuad toBakedQuad() {
        return new BakedQuad(Arrays.copyOf(this.vertices, this.vertices.length), this.tintIndex, this.direction, this.sprite, this.shade, this.hasAmbientOcclusion);
    }
}
