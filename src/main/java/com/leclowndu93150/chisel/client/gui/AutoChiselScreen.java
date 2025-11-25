package com.leclowndu93150.chisel.client.gui;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.ChiselConfig;
import com.leclowndu93150.chisel.init.ChiselItems;
import com.leclowndu93150.chisel.inventory.AutoChiselMenu;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class AutoChiselScreen extends AbstractContainerScreen<AutoChiselMenu> {

    private static final ResourceLocation TEXTURE = Chisel.id("textures/gui/autochisel.png");

    private static final int PROG_BAR_LENGTH = 50;
    private static final int POWER_BAR_LENGTH = 160;

    private final ItemStack fakeChisel;

    public AutoChiselScreen(AutoChiselMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageHeight = 200;
        this.fakeChisel = new ItemStack(ChiselItems.IRON_CHISEL.get());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (menu.isActive()) {
            int scaledProg = menu.getProgressScaled(PROG_BAR_LENGTH);
            graphics.blit(TEXTURE, leftPos + 63, topPos + 19 + 9, 176, 18, scaledProg + 1, 16);
        }

        if (ChiselConfig.autoChiselPowered) {
            graphics.blit(TEXTURE, leftPos + 7, topPos + 93, 7, 200, 162, 6);
            if (menu.hasEnergy()) {
                graphics.blit(TEXTURE, leftPos + 8, topPos + 94, 8, 206, menu.getEnergyScaled(POWER_BAR_LENGTH) + 1, 4);
            }
        }

        if (!menu.getSlot(menu.chiselSlot).hasItem()) {
            drawGhostItem(graphics, fakeChisel, leftPos + 80, topPos + 28);
        }
        if (!menu.getSlot(menu.targetSlot).hasItem()) {
            RenderSystem.setShaderColor(1, 1, 1, 1);
            graphics.blit(TEXTURE, leftPos + 80, topPos + 64, 176, 34, 16, 16);
        }
    }

    private void drawGhostItem(GuiGraphics graphics, ItemStack stack, int x, int y) {
        graphics.renderItem(stack, x, y);
        RenderSystem.setShaderColor(1, 1, 1, 0.5f);
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        Lighting.setupForFlatItems();
        graphics.blit(TEXTURE, x, y, x - leftPos, y - topPos, 16, 16);
        Lighting.setupFor3DItems();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, imageWidth / 2 - font.width(title) / 2, 6, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 0x404040, false);

        if (ChiselConfig.autoChiselPowered) {
            int localMouseX = mouseX - leftPos;
            int localMouseY = mouseY - topPos;

            if (localMouseX >= 7 && localMouseY >= 93 && localMouseX <= 169 && localMouseY <= 98) {
                NumberFormat fmt = NumberFormat.getNumberInstance();
                String stored = fmt.format(menu.getEnergy());
                String max = fmt.format(menu.getMaxEnergy());
                List<Component> tooltip = new ArrayList<>();
                tooltip.add(Component.translatable("chisel.tooltip.power.stored", stored, max));
                tooltip.add(Component.translatable("chisel.tooltip.power.pertick", fmt.format(menu.getUsagePerTick()))
                        .withStyle(ChatFormatting.GRAY));
                graphics.renderComponentTooltip(font, tooltip, localMouseX, localMouseY);
            }
        }
    }
}
