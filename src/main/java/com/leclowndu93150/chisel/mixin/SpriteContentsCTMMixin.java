package com.leclowndu93150.chisel.mixin;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.client.ctm.CTMMetadataSection;
import com.supermartijn642.fusion.extensions.SpriteContentsExtension;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = SpriteContents.class, priority = 1100)
public class SpriteContentsCTMMixin {

    @Inject(
        method = "<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/client/resources/metadata/animation/FrameSize;Lcom/mojang/blaze3d/platform/NativeImage;Lnet/minecraft/server/packs/resources/ResourceMetadata;)V",
        at = @At("TAIL")
    )
    private void chisel$loadCTMMetadata(ResourceLocation id, FrameSize frameSize, NativeImage image, ResourceMetadata metadata, CallbackInfo ci) {
        if (((SpriteContentsExtension) this).fusionTextureMetadata() != null) return;

        metadata.getSection(CTMMetadataSection.INSTANCE).ifPresent(data -> {
            ((SpriteContentsAccessor) this).chisel$setFusionTextureMetadata(data);
        });
    }
}
