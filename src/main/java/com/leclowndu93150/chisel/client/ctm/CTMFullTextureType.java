package com.leclowndu93150.chisel.client.ctm;

import com.google.gson.JsonObject;
import com.leclowndu93150.chisel.mixin.MutableQuadAccessor;
import com.supermartijn642.fusion.api.texture.SpriteCreationContext;
import com.supermartijn642.fusion.api.texture.SpritePreparationContext;
import com.supermartijn642.fusion.api.texture.TextureType;
import com.supermartijn642.fusion.api.util.Pair;
import com.supermartijn642.fusion.model.MutableQuad;
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

public class CTMFullTextureType implements TextureType<CTMTextureData>, ChiselQuadProcessor {

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
        // Offsets match vanilla MC / Fusion default texture orientation for each face.
        // For each face: up = texture "up" direction, right = texture "right" direction.
        // Order: TOP, TOP_RIGHT, RIGHT, BOTTOM_RIGHT, BOTTOM, BOTTOM_LEFT, LEFT, TOP_LEFT

        // SOUTH: up=UP(0,1,0), right=EAST(1,0,0)
        DIR_OFFSETS[Direction.SOUTH.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 1, 0),
            new BlockPos(1, 1, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(0, -1, 0),
            new BlockPos(-1, -1, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(-1, 1, 0)
        };
        // NORTH: up=UP(0,1,0), right=WEST(-1,0,0)
        DIR_OFFSETS[Direction.NORTH.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 1, 0),
            new BlockPos(-1, 1, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(-1, -1, 0),
            new BlockPos(0, -1, 0),
            new BlockPos(1, -1, 0),
            new BlockPos(1, 0, 0),
            new BlockPos(1, 1, 0)
        };
        // EAST: up=UP(0,1,0), right=NORTH(0,0,-1)
        DIR_OFFSETS[Direction.EAST.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 1, 0),
            new BlockPos(0, 1, -1),
            new BlockPos(0, 0, -1),
            new BlockPos(0, -1, -1),
            new BlockPos(0, -1, 0),
            new BlockPos(0, -1, 1),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 1, 1)
        };
        // WEST: up=UP(0,1,0), right=SOUTH(0,0,1)
        DIR_OFFSETS[Direction.WEST.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 1, 0),
            new BlockPos(0, 1, 1),
            new BlockPos(0, 0, 1),
            new BlockPos(0, -1, 1),
            new BlockPos(0, -1, 0),
            new BlockPos(0, -1, -1),
            new BlockPos(0, 0, -1),
            new BlockPos(0, 1, -1)
        };
        // UP: up=NORTH(0,0,-1), right=EAST(1,0,0)
        DIR_OFFSETS[Direction.UP.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 0, -1),
            new BlockPos(1, 0, -1),
            new BlockPos(1, 0, 0),
            new BlockPos(1, 0, 1),
            new BlockPos(0, 0, 1),
            new BlockPos(-1, 0, 1),
            new BlockPos(-1, 0, 0),
            new BlockPos(-1, 0, -1)
        };
        // DOWN: up=SOUTH(0,0,1), right=EAST(1,0,0)
        DIR_OFFSETS[Direction.DOWN.get3DDataValue()] = new BlockPos[]{
            new BlockPos(0, 0, 1),
            new BlockPos(1, 0, 1),
            new BlockPos(1, 0, 0),
            new BlockPos(1, 0, -1),
            new BlockPos(0, 0, -1),
            new BlockPos(-1, 0, -1),
            new BlockPos(-1, 0, 0),
            new BlockPos(-1, 0, 1)
        };
    }

    private static final int[] SUBMAP_OFFSETS = {4, 5, 1, 0};

    private static final int[][] SUBMAP_MAP = {
        {DIR_BOTTOM, DIR_LEFT, DIR_BOTTOM_LEFT},
        {DIR_BOTTOM, DIR_RIGHT, DIR_BOTTOM_RIGHT},
        {DIR_TOP, DIR_RIGHT, DIR_TOP_RIGHT},
        {DIR_TOP, DIR_LEFT, DIR_TOP_LEFT}
    };

    private static final float[][] SUBMAP_UVS = new float[20][4];

    static {
        for (int i = 0; i < 16; i++) {
            int col = i % 4;
            int row = i / 4;
            SUBMAP_UVS[i] = new float[]{
                col * 4f / 16f, row * 4f / 16f,
                (col + 1) * 4f / 16f, (row + 1) * 4f / 16f
            };
        }
        SUBMAP_UVS[16] = new float[]{0f, 0f, 0.5f, 0.5f};
        SUBMAP_UVS[17] = new float[]{0.5f, 0f, 1f, 0.5f};
        SUBMAP_UVS[18] = new float[]{0f, 0.5f, 0.5f, 1f};
        SUBMAP_UVS[19] = new float[]{0.5f, 0.5f, 1f, 1f};
    }

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
        return context.getOriginalFrameSize();
    }

    @Override
    public TextureAtlasSprite createSprite(SpriteCreationContext context, CTMTextureData data) {
        TextureAtlasSprite original = context.createOriginalSprite();
        String[] textures = data.getTextures();
        if (textures.length > 0) {
            ResourceLocation ctmLoc = ResourceLocation.parse(textures[0]);
            return new CTMSprite(original, ctmLoc);
        }
        return original;
    }

    @Override
    public List<BakedQuad> processQuad(BakedQuad quad, BlockPos pos, BlockAndTintGetter level, BlockState state) {
        TextureAtlasSprite sprite = quad.getSprite();
        if (!(sprite instanceof CTMSprite ctmSprite)) {
            return Collections.singletonList(quad);
        }

        Direction face = quad.getDirection();
        if (face == null || level == null) {
            return Collections.singletonList(quad);
        }

        byte connectionMap = buildConnectionMap(level, pos, state, face);

        int[] submapCache = {18, 19, 17, 16};
        for (int i = 0; i < 4; i++) {
            fillSubmaps(submapCache, connectionMap, i);
        }

        TextureAtlasSprite ctmSheet = ctmSprite.getCtmSprite();

        return subdivideAndMap(quad, submapCache, ctmSprite, ctmSheet);
    }

    private byte buildConnectionMap(BlockAndTintGetter level, BlockPos pos, BlockState state, Direction face) {
        byte map = 0;
        BlockPos[] offsets = DIR_OFFSETS[face.get3DDataValue()];
        for (int i = 0; i < 8; i++) {
            BlockPos neighbor = pos.offset(offsets[i]);
            BlockState neighborState = level.getBlockState(neighbor);
            if (state.getBlock() == neighborState.getBlock()) {
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

    private List<BakedQuad> subdivideAndMap(BakedQuad original, int[] submapCache,
                                            TextureAtlasSprite baseSprite, TextureAtlasSprite ctmSheet) {
        MutableQuad reader = new MutableQuad();
        reader.fillFromBakedQuad(original);

        float[] px = new float[4], py = new float[4], pz = new float[4];
        float[] pu = new float[4], pv = new float[4];
        for (int i = 0; i < 4; i++) {
            px[i] = reader.x(i);
            py[i] = reader.y(i);
            pz[i] = reader.z(i);
            pu[i] = reader.u(i);
            pv[i] = reader.v(i);
        }

        float minU = pu[0], maxU = pu[0], minV = pv[0], maxV = pv[0];
        for (int i = 1; i < 4; i++) {
            if (pu[i] < minU) minU = pu[i];
            if (pu[i] > maxU) maxU = pu[i];
            if (pv[i] < minV) minV = pv[i];
            if (pv[i] > maxV) maxV = pv[i];
        }

        float uRange = maxU - minU;
        float vRange = maxV - minV;
        if (uRange < 1e-6f || vRange < 1e-6f) {
            return Collections.singletonList(original);
        }

        int vTL = -1, vTR = -1, vBR = -1, vBL = -1;
        for (int i = 0; i < 4; i++) {
            boolean isLeft = (pu[i] - minU) < (maxU - pu[i]);
            boolean isTop = (pv[i] - minV) < (maxV - pv[i]);
            if (isTop && isLeft) vTL = i;
            else if (isTop) vTR = i;
            else if (isLeft) vBL = i;
            else vBR = i;
        }

        if (vTL < 0 || vTR < 0 || vBR < 0 || vBL < 0) {
            return Collections.singletonList(original);
        }

        float midX = (px[vTL] + px[vTR] + px[vBR] + px[vBL]) * 0.25f;
        float midY = (py[vTL] + py[vTR] + py[vBR] + py[vBL]) * 0.25f;
        float midZ = (pz[vTL] + pz[vTR] + pz[vBR] + pz[vBL]) * 0.25f;

        float midTopX = (px[vTL] + px[vTR]) * 0.5f;
        float midTopY = (py[vTL] + py[vTR]) * 0.5f;
        float midTopZ = (pz[vTL] + pz[vTR]) * 0.5f;

        float midBottomX = (px[vBL] + px[vBR]) * 0.5f;
        float midBottomY = (py[vBL] + py[vBR]) * 0.5f;
        float midBottomZ = (pz[vBL] + pz[vBR]) * 0.5f;

        float midLeftX = (px[vTL] + px[vBL]) * 0.5f;
        float midLeftY = (py[vTL] + py[vBL]) * 0.5f;
        float midLeftZ = (pz[vTL] + pz[vBL]) * 0.5f;

        float midRightX = (px[vTR] + px[vBR]) * 0.5f;
        float midRightY = (py[vTR] + py[vBR]) * 0.5f;
        float midRightZ = (pz[vTR] + pz[vBR]) * 0.5f;

        List<BakedQuad> result = new ArrayList<>(4);

        for (int q = 0; q < 4; q++) {
            int submapIdx = submapCache[q];
            TextureAtlasSprite targetSprite;
            if (submapIdx > 15) {
                targetSprite = baseSprite;
            } else {
                targetSprite = (ctmSheet != null) ? ctmSheet : baseSprite;
                if (targetSprite == baseSprite) {
                    submapIdx = 16 + q;
                }
            }

            float[] submapUV = SUBMAP_UVS[submapIdx];
            float tU0 = lerp(targetSprite.getU0(), targetSprite.getU1(), submapUV[0]);
            float tV0 = lerp(targetSprite.getV0(), targetSprite.getV1(), submapUV[1]);
            float tU1 = lerp(targetSprite.getU0(), targetSprite.getU1(), submapUV[2]);
            float tV1 = lerp(targetSprite.getV0(), targetSprite.getV1(), submapUV[3]);

            float cTLx, cTLy, cTLz, cTRx, cTRy, cTRz;
            float cBLx, cBLy, cBLz, cBRx, cBRy, cBRz;

            switch (q) {
                case 0: // bottom-left: BL, midBottom, center, midLeft
                    cTLx = midLeftX;  cTLy = midLeftY;  cTLz = midLeftZ;
                    cTRx = midX;      cTRy = midY;      cTRz = midZ;
                    cBRx = midBottomX; cBRy = midBottomY; cBRz = midBottomZ;
                    cBLx = px[vBL];   cBLy = py[vBL];   cBLz = pz[vBL];
                    break;
                case 1: // bottom-right: midBottom, BR, midRight, center
                    cTLx = midX;      cTLy = midY;      cTLz = midZ;
                    cTRx = midRightX; cTRy = midRightY; cTRz = midRightZ;
                    cBRx = px[vBR];   cBRy = py[vBR];   cBRz = pz[vBR];
                    cBLx = midBottomX; cBLy = midBottomY; cBLz = midBottomZ;
                    break;
                case 2: // top-right: center, midRight, TR, midTop
                    cTLx = midTopX;   cTLy = midTopY;   cTLz = midTopZ;
                    cTRx = px[vTR];   cTRy = py[vTR];   cTRz = pz[vTR];
                    cBRx = midRightX; cBRy = midRightY; cBRz = midRightZ;
                    cBLx = midX;      cBLy = midY;      cBLz = midZ;
                    break;
                default: // case 3, top-left: midLeft, center, midTop, TL
                    cTLx = px[vTL];   cTLy = py[vTL];   cTLz = pz[vTL];
                    cTRx = midTopX;   cTRy = midTopY;   cTRz = midTopZ;
                    cBRx = midX;      cBRy = midY;      cBRz = midZ;
                    cBLx = midLeftX;  cBLy = midLeftY;  cBLz = midLeftZ;
                    break;
            }

            MutableQuad subQuad = new MutableQuad();
            subQuad.fillFromBakedQuad(original);

            if (targetSprite != baseSprite) {
                ((MutableQuadAccessor) subQuad).chisel$setSprite(targetSprite);
            }

            subQuad.pos(vTL, cTLx, cTLy, cTLz);
            subQuad.uv(vTL, tU0, tV0);

            subQuad.pos(vTR, cTRx, cTRy, cTRz);
            subQuad.uv(vTR, tU1, tV0);

            subQuad.pos(vBR, cBRx, cBRy, cBRz);
            subQuad.uv(vBR, tU1, tV1);

            subQuad.pos(vBL, cBLx, cBLy, cBLz);
            subQuad.uv(vBL, tU0, tV1);

            result.add(subQuad.toBakedQuad());
        }

        return result;
    }

    private static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }
}
