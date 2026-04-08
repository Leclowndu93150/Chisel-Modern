package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public final class ChiselSpriteResolver {

    private ChiselSpriteResolver() {
    }

    public static TextureAtlasSprite resolve(ResourceLocation location) {
        if (location == null) {
            return null;
        }

        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(location);
        if (sprite.contents().name().equals(ResourceLocation.withDefaultNamespace("missingno"))) {
            return null;
        }
        return sprite;
    }
}
