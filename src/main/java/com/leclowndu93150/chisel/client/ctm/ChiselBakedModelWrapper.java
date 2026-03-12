package com.leclowndu93150.chisel.client.ctm;

import com.leclowndu93150.chisel.api.chunkdata.ChunkData;
import com.leclowndu93150.chisel.api.chunkdata.IOffsetData;
import com.supermartijn642.fusion.extensions.TextureAtlasSpriteExtension;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.BakedModelWrapper;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ChiselBakedModelWrapper extends BakedModelWrapper<BakedModel> {

    public static final ModelProperty<BlockPos> POSITION = new ModelProperty<>();
    public static final ModelProperty<BlockAndTintGetter> LEVEL = new ModelProperty<>();

    public ChiselBakedModelWrapper(BakedModel original) {
        super(original);
    }

    @Override
    public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData) {
        BlockPos effectivePos = pos;
        IOffsetData offsetData = ChunkData.getOffsetData(level, pos);
        BlockPos offset = offsetData.getOffset(pos);
        if (!offset.equals(BlockPos.ZERO)) {
            effectivePos = pos.offset(offset);
        }
        return ModelData.builder()
                .with(POSITION, effectivePos)
                .with(LEVEL, level)
                .build();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType) {
        List<BakedQuad> original = originalModel.getQuads(state, side, rand, data, renderType);
        BlockPos pos = data.get(POSITION);
        BlockAndTintGetter level = data.get(LEVEL);
        if (pos == null) return original;

        boolean modified = false;
        List<BakedQuad> result = null;

        for (int i = 0; i < original.size(); i++) {
            BakedQuad quad = original.get(i);
            var textureType = ((TextureAtlasSpriteExtension) quad.getSprite()).getFusionTextureType();
            if (textureType instanceof ChiselQuadProcessor processor) {
                if (!modified) {
                    modified = true;
                    result = new ArrayList<>(original.size());
                    for (int j = 0; j < i; j++) result.add(original.get(j));
                }
                result.addAll(processor.processQuad(quad, pos, level, state));
                continue;
            }
            if (modified) result.add(quad);
        }

        return modified ? result : original;
    }
}
