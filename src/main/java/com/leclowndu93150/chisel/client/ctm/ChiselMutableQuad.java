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

    public ChiselMutableQuad copy() {
        ChiselMutableQuad copy = new ChiselMutableQuad();
        System.arraycopy(this.vertices, 0, copy.vertices, 0, this.vertices.length);
        copy.tintIndex = this.tintIndex;
        copy.direction = this.direction;
        copy.sprite = this.sprite;
        copy.shade = this.shade;
        copy.hasAmbientOcclusion = this.hasAmbientOcclusion;
        return copy;
    }

    public void derotate() {
        float minU = Float.POSITIVE_INFINITY;
        float minV = Float.POSITIVE_INFINITY;
        for (int i = 0; i < 4; i++) {
            minU = Math.min(minU, u(i));
            minV = Math.min(minV, v(i));
        }

        int start = 0;
        for (int i = 0; i < 4; i++) {
            if (u(i) <= minU && v(i) <= minV) {
                start = i;
                break;
            }
        }
        if (start == 0) {
            return;
        }

        float[] us = new float[4];
        float[] vs = new float[4];
        for (int i = 0; i < 4; i++) {
            int src = (i + start) % 4;
            us[i] = u(src);
            vs[i] = v(src);
        }
        for (int i = 0; i < 4; i++) {
            uv(i, us[i], vs[i]);
        }
    }

    public ChiselMutableQuad subsect(float xOffset, float yOffset, float width, float height) {
        ChiselMutableQuad subQuad = copy();
        if (direction == null) {
            return subQuad;
        }

        float[][] xy = new float[4][2];
        float[][] newXy = new float[4][2];
        float[][] uvs = new float[4][2];

        for (int i = 0; i < 4; i++) {
            switch (direction.getAxis()) {
                case Y -> {
                    xy[i][0] = x(i);
                    xy[i][1] = z(i);
                }
                case Z -> {
                    xy[i][0] = x(i);
                    xy[i][1] = y(i);
                }
                case X -> {
                    xy[i][0] = z(i);
                    xy[i][1] = y(i);
                }
            }
            uvs[i][0] = u(i);
            uvs[i][1] = v(i);
        }

        if (direction.getAxis() != Direction.Axis.Y) {
            yOffset = 1f - (yOffset + height);
        }
        if (direction == Direction.EAST || direction == Direction.NORTH) {
            xOffset = 1f - (xOffset + width);
        }

        if (direction.getAxis() == Direction.Axis.Y || direction == Direction.SOUTH || direction == Direction.WEST) {
            newXy[0][0] = Math.max(xy[0][0], xOffset);
            newXy[1][0] = Math.max(xy[1][0], xOffset);
            newXy[2][0] = Math.min(xy[2][0], xOffset + width);
            newXy[3][0] = Math.min(xy[3][0], xOffset + width);
        } else {
            newXy[0][0] = Math.min(xy[0][0], xOffset + width);
            newXy[1][0] = Math.min(xy[1][0], xOffset + width);
            newXy[2][0] = Math.max(xy[2][0], xOffset);
            newXy[3][0] = Math.max(xy[3][0], xOffset);
        }

        if (direction != Direction.UP) {
            newXy[0][1] = Math.min(xy[0][1], yOffset + height);
            newXy[1][1] = Math.max(xy[1][1], yOffset);
            newXy[2][1] = Math.max(xy[2][1], yOffset);
            newXy[3][1] = Math.min(xy[3][1], yOffset + height);
        } else {
            newXy[0][1] = Math.max(xy[0][1], yOffset);
            newXy[1][1] = Math.min(xy[1][1], yOffset + height);
            newXy[2][1] = Math.min(xy[2][1], yOffset + height);
            newXy[3][1] = Math.max(xy[3][1], yOffset);
        }

        float u0Interp = inverseLerp(xy[0][0], xy[3][0], newXy[0][0]);
        float v0Interp = inverseLerp(xy[0][1], xy[1][1], newXy[0][1]);
        float u1Interp = inverseLerp(xy[1][0], xy[2][0], newXy[1][0]);
        float v1Interp = inverseLerp(xy[1][1], xy[0][1], newXy[1][1]);
        float u2Interp = inverseLerp(xy[2][0], xy[1][0], newXy[2][0]);
        float v2Interp = inverseLerp(xy[2][1], xy[3][1], newXy[2][1]);
        float u3Interp = inverseLerp(xy[3][0], xy[0][0], newXy[3][0]);
        float v3Interp = inverseLerp(xy[3][1], xy[2][1], newXy[3][1]);

        subQuad.uv(0, lerp(uvs[0][0], uvs[3][0], u0Interp), lerp(uvs[0][1], uvs[1][1], v0Interp));
        subQuad.uv(1, lerp(uvs[1][0], uvs[2][0], u1Interp), lerp(uvs[1][1], uvs[0][1], v1Interp));
        subQuad.uv(2, lerp(uvs[2][0], uvs[1][0], u2Interp), lerp(uvs[2][1], uvs[3][1], v2Interp));
        subQuad.uv(3, lerp(uvs[3][0], uvs[0][0], u3Interp), lerp(uvs[3][1], uvs[2][1], v3Interp));

        for (int i = 0; i < 4; i++) {
            switch (direction.getAxis()) {
                case Y -> subQuad.pos(i, newXy[i][0], y(i), newXy[i][1]);
                case Z -> subQuad.pos(i, newXy[i][0], newXy[i][1], z(i));
                case X -> subQuad.pos(i, x(i), newXy[i][1], newXy[i][0]);
            }
        }

        return subQuad;
    }

    public void normalizeQuadrant() {
        if (sprite == null) {
            return;
        }

        int quadrant = getQuadrant();
        float minU = quadrant == 1 || quadrant == 2 ? 0.5f : 0f;
        float minV = quadrant < 2 ? 0.5f : 0f;
        float maxU = quadrant == 0 || quadrant == 3 ? 0.5f : 1f;
        float maxV = quadrant > 1 ? 0.5f : 1f;

        for (int i = 0; i < 4; i++) {
            float normalizedU = inverseLerp(sprite.getU0(), sprite.getU1(), u(i));
            float normalizedV = inverseLerp(sprite.getV0(), sprite.getV1(), v(i));
            float expandedU = inverseLerp(minU, maxU, normalizedU);
            float expandedV = inverseLerp(minV, maxV, normalizedV);
            uv(i, lerp(sprite.getU0(), sprite.getU1(), expandedU), lerp(sprite.getV0(), sprite.getV1(), expandedV));
        }
    }

    public void transformUVs(TextureAtlasSprite targetSprite, float xOffset, float yOffset, float width, float height) {
        if (sprite == null || targetSprite == null) {
            return;
        }

        TextureAtlasSprite sourceSprite = this.sprite;
        for (int i = 0; i < 4; i++) {
            float normalizedU = inverseLerp(sourceSprite.getU0(), sourceSprite.getU1(), u(i));
            float normalizedV = inverseLerp(sourceSprite.getV0(), sourceSprite.getV1(), v(i));
            float mappedU = xOffset + (normalizedU * width);
            float mappedV = yOffset + (normalizedV * height);
            uv(i, lerp(targetSprite.getU0(), targetSprite.getU1(), mappedU), lerp(targetSprite.getV0(), targetSprite.getV1(), mappedV));
        }
        this.sprite = targetSprite;
    }

    public int getQuadrant() {
        if (sprite == null) {
            return 0;
        }

        float maxNormalizedU = 0f;
        float maxNormalizedV = 0f;
        for (int i = 0; i < 4; i++) {
            maxNormalizedU = Math.max(maxNormalizedU, inverseLerp(sprite.getU0(), sprite.getU1(), u(i)));
            maxNormalizedV = Math.max(maxNormalizedV, inverseLerp(sprite.getV0(), sprite.getV1(), v(i)));
        }

        if (maxNormalizedU <= 0.5f) {
            return maxNormalizedV <= 0.5f ? 3 : 0;
        }
        return maxNormalizedV <= 0.5f ? 2 : 1;
    }

    public void setSprite(TextureAtlasSprite sprite) {
        this.sprite = sprite;
    }

    public BakedQuad toBakedQuad() {
        return new BakedQuad(Arrays.copyOf(this.vertices, this.vertices.length), this.tintIndex, this.direction, this.sprite, this.shade, this.hasAmbientOcclusion);
    }

    private static float lerp(float a, float b, float t) {
        return a + ((b - a) * t);
    }

    private static float inverseLerp(float min, float max, float value) {
        if (min == max) {
            return 0.5f;
        }
        return (value - min) / (max - min);
    }
}
