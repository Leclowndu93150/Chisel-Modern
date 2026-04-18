package com.leclowndu93150.chisel.client.gui;

import com.leclowndu93150.chisel.api.carving.IChiselMode;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

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
    protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SPRITES.get(this.active, this.isHovered()), this.getX(), this.getY(), this.width, this.height);

        Identifier spriteSheet = mode.getSpriteSheet();
        int[] spritePos = mode.getSpritePos();

        graphics.blit(
                RenderPipelines.GUI_TEXTURED,
                spriteSheet,
                getX() + ICON_OFFSET,
                getY() + ICON_OFFSET,
                spritePos[0],
                spritePos[1],
                RENDER_SIZE,
                RENDER_SIZE,
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
