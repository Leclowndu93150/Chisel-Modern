package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CTMFullProcessor implements ChiselQuadProcessor {

    private static final int DIR_TOP = 0;
    private static final int DIR_TOP_RIGHT = 1;
    private static final int DIR_RIGHT = 2;
    private static final int DIR_BOTTOM_RIGHT = 3;
    private static final int DIR_BOTTOM = 4;
    private static final int DIR_BOTTOM_LEFT = 5;
    private static final int DIR_LEFT = 6;
    private static final int DIR_TOP_LEFT = 7;

    private static final BlockPos[][] DIR_OFFSETS = new BlockPos[6][8];

    static {
        DIR_OFFSETS[Direction.SOUTH.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 1, 0), new BlockPos(1, 1, 0), new BlockPos(1, 0, 0), new BlockPos(1, -1, 0),
            new BlockPos(0, -1, 0), new BlockPos(-1, -1, 0), new BlockPos(-1, 0, 0), new BlockPos(-1, 1, 0)
        };
        DIR_OFFSETS[Direction.NORTH.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 1, 0), new BlockPos(-1, 1, 0), new BlockPos(-1, 0, 0), new BlockPos(-1, -1, 0),
            new BlockPos(0, -1, 0), new BlockPos(1, -1, 0), new BlockPos(1, 0, 0), new BlockPos(1, 1, 0)
        };
        DIR_OFFSETS[Direction.EAST.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 1, 0), new BlockPos(0, 1, -1), new BlockPos(0, 0, -1), new BlockPos(0, -1, -1),
            new BlockPos(0, -1, 0), new BlockPos(0, -1, 1), new BlockPos(0, 0, 1), new BlockPos(0, 1, 1)
        };
        DIR_OFFSETS[Direction.WEST.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 1, 0), new BlockPos(0, 1, 1), new BlockPos(0, 0, 1), new BlockPos(0, -1, 1),
            new BlockPos(0, -1, 0), new BlockPos(0, -1, -1), new BlockPos(0, 0, -1), new BlockPos(0, 1, -1)
        };
        DIR_OFFSETS[Direction.UP.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 0, -1), new BlockPos(1, 0, -1), new BlockPos(1, 0, 0), new BlockPos(1, 0, 1),
            new BlockPos(0, 0, 1), new BlockPos(-1, 0, 1), new BlockPos(-1, 0, 0), new BlockPos(-1, 0, -1)
        };
        DIR_OFFSETS[Direction.DOWN.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 0, 1), new BlockPos(1, 0, 1), new BlockPos(1, 0, 0), new BlockPos(1, 0, -1),
            new BlockPos(0, 0, -1), new BlockPos(-1, 0, -1), new BlockPos(-1, 0, 0), new BlockPos(-1, 0, 1)
        };
    }

    private static final int[] SUBMAP_OFFSETS = {4, 5, 1, 0};

    private static final int[][] SUBMAP_MAP = {
        {DIR_BOTTOM, DIR_LEFT, DIR_BOTTOM_LEFT},
        {DIR_BOTTOM, DIR_RIGHT, DIR_BOTTOM_RIGHT},
        {DIR_TOP, DIR_RIGHT, DIR_TOP_RIGHT},
        {DIR_TOP, DIR_LEFT, DIR_TOP_LEFT}
    };

    private static final float[][] SUBMAP_UV_PIXEL = new float[20][];

    static {
        for (int i = 0; i < 16; i++) {
            int col = i % 4;
            int row = i / 4;
            SUBMAP_UV_PIXEL[i] = new float[]{ col * 4f, row * 4f, 4f, 4f };
        }
        SUBMAP_UV_PIXEL[16] = new float[]{ 0f, 0f, 8f, 8f };
        SUBMAP_UV_PIXEL[17] = new float[]{ 8f, 0f, 8f, 8f };
        SUBMAP_UV_PIXEL[18] = new float[]{ 0f, 8f, 8f, 8f };
        SUBMAP_UV_PIXEL[19] = new float[]{ 8f, 8f, 8f, 8f };
    }

    private final TextureAtlasSprite ctmSheet;

    public CTMFullProcessor(TextureAtlasSprite ctmSheet) {
        this.ctmSheet = ctmSheet;
    }

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        Direction face = quad.getDirection();
        if (face == null || level == null) {
            return Collections.singletonList(quad);
        }

        byte connectionMap = buildConnectionMap(level, pos, state, face);

        int[] submapCache = {18, 19, 17, 16};
        for (int i = 0; i < 4; i++) {
            fillSubmaps(submapCache, connectionMap, i);
        }

        return subdivideAndRemap(quad, submapCache, face);
    }

    private byte buildConnectionMap(BlockAndTintGetter level, BlockPos pos, BlockState state, Direction face) {
        byte map = 0;
        BlockPos[] offsets = DIR_OFFSETS[face.get3DDataValue()];
        for (int i = 0; i < 8; i++) {
            BlockPos neighbor = pos.offset(offsets[i]);
            if (state.getBlock() == level.getBlockState(neighbor).getBlock()) {
                map |= (byte) (1 << i);
            }
        }
        return map;
    }

    private void fillSubmaps(int[] submapCache, byte connectionMap, int idx) {
        int[] dirs = SUBMAP_MAP[idx];
        boolean cardinal0 = ((connectionMap >> dirs[0]) & 1) == 1;
        boolean cardinal1 = ((connectionMap >> dirs[1]) & 1) == 1;
        boolean diagonal = ((connectionMap >> dirs[2]) & 1) == 1;

        if (cardinal0 || cardinal1) {
            if (cardinal0 && cardinal1 && diagonal) {
                submapCache[idx] = SUBMAP_OFFSETS[idx];
            } else {
                submapCache[idx] = SUBMAP_OFFSETS[idx] + (cardinal0 ? 2 : 0) + (cardinal1 ? 8 : 0);
            }
        }
    }

    private List<BakedQuad> subdivideAndRemap(BakedQuad original, int[] submapCache, Direction face) {
        TextureAtlasSprite baseSprite = original.getSprite();
        ChiselMutableQuad reader = new ChiselMutableQuad();
        reader.fillFromBakedQuad(original);

        float[] pu = new float[4], pv = new float[4];
        for (int i = 0; i < 4; i++) {
            pu[i] = reader.u(i);
            pv[i] = reader.v(i);
        }

        Vec2[] normalized = new Vec2[4];
        for (int i = 0; i < 4; i++) {
            normalized[i] = new Vec2(
                inverseLerp(baseSprite.getU0(), baseSprite.getU1(), pu[i]),
                inverseLerp(baseSprite.getV0(), baseSprite.getV1(), pv[i])
            );
        }

        int firstIndex = 0;
        for (int i = 0; i < 4; i++) {
            if (normalized[i].x <= normalized[(i + 1) % 4].x && normalized[i].x <= normalized[(i + 3) % 4].x &&
                normalized[i].y <= normalized[(i + 1) % 4].y && normalized[i].y <= normalized[(i + 3) % 4].y) {
                firstIndex = i;
                break;
            }
        }

        float[] nx = new float[4], ny = new float[4], nz = new float[4];
        float[] nu = new float[4], nv = new float[4];
        for (int i = 0; i < 4; i++) {
            int idx = (firstIndex + i) % 4;
            nx[i] = reader.x(idx);
            ny[i] = reader.y(idx);
            nz[i] = reader.z(idx);
            nu[i] = pu[idx];
            nv[i] = pv[idx];
        }

        float[][] halfSubmaps = {
            {0f, 0.5f, 0.5f, 1f},
            {0.5f, 0.5f, 1f, 1f},
            {0.5f, 0f, 1f, 0.5f},
            {0f, 0f, 0.5f, 0.5f}
        };

        Direction.Axis axis = face.getAxis();
        List<BakedQuad> result = new ArrayList<>(4);

        for (int q = 0; q < 4; q++) {
            float[] hs = halfSubmaps[q];
            float subXOff = hs[0], subYOff = hs[1], subXEnd = hs[2], subYEnd = hs[3];

            boolean flipX = false, flipY = false;
            if (axis != Direction.Axis.Y) {
                flipY = true;
            }
            if (face == Direction.EAST || face == Direction.NORTH) {
                flipX = true;
            }

            float sxo = flipX ? (1f - subXEnd) : subXOff;
            float sxe = flipX ? (1f - subXOff) : subXEnd;
            float syo = flipY ? (1f - subYEnd) : subYOff;
            float sye = flipY ? (1f - subYOff) : subYEnd;

            float[] newXY = new float[8];
            float[] xy = new float[8];
            for (int i = 0; i < 4; i++) {
                switch (axis) {
                    case Y -> { xy[i * 2] = nx[i]; xy[i * 2 + 1] = nz[i]; }
                    case Z -> { xy[i * 2] = nx[i]; xy[i * 2 + 1] = ny[i]; }
                    case X -> { xy[i * 2] = nz[i]; xy[i * 2 + 1] = ny[i]; }
                }
            }

            if (axis == Direction.Axis.Y || face == Direction.SOUTH || face == Direction.WEST) {
                newXY[0] = Math.max(xy[0], sxo);
                newXY[2] = Math.max(xy[2], sxo);
                newXY[4] = Math.min(xy[4], sxe);
                newXY[6] = Math.min(xy[6], sxe);
            } else {
                newXY[0] = Math.min(xy[0], sxe);
                newXY[2] = Math.min(xy[2], sxe);
                newXY[4] = Math.max(xy[4], sxo);
                newXY[6] = Math.max(xy[6], sxo);
            }

            if (face != Direction.UP) {
                newXY[1] = Math.min(xy[1], sye);
                newXY[3] = Math.max(xy[3], syo);
                newXY[5] = Math.max(xy[5], syo);
                newXY[7] = Math.min(xy[7], sye);
            } else {
                newXY[1] = Math.max(xy[1], syo);
                newXY[3] = Math.min(xy[3], sye);
                newXY[5] = Math.min(xy[5], sye);
                newXY[7] = Math.max(xy[7], syo);
            }

            float u0Interp = inverseLerp(xy[0], xy[6], newXY[0]);
            float v0Interp = inverseLerp(xy[1], xy[3], newXY[1]);
            float u1Interp = inverseLerp(xy[2], xy[4], newXY[2]);
            float v1Interp = inverseLerp(xy[3], xy[1], newXY[3]);
            float u2Interp = inverseLerp(xy[4], xy[2], newXY[4]);
            float v2Interp = inverseLerp(xy[5], xy[7], newXY[5]);
            float u3Interp = inverseLerp(xy[6], xy[0], newXY[6]);
            float v3Interp = inverseLerp(xy[7], xy[5], newXY[7]);

            float newU0 = lerp(nu[0], nu[3], u0Interp);
            float newV0 = lerp(nv[0], nv[1], v0Interp);
            float newU1 = lerp(nu[1], nu[2], u1Interp);
            float newV1 = lerp(nv[1], nv[0], v1Interp);
            float newU2 = lerp(nu[2], nu[1], u2Interp);
            float newV2 = lerp(nv[2], nv[3], v2Interp);
            float newU3 = lerp(nu[3], nu[0], u3Interp);
            float newV3 = lerp(nv[3], nv[2], v3Interp);

            ChiselMutableQuad subQuad = new ChiselMutableQuad();
            subQuad.fillFromBakedQuad(original);

            for (int i = 0; i < 4; i++) {
                int srcIdx = (firstIndex + i) % 4;
                float newPosA, newPosB;
                switch (i) {
                    case 0 -> { newPosA = newXY[0]; newPosB = newXY[1]; }
                    case 1 -> { newPosA = newXY[2]; newPosB = newXY[3]; }
                    case 2 -> { newPosA = newXY[4]; newPosB = newXY[5]; }
                    default -> { newPosA = newXY[6]; newPosB = newXY[7]; }
                }
                switch (axis) {
                    case Y -> subQuad.pos(srcIdx, newPosA, reader.y(srcIdx), newPosB);
                    case Z -> subQuad.pos(srcIdx, newPosA, newPosB, reader.z(srcIdx));
                    case X -> subQuad.pos(srcIdx, reader.x(srcIdx), newPosB, newPosA);
                }
            }

            subQuad.uv((firstIndex) % 4, newU0, newV0);
            subQuad.uv((firstIndex + 1) % 4, newU1, newV1);
            subQuad.uv((firstIndex + 2) % 4, newU2, newV2);
            subQuad.uv((firstIndex + 3) % 4, newU3, newV3);

            float subMinU = Math.min(Math.min(newU0, newU1), Math.min(newU2, newU3));
            float subMaxU = Math.max(Math.max(newU0, newU1), Math.max(newU2, newU3));
            float subMinV = Math.min(Math.min(newV0, newV1), Math.min(newV2, newV3));
            float subMaxV = Math.max(Math.max(newV0, newV1), Math.max(newV2, newV3));

            float normMinU = inverseLerp(baseSprite.getU0(), baseSprite.getU1(), subMinU);
            float normMaxU = inverseLerp(baseSprite.getU0(), baseSprite.getU1(), subMaxU);
            float normMinV = inverseLerp(baseSprite.getV0(), baseSprite.getV1(), subMinV);
            float normMaxV = inverseLerp(baseSprite.getV0(), baseSprite.getV1(), subMaxV);

            int quadrant;
            if (normMaxU <= 0.5f) {
                quadrant = normMaxV <= 0.5f ? 3 : 0;
            } else {
                quadrant = normMaxV <= 0.5f ? 2 : 1;
            }

            int ctmid = submapCache[quadrant];
            TextureAtlasSprite targetSprite = ctmid > 15 ? baseSprite : (ctmSheet != null ? ctmSheet : baseSprite);
            if (targetSprite == baseSprite && ctmid <= 15) {
                ctmid = 16 + quadrant;
            }

            float[] sm = SUBMAP_UV_PIXEL[ctmid];
            float smXOff = sm[0] / 16f;
            float smYOff = sm[1] / 16f;
            float smW = sm[2] / 16f;
            float smH = sm[3] / 16f;

            float qMinU = quadrant == 1 || quadrant == 2 ? 0.5f : 0f;
            float qMinV = quadrant < 2 ? 0.5f : 0f;
            float qMaxU = quadrant == 0 || quadrant == 3 ? 0.5f : 1f;
            float qMaxV = quadrant > 1 ? 0.5f : 1f;

            for (int i = 0; i < 4; i++) {
                int srcIdx = (firstIndex + i) % 4;
                float u = subQuad.u(srcIdx);
                float v = subQuad.v(srcIdx);

                float normU = inverseLerp(baseSprite.getU0(), baseSprite.getU1(), u);
                float normV = inverseLerp(baseSprite.getV0(), baseSprite.getV1(), v);

                float expandedU = inverseLerp(qMinU, qMaxU, normU);
                float expandedV = inverseLerp(qMinV, qMaxV, normV);

                float finalU = smXOff + expandedU * smW;
                float finalV = smYOff + expandedV * smH;

                float atlasU = targetSprite.getU0() + finalU * (targetSprite.getU1() - targetSprite.getU0());
                float atlasV = targetSprite.getV0() + finalV * (targetSprite.getV1() - targetSprite.getV0());

                subQuad.uv(srcIdx, atlasU, atlasV);
            }

            if (targetSprite != baseSprite) {
                subQuad.setSprite(targetSprite);
            }

            result.add(subQuad.toBakedQuad());
        }

        return result;
    }

    private static float lerp(float a, float b, float f) {
        return a * (1 - f) + b * f;
    }

    private static float inverseLerp(float min, float max, float x) {
        if (min == max) return 0.5f;
        return (x - min) / (max - min);
    }
}
