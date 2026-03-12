package com.leclowndu93150.chisel.mixin;

import com.leclowndu93150.chisel.client.ctm.ChiselCTMTypes;
import com.leclowndu93150.chisel.client.ctm.ChiselQuadProcessor;
import com.supermartijn642.fusion.extensions.TextureAtlasSpriteExtension;
import com.supermartijn642.fusion.model.types.base.BaseBakedModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = BaseBakedModel.class, remap = false)
public class FusionBaseBakedModelMixin {

    @Inject(
        method = "getQuads(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;Lnet/minecraft/util/RandomSource;Lnet/neoforged/neoforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)Ljava/util/List;",
        at = @At("RETURN"),
        cancellable = true
    )
    private void chisel$processQuads(BlockState state, Direction side, RandomSource random, ModelData data, RenderType renderType, CallbackInfoReturnable<List<BakedQuad>> cir) {
        BlockPos pos = data.get(BaseBakedModel.POSITION_PROPERTY);
        BlockAndTintGetter level = data.get(ChiselCTMTypes.CHISEL_LEVEL_PROPERTY);
        if (pos == null) return;

        List<BakedQuad> original = cir.getReturnValue();
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
                List<BakedQuad> processed = processor.processQuad(quad, pos, level, state);
                result.addAll(processed);
                continue;
            }
            if (modified) result.add(quad);
        }

        if (modified) cir.setReturnValue(result);
    }
}
