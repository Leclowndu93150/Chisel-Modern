package com.leclowndu93150.chisel.client.ctm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class CTMSprite extends TextureAtlasSprite {

    private final ResourceLocation ctmTextureLocation;
    private TextureAtlasSprite ctmSprite;
    private boolean ctmResolved;

    public CTMSprite(TextureAtlasSprite original, ResourceLocation ctmTextureLocation) {
        super(
            original.atlasLocation(),
            original.contents(),
            1,
            1,
            original.getX(),
            original.getY()
        );
        this.u0 = original.getU0();
        this.v0 = original.getV0();
        this.u1 = original.getU1();
        this.v1 = original.getV1();
        this.ctmTextureLocation = ctmTextureLocation;
    }

    public TextureAtlasSprite getCtmSprite() {
        if (!ctmResolved) {
            ctmResolved = true;
            try {
                ctmSprite = Minecraft.getInstance()
                    .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                    .apply(ctmTextureLocation);
                if (ctmSprite.contents().name().equals(ResourceLocation.withDefaultNamespace("missingno"))) {
                    ctmSprite = null;
                }
            } catch (Exception e) {
                ctmSprite = null;
            }
        }
        return ctmSprite;
    }

    public ResourceLocation getCtmTextureLocation() {
        return ctmTextureLocation;
    }
}
