package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

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

    private final ResourceLocation targetSprite;
    private final ResourceLocation ctmSheet;
    private static final float[][] QUADRANT_SUBMAPS = {
        {0f, 0.5f, 0.5f, 0.5f},
        {0.5f, 0.5f, 0.5f, 0.5f},
        {0.5f, 0f, 0.5f, 0.5f},
        {0f, 0f, 0.5f, 0.5f}
    };

    public CTMFullProcessor(ResourceLocation targetSprite, ResourceLocation ctmSheet) {
        this.targetSprite = targetSprite;
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

        return subdivideAndRemap(quad, submapCache);
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

    private List<BakedQuad> subdivideAndRemap(BakedQuad original, int[] submapCache) {
        TextureAtlasSprite baseSprite = targetSprite != null ? ChiselSpriteResolver.resolve(targetSprite) : original.getSprite();
        if (baseSprite == null) {
            baseSprite = original.getSprite();
        }
        TextureAtlasSprite ctmSheetSprite = ctmSheet != null ? ChiselSpriteResolver.resolve(ctmSheet) : null;
        ChiselMutableQuad working = new ChiselMutableQuad();
        working.fillFromBakedQuad(original);
        working.derotate();

        List<BakedQuad> result = new ArrayList<>(4);

        for (float[] quadrantSubmap : QUADRANT_SUBMAPS) {
            ChiselMutableQuad subQuad = working.subsect(quadrantSubmap[0], quadrantSubmap[1], quadrantSubmap[2], quadrantSubmap[3]);
            int quadrant = subQuad.getQuadrant();
            int ctmid = submapCache[quadrant];
            TextureAtlasSprite targetSprite = ctmid > 15 ? baseSprite : (ctmSheetSprite != null ? ctmSheetSprite : baseSprite);
            if (targetSprite == baseSprite && ctmid <= 15) {
                ctmid = 16 + quadrant;
            }

            subQuad.normalizeQuadrant();

            float[] sm = SUBMAP_UV_PIXEL[ctmid];
            subQuad.transformUVs(targetSprite, sm[0] / 16f, sm[1] / 16f, sm[2] / 16f, sm[3] / 16f);
            result.add(subQuad.toBakedQuad());
        }

        return result;
    }
}
