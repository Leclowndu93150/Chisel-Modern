package com.leclowndu93150.chisel.client.gui;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.leclowndu93150.chisel.carving.ChiselModeRegistry;
import com.leclowndu93150.chisel.inventory.ChiselMenu;
import com.leclowndu93150.chisel.item.ItemChisel;
import com.leclowndu93150.chisel.network.ChiselModePayload;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;

public class ChiselScreen extends AbstractContainerScreen<ChiselMenu> {

    private static final ResourceLocation TEXTURE = Chisel.id("textures/gui/chisel2gui.png");
    public static final int GUI_WIDTH = 252;
    public static final int GUI_HEIGHT = 202;

    private static final int MODE_BUTTON_LEFT = 7;
    private static final int MODE_BUTTON_TOP = 80;
    private static final int MODE_BUTTON_HEIGHT = 119;
    private static final int MODE_BUTTON_SIZE = 20;

    public ChiselScreen(ChiselMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        ItemStack chisel = menu.getChiselStack();
        if (chisel.getItem() instanceof ItemChisel itemChisel && itemChisel.getChiselType().hasModes()) {
            addModeButtons();
        }
    }

    private void addModeButtons() {
        int buttonX = leftPos + MODE_BUTTON_LEFT;
        int buttonY = topPos + MODE_BUTTON_TOP;
        int maxModes = MODE_BUTTON_HEIGHT / MODE_BUTTON_SIZE;
        int modesAdded = 0;

        for (IChiselMode mode : ChiselModeRegistry.INSTANCE.getAllModes()) {
            if (modesAdded >= maxModes) break;

            ItemStack chisel = menu.getChiselStack();
            if (chisel.getItem() instanceof ItemChisel itemChisel) {
                if (!itemChisel.supportsMode(minecraft.player, chisel, mode)) {
                    continue;
                }
            }

            final IChiselMode finalMode = mode;
            addRenderableWidget(new ButtonChiselMode(
                    buttonX,
                    buttonY + modesAdded * (MODE_BUTTON_SIZE + 2),
                    MODE_BUTTON_SIZE,
                    MODE_BUTTON_SIZE,
                    mode,
                    button -> onModeButtonClick(finalMode)
            ));
            modesAdded++;
        }
    }

    private void onModeButtonClick(IChiselMode mode) {
        ItemStack chisel = menu.getChiselStack();
        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            itemChisel.setMode(chisel, mode);
            // Send to server
            int slot = menu.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND
                    ? minecraft.player.getInventory().selected
                    : 40; // Offhand slot
            PacketDistributor.sendToServer(new ChiselModePayload(slot, mode));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    protected void renderTooltip(GuiGraphics graphics, int mouseX, int mouseY) {
        super.renderTooltip(graphics, mouseX, mouseY);

        for (var widget : this.children()) {
            if (widget instanceof ButtonChiselMode modeButton && modeButton.isHovered()) {
                graphics.renderTooltip(font, modeButton.getMode().getLocalizedDescription(), mouseX, mouseY);
            }
        }
    }
}
