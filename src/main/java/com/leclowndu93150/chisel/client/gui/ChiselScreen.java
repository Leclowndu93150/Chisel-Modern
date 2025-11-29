package com.leclowndu93150.chisel.client.gui;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.leclowndu93150.chisel.carving.ChiselModeRegistry;
import com.leclowndu93150.chisel.inventory.ChiselMenu;
import com.leclowndu93150.chisel.item.ItemChisel;
import com.leclowndu93150.chisel.network.server.ChiselModePacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import com.leclowndu93150.chisel.network.ChiselNetwork;

import java.util.List;

public class ChiselScreen extends AbstractContainerScreen<ChiselMenu> {

    private static final ResourceLocation TEXTURE = Chisel.id("textures/gui/chisel2gui.png");
    public static final int GUI_WIDTH = 252;
    public static final int GUI_HEIGHT = 202;

    public ChiselScreen(ChiselMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        ItemStack chisel = menu.getChisel();
        if (chisel.getItem() instanceof ItemChisel itemChisel && itemChisel.getChiselType().hasModes()) {
            addModeButtons();
        }
    }

    private void addModeButtons() {
        Rect2i area = getModeButtonArea();
        int buttonsPerRow = area.getWidth() / 20;
        int padding = (area.getWidth() - (buttonsPerRow * 20)) / Math.max(1, buttonsPerRow);
        int id = 0;

        ItemStack chisel = menu.getChisel();
        IChiselMode currentMode = null;
        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            currentMode = itemChisel.getMode(chisel);
        }

        for (IChiselMode mode : ChiselModeRegistry.INSTANCE.getAllModes()) {
            if (chisel.getItem() instanceof ItemChisel itemChisel) {
                if (!itemChisel.supportsMode(minecraft.player, chisel, mode)) {
                    continue;
                }
            }

            int x = area.getX() + (padding / 2) + ((id % buttonsPerRow) * (20 + padding));
            int y = area.getY() + ((id / buttonsPerRow) * (20 + padding));

            final IChiselMode finalMode = mode;
            ButtonChiselMode button = new ButtonChiselMode(x, y, 20, 20, mode, b -> {
                onModeButtonClick(finalMode);
                // Disable this button, enable others
                b.active = false;
                for (Renderable other : renderables) {
                    if (other != b && other instanceof ButtonChiselMode b2) {
                        b2.active = true;
                    }
                }
            });

            if (mode == currentMode) {
                button.active = false;
            }

            addRenderableWidget(button);
            id++;
        }
    }

    protected Rect2i getModeButtonArea() {
        int down = 73;
        int padding = 7;
        return new Rect2i(leftPos + padding, topPos + down + padding, 50, imageHeight - down - (padding * 2));
    }

    private void onModeButtonClick(IChiselMode mode) {
        ItemStack chisel = menu.getChisel();
        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            itemChisel.setMode(chisel, mode);
            int slot = menu.getHand() == InteractionHand.MAIN_HAND
                    ? minecraft.player.getInventory().selected
                    : 40; // Offhand slot
            ChiselNetwork.sendToServer(new ChiselModePacket(slot, mode));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        Slot inputSlot = menu.getInputSlot();
        if (inputSlot.getItem().isEmpty()) {
            graphics.blit(TEXTURE, leftPos + inputSlot.x - 16, topPos + inputSlot.y - 16, 0, imageHeight, 48, 48);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        List<FormattedCharSequence> lines = font.split(title, 40);
        int y = 60;
        for (FormattedCharSequence s : lines) {
            graphics.drawString(font, s, 32 - font.width(s) / 2, y, 0x404040, false);
            y += 10;
        }

        drawButtonTooltips(graphics, mouseX, mouseY);
    }

    protected void drawButtonTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        for (Renderable widget : renderables) {
            if (widget instanceof ButtonChiselMode button && button.isHovered()) {
                IChiselMode mode = button.getMode();
                List<Component> ttLines = List.of(
                        mode.getLocalizedName(),
                        mode.getLocalizedDescription().copy().withStyle(ChatFormatting.GRAY)
                );
                graphics.renderComponentTooltip(font, ttLines, mouseX - leftPos, mouseY - topPos);
            }
        }
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        Slot inputSlot = menu.getInputSlot();
        if (x == inputSlot.x && y == inputSlot.y && width == 16 && height == 16) {
            return super.isHovering(x - 8, y - 8, 32, 32, mouseX, mouseY);
        }
        return super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public void renderSlot(GuiGraphics graphics, Slot slot) {
        if (slot == menu.getInputSlot()) {
            PoseStack poseStack = graphics.pose();
            poseStack.pushPose();

            float centerX = slot.x + 8;
            float centerY = slot.y + 8;

            poseStack.translate(centerX, centerY, 0);
            poseStack.scale(2.0f, 2.0f, 1.0f);
            poseStack.translate(-centerX, -centerY, 0);

            super.renderSlot(graphics, slot);

            poseStack.popPose();
        } else {
            super.renderSlot(graphics, slot);
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);
    }
}
