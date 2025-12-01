package com.leclowndu93150.chisel.mixin;

import net.minecraft.client.resources.model.BakedModel;
import team.chisel.ctm.client.util.TextureMetadataHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = TextureMetadataHandler.class, remap = false)
public abstract class TextureMetadataHandlerMixin {

    /**
     * Patch: Return true for isCustomRenderer() when baked model is null to skip processing.
     * This prevents NPE when baked model is null in the map.
     */
    @Redirect(
            method = "onModelBake(Lnet/neoforged/neoforge/client/event/ModelEvent$ModifyBakingResult;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/resources/model/BakedModel;isCustomRenderer()Z",
                    remap = true
            )
    )
    private boolean chisel$skipNullBakedModels(BakedModel baked) {
        return baked == null || baked.isCustomRenderer();
    }
}
