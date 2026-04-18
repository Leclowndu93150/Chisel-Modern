package com.leclowndu93150.chisel.client.gui;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.ChiselConfig;
import com.leclowndu93150.chisel.init.ChiselItems;
import com.leclowndu93150.chisel.inventory.AutoChiselMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class AutoChiselScreen extends AbstractContainerScreen<AutoChiselMenu> {

    private static final Identifier TEXTURE = Chisel.id("textures/gui/autochisel.png");

    private static final int PROG_BAR_LENGTH = 50;
    private static final int POWER_BAR_LENGTH = 160;

    private final ItemStack fakeChisel;

    public AutoChiselScreen(AutoChiselMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, 176, 200);
        this.fakeChisel = new ItemStack(ChiselItems.IRON_CHISEL.get());
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        this.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        if (menu.isActive()) {
            int scaledProg = menu.getProgressScaled(PROG_BAR_LENGTH);
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 63, topPos + 19 + 9, 176, 18, scaledProg + 1, 16, 256, 256);
        }

        if (ChiselConfig.autoChiselPowered) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 7, topPos + 93, 7, 200, 162, 6, 256, 256);
            if (menu.hasEnergy()) {
                graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 8, topPos + 94, 8, 206, menu.getEnergyScaled(POWER_BAR_LENGTH) + 1, 4, 256, 256);
            }
        }

        if (!menu.getSlot(menu.chiselSlot).hasItem()) {
            drawGhostItem(graphics, fakeChisel, leftPos + 80, topPos + 28);
        }
        if (!menu.getSlot(menu.targetSlot).hasItem()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + 80, topPos + 64, 176, 34, 16, 16, 256, 256);
        }
    }

    private void drawGhostItem(GuiGraphicsExtractor graphics, ItemStack stack, int x, int y) {
        graphics.item(stack, x, y);
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, x, y, x - leftPos, y - topPos, 16, 16, 256, 256);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, imageWidth / 2 - font.width(title) / 2, 6, 0x404040, false);
        graphics.text(font, playerInventoryTitle, 8, imageHeight - 96 + 2, 0x404040, false);

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
                graphics.setComponentTooltipForNextFrame(font, tooltip, localMouseX, localMouseY);
            }
        }
    }
}
