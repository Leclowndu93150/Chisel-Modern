package com.leclowndu93150.chisel.client.gui;

import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/**
 * Button widget for selecting chisel modes.
 * Based on the original Chisel mod's ButtonChiselMode.
 */
public class ButtonChiselMode extends Button {

    private static final int SPRITE_SIZE = 24; // Size of each sprite in the sprite sheet
    private static final int RENDER_SIZE = 16; // Size to render on screen
    private static final int ICON_OFFSET = 2;  // Offset from button edge

    private final IChiselMode mode;

    public ButtonChiselMode(int x, int y, int width, int height, IChiselMode mode, OnPress onPress) {
        super(x, y, width, height, mode.getLocalizedName(), onPress, DEFAULT_NARRATION);
        this.mode = mode;
    }

    public IChiselMode getMode() {
        return mode;
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // Draw button background
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        // Draw vanilla button texture as background
        graphics.blitSprite(SPRITES.get(this.active, this.isHovered()), this.getX(), this.getY(), this.width, this.height);

        // Draw mode icon from sprite sheet
        ResourceLocation spriteSheet = mode.getSpriteSheet();
        int[] spritePos = mode.getSpritePos();

        // Blit the full 24x24 sprite, scaled to RENDER_SIZE
        graphics.blit(
                spriteSheet,
                getX() + ICON_OFFSET,
                getY() + ICON_OFFSET,
                RENDER_SIZE,  // render width
                RENDER_SIZE,  // render height
                spritePos[0], // u
                spritePos[1], // v
                SPRITE_SIZE,  // uWidth (source width in texture)
                SPRITE_SIZE,  // vHeight (source height in texture)
                256,          // textureWidth
                256           // textureHeight
        );
    }

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }
}
