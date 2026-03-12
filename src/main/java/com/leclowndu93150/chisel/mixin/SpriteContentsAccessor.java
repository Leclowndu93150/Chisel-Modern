package com.leclowndu93150.chisel.mixin;

import com.supermartijn642.fusion.api.texture.TextureType;
import com.supermartijn642.fusion.api.util.Pair;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteContents.class)
public interface SpriteContentsAccessor {
    @Accessor(value = "fusionTextureMetadata", remap = false)
    void chisel$setFusionTextureMetadata(Pair<TextureType<Object>, Object> metadata);
}
