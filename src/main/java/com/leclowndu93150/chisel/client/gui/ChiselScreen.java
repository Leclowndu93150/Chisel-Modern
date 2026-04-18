package com.leclowndu93150.chisel.client.gui;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.leclowndu93150.chisel.carving.ChiselModeRegistry;
import com.leclowndu93150.chisel.inventory.ChiselMenu;
import com.leclowndu93150.chisel.item.ItemChisel;
import com.leclowndu93150.chisel.network.server.ChiselModePayload;
import com.leclowndu93150.chisel.network.server.ChiselScrollPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

public class ChiselScreen extends AbstractContainerScreen<ChiselMenu> {

    private static final Identifier TEXTURE = Chisel.id("textures/gui/chisel2gui.png");
    public static final int GUI_WIDTH = 252;
    public static final int GUI_HEIGHT = 202;

    private static final int SCROLLBAR_X = 243;
    private static final int SCROLLBAR_Y = 8;
    private static final int SCROLLBAR_WIDTH = 6;
    private static final int SCROLLBAR_HEIGHT = 108;
    private static final int SCROLLBAR_THUMB_MIN_HEIGHT = 10;

    private boolean scrollbarDragging = false;
    private double scrollbarDragOffset = 0;

    public ChiselScreen(ChiselMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, GUI_WIDTH, GUI_HEIGHT);
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
                    ? minecraft.player.getInventory().getSelectedSlot()
                    : 40; // Offhand slot
            ClientPacketDistributor.sendToServer(new ChiselModePayload(slot, mode));
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        Slot inputSlot = menu.getInputSlot();
        if (inputSlot.getItem().isEmpty()) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + inputSlot.x - 16, topPos + inputSlot.y - 16, 0, imageHeight, 48, 48, 256, 256);
        }

        if (menu.canScroll()) {
            renderScrollbar(graphics, mouseX, mouseY);
        }
    }

    private void renderScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = leftPos + SCROLLBAR_X;
        int y = topPos + SCROLLBAR_Y;

        graphics.fill(x, y, x + SCROLLBAR_WIDTH, y + SCROLLBAR_HEIGHT, 0xFF2B2B2B);

        int maxScroll = menu.getMaxScrollRow();
        int thumbHeight = Math.max(SCROLLBAR_THUMB_MIN_HEIGHT,
                (int) ((float) SCROLLBAR_HEIGHT * SCROLLBAR_HEIGHT /
                        (SCROLLBAR_HEIGHT + maxScroll * 18)));
        int thumbY = maxScroll > 0
                ? y + (int) ((float) menu.getScrollRow() / maxScroll * (SCROLLBAR_HEIGHT - thumbHeight))
                : y;

        boolean hovered = mouseX >= x && mouseX < x + SCROLLBAR_WIDTH
                && mouseY >= thumbY && mouseY < thumbY + thumbHeight;

        int thumbColor = scrollbarDragging ? 0xFFD0D0D0 : (hovered ? 0xFFA0A0A0 : 0xFF808080);
        graphics.fill(x, thumbY, x + SCROLLBAR_WIDTH, thumbY + thumbHeight, thumbColor);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        this.extractTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        List<FormattedCharSequence> lines = font.split(title, 40);
        int y = 60;
        for (FormattedCharSequence s : lines) {
            graphics.text(font, s, 32 - font.width(s) / 2, y, 0x404040, false);
            y += 10;
        }

        drawButtonTooltips(graphics, mouseX, mouseY);
    }

    protected void drawButtonTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        for (Renderable widget : renderables) {
            if (widget instanceof ButtonChiselMode button && button.isHovered()) {
                IChiselMode mode = button.getMode();
                List<Component> ttLines = List.of(
                        mode.getLocalizedName(),
                        mode.getLocalizedDescription().copy().withStyle(ChatFormatting.GRAY)
                );
                graphics.setComponentTooltipForNextFrame(font, ttLines, mouseX - leftPos, mouseY - topPos);
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
    protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
        if (slot == menu.getInputSlot()) {
            var pose = graphics.pose();
            pose.pushMatrix();

            float centerX = slot.x + 8;
            float centerY = slot.y + 8;

            pose.translate(centerX, centerY);
            pose.scale(2.0f, 2.0f);
            pose.translate(-centerX, -centerY);

            super.extractSlot(graphics, slot, mouseX, mouseY);

            pose.popMatrix();
        } else {
            super.extractSlot(graphics, slot, mouseX, mouseY);
        }
    }

    @Override
    protected void extractTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractTooltip(graphics, mouseX, mouseY);
    }

    private void scrollTo(int row) {
        menu.setScrollRow(row);
        ClientPacketDistributor.sendToServer(new ChiselScrollPayload(menu.getScrollRow()));
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (menu.canScroll()) {
            scrollTo(menu.getScrollRow() - (int) Math.signum(scrollY));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (menu.canScroll() && button == 0) {
            int x = leftPos + SCROLLBAR_X;
            int y = topPos + SCROLLBAR_Y;
            if (mouseX >= x && mouseX < x + SCROLLBAR_WIDTH && mouseY >= y && mouseY < y + SCROLLBAR_HEIGHT) {
                scrollbarDragging = true;
                int maxScroll = menu.getMaxScrollRow();
                int thumbHeight = Math.max(SCROLLBAR_THUMB_MIN_HEIGHT,
                        (int) ((float) SCROLLBAR_HEIGHT * SCROLLBAR_HEIGHT /
                                (SCROLLBAR_HEIGHT + maxScroll * 18)));
                int thumbY = maxScroll > 0
                        ? y + (int) ((float) menu.getScrollRow() / maxScroll * (SCROLLBAR_HEIGHT - thumbHeight))
                        : y;
                if (mouseY >= thumbY && mouseY < thumbY + thumbHeight) {
                    scrollbarDragOffset = mouseY - thumbY;
                } else {
                    scrollbarDragOffset = thumbHeight / 2.0;
                    updateScrollFromMouse(mouseY);
                }
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (scrollbarDragging) {
            scrollbarDragging = false;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        if (scrollbarDragging) {
            updateScrollFromMouse(event.y());
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    private void updateScrollFromMouse(double mouseY) {
        int y = topPos + SCROLLBAR_Y;
        int maxScroll = menu.getMaxScrollRow();
        int thumbHeight = Math.max(SCROLLBAR_THUMB_MIN_HEIGHT,
                (int) ((float) SCROLLBAR_HEIGHT * SCROLLBAR_HEIGHT /
                        (SCROLLBAR_HEIGHT + maxScroll * 18)));
        double relativeY = mouseY - scrollbarDragOffset - y;
        double scrollable = SCROLLBAR_HEIGHT - thumbHeight;
        if (scrollable > 0) {
            double ratio = relativeY / scrollable;
            scrollTo((int) Math.round(ratio * maxScroll));
        }
    }
}
