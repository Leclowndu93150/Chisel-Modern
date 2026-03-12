package com.leclowndu93150.chisel.mixin;

import com.leclowndu93150.chisel.api.chunkdata.ChunkData;
import com.leclowndu93150.chisel.api.chunkdata.IOffsetData;
import com.leclowndu93150.chisel.client.ctm.ChiselCTMTypes;
import com.leclowndu93150.chisel.client.ctm.ChiselQuadProcessor;
import com.supermartijn642.fusion.extensions.TextureAtlasSpriteExtension;
import com.supermartijn642.fusion.model.types.base.BaseBakedModel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = BaseBakedModel.class, remap = false)
public class FusionModelDataMixin {

    @Shadow
    @Final
    private boolean hasSpecialQuads;

    @Unique
    private Boolean chisel$hasChiselQuads;

    @Inject(
        method = "getModelData",
        at = @At("HEAD"),
        cancellable = true
    )
    private void chisel$provideModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data, CallbackInfoReturnable<ModelData> cir) {
        if (this.hasSpecialQuads) return;

        if (chisel$hasChiselQuads == null) {
            chisel$hasChiselQuads = chisel$detectChiselQuads();
        }

        if (chisel$hasChiselQuads) {
            BlockPos effectivePos = pos;
            IOffsetData offsetData = ChunkData.getOffsetData(level, pos);
            BlockPos offset = offsetData.getOffset(pos);
            if (!offset.equals(BlockPos.ZERO)) {
                effectivePos = pos.offset(offset);
            }
            cir.setReturnValue(ModelData.builder()
                .with(BaseBakedModel.POSITION_PROPERTY, effectivePos)
                .with(ChiselCTMTypes.CHISEL_LEVEL_PROPERTY, level)
                .build());
        }
    }

    @Inject(
        method = "getModelData",
        at = @At("RETURN"),
        cancellable = true
    )
    private void chisel$applyOffsetAndLevel(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData data, CallbackInfoReturnable<ModelData> cir) {
        if (!this.hasSpecialQuads) return;

        ModelData original = cir.getReturnValue();
        if (original == ModelData.EMPTY) return;

        IOffsetData offsetData = ChunkData.getOffsetData(level, pos);
        BlockPos offset = offsetData.getOffset(pos);
        BlockPos effectivePos = offset.equals(BlockPos.ZERO) ? pos : pos.offset(offset);

        cir.setReturnValue(original.derive()
            .with(BaseBakedModel.POSITION_PROPERTY, effectivePos)
            .with(ChiselCTMTypes.CHISEL_LEVEL_PROPERTY, level)
            .build());
    }

    @Unique
    private boolean chisel$detectChiselQuads() {
        BaseBakedModel self = (BaseBakedModel) (Object) this;
        for (Direction dir : Direction.values()) {
            List<BakedQuad> quads = self.getQuads(null, dir, null, ModelData.EMPTY, null);
            for (BakedQuad quad : quads) {
                var type = ((TextureAtlasSpriteExtension) quad.getSprite()).getFusionTextureType();
                if (type instanceof ChiselQuadProcessor) return true;
            }
        }
        List<BakedQuad> quads = self.getQuads(null, null, null, ModelData.EMPTY, null);
        for (BakedQuad quad : quads) {
            var type = ((TextureAtlasSpriteExtension) quad.getSprite()).getFusionTextureType();
            if (type instanceof ChiselQuadProcessor) return true;
        }
        return false;
    }
}
