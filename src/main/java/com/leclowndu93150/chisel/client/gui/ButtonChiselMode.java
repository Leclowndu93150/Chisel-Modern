package com.leclowndu93150.chisel.client.gui;

import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ButtonChiselMode extends Button {

    private static final int SPRITE_SIZE = 24;
    private static final int RENDER_SIZE = 16;
    private static final int ICON_OFFSET = 2;

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
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        graphics.blitSprite(SPRITES.get(this.active, this.isHovered()), this.getX(), this.getY(), this.width, this.height);

        ResourceLocation spriteSheet = mode.getSpriteSheet();
        int[] spritePos = mode.getSpritePos();

        graphics.blit(
                spriteSheet,
                getX() + ICON_OFFSET,
                getY() + ICON_OFFSET,
                RENDER_SIZE,
                RENDER_SIZE,
                spritePos[0],
                spritePos[1],
                SPRITE_SIZE,
                SPRITE_SIZE,
                256,
                256
        );
    }

    @Override
    public boolean isHovered() {
        return super.isHovered();
    }
}
