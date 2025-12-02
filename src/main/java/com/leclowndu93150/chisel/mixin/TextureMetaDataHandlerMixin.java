package com.leclowndu93150.chisel.mixin;

import net.minecraft.client.resources.model.BakedModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import team.chisel.ctm.client.util.TextureMetadataHandler;

/**
 * Fixes NPE in CTM's TextureMetadataHandler when a baked model is null.
 * The original code does: if (!(baked instanceof AbstractCTMBakedModel) && !baked.isCustomRenderer())
 * which crashes if baked is null because isCustomRenderer() is called on null.
 */
@Mixin(value = TextureMetadataHandler.class, remap = false)
public class TextureMetaDataHandlerMixin {

    @Redirect(
            method = "onModelBake(Lnet/minecraftforge/client/event/ModelEvent$ModifyBakingResult;)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/resources/model/BakedModel;isCustomRenderer()Z"),
            remap = true
    )
    private boolean chisel$nullSafeIsCustomRenderer(BakedModel baked) {
        if (baked == null) {
            return true;
        }
        return baked.isCustomRenderer();
    }
}
