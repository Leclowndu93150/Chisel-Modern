package com.leclowndu93150.chisel.mixin;

import com.mojang.blaze3d.platform.Transparency;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.resources.model.cuboid.FaceBakery;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(FaceBakery.class)
public class FaceBakeryMixin {

    @Redirect(
            method = "computeMaterialTransparency",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/SpriteContents;computeTransparency(FFFF)Lcom/mojang/blaze3d/platform/Transparency;")
    )
    private static Transparency chisel$clampUVs(SpriteContents contents, float u0, float v0, float u1, float v1) {
        return contents.computeTransparency(clamp(u0), clamp(v0), clamp(u1), clamp(v1));
    }

    private static float clamp(float v) {
        if (v < 0.0F) return 0.0F;
        if (v > 1.0F) return 1.0F;
        return v;
    }
}
