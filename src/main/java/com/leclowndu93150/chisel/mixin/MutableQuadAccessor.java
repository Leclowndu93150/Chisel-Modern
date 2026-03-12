package com.leclowndu93150.chisel.mixin;

import com.supermartijn642.fusion.model.MutableQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MutableQuad.class, remap = false)
public interface MutableQuadAccessor {
    @Accessor("sprite")
    void chisel$setSprite(TextureAtlasSprite sprite);
}
