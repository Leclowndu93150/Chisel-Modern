package com.leclowndu93150.chisel.client.gui;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.client.render.BlockPreviewRenderState;
import com.leclowndu93150.chisel.mixin.GuiGraphicsExtractorAccessor;
import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.leclowndu93150.chisel.carving.ChiselModeRegistry;
import com.leclowndu93150.chisel.inventory.HitechChiselMenu;
import com.leclowndu93150.chisel.item.ItemChisel;
import com.leclowndu93150.chisel.network.server.ChiselButtonPayload;
import com.leclowndu93150.chisel.network.server.ChiselModePayload;
import com.leclowndu93150.chisel.network.server.ChiselScrollPayload;
import com.leclowndu93150.chisel.network.server.HitechSettingsPayload;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import com.mojang.blaze3d.vertex.QuadInstance;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.minecraft.core.BlockPos;

import java.util.List;

public class HitechChiselScreen extends AbstractContainerScreen<HitechChiselMenu> {

    private static final Identifier TEXTURE = Chisel.id("textures/gui/chiselguihitech.png");
    public static final int GUI_WIDTH = 256;
    public static final int GUI_HEIGHT = 220;

    private static final int PREVIEW_X = 8;
    private static final int PREVIEW_Y = 14;
    private static final int PREVIEW_WIDTH = 74;
    private static final int PREVIEW_HEIGHT = 74;

    private static final int PREVIEW_BUTTON_X = 7;
    private static final int PREVIEW_BUTTON_Y = 91;
    private static final int CHISEL_BUTTON_Y = 113;
    private static final int BUTTON_WIDTH = 76;
    private static final int BUTTON_HEIGHT = 20;

    private static final int HIGHLIGHT_SELECTION_U = 0;
    private static final int HIGHLIGHT_DUPLICATE_U = 18;
    private static final int HIGHLIGHT_TARGET_U = 36;
    private static final int HIGHLIGHT_V = 220;

    private static final int SCROLLBAR_X = 251;
    private static final int SCROLLBAR_SCROLL_Y = 8;
    private static final int SCROLLBAR_WIDTH = 4;
    private static final int SCROLLBAR_SCROLL_HEIGHT = 126;
    private static final int SCROLLBAR_THUMB_MIN_HEIGHT = 10;

    private float rotX = 25.0F;
    private float rotY = -45.0F;
    private float zoom = 1.0F;
    private float initRotX, initRotY, initZoom;
    private float prevRotX, prevRotY;
    private double momentumX = 0;
    private double momentumY = 0;
    private PreviewMode previewMode = PreviewMode.PANEL;
    private boolean autoRotate = true;
    private boolean panelClicked = false;
    private boolean scrollbarDragging = false;
    private double scrollbarDragOffset = 0;
    private int clickButton;
    private int clickX, clickY;
    private long lastDragTime;

    private Button chiselButton;

    public HitechChiselScreen(HitechChiselMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title, GUI_WIDTH, GUI_HEIGHT);
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(Button.builder(getPreviewModeText(), this::onPreviewModeClick)
                .bounds(leftPos + PREVIEW_BUTTON_X, topPos + PREVIEW_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        chiselButton = addRenderableWidget(Button.builder(Component.translatable("chisel.button.chisel"), this::onChiselClick)
                .bounds(leftPos + PREVIEW_BUTTON_X, topPos + CHISEL_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        addModeButtons();

        ItemStack chisel = menu.getChisel();
        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            previewMode = PreviewMode.values()[Math.min(itemChisel.getPreviewType(chisel), PreviewMode.values().length - 1)];
            autoRotate = itemChisel.getRotate(chisel);
        }
    }

    private Component getPreviewModeText() {
        return Component.literal("< ").append(Component.translatable("chisel.preview." + previewMode.name().toLowerCase())).append(" >");
    }

    private void addModeButtons() {
        int buttonX = leftPos + 7;
        int buttonY = topPos + 140;
        int modesAdded = 0;

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

            final IChiselMode finalMode = mode;
            ButtonChiselMode button = new ButtonChiselMode(
                    buttonX + (modesAdded % 2) * 38,
                    buttonY + (modesAdded / 2) * 22,
                    20, 20,
                    mode,
                    b -> onModeButtonClick(finalMode)
            );

            if (mode == currentMode) {
                button.active = false;
            }

            addRenderableWidget(button);
            modesAdded++;
        }
    }

    private int getChiselSlot() {
        return menu.getHand() == InteractionHand.MAIN_HAND
                ? minecraft.player.getInventory().getSelectedSlot()
                : 40;
    }

    private void onPreviewModeClick(Button button) {
        previewMode = PreviewMode.values()[(previewMode.ordinal() + 1) % PreviewMode.values().length];
        button.setMessage(getPreviewModeText());
        saveSettings();
    }

    private void onChiselClick(Button button) {
        Slot selection = menu.getSelection();
        Slot target = menu.getTarget();

        if (selection == null || !selection.hasItem()) return;
        if (target == null || !target.hasItem()) return;

        if (ItemStack.isSameItem(selection.getItem(), target.getItem())) return;

        int[] slots;
        if (minecraft.hasShiftDown()) {
            List<Slot> duplicates = menu.getSelectionDuplicates();
            slots = new int[1 + duplicates.size()];
            slots[0] = selection.index;
            for (int i = 0; i < duplicates.size(); i++) {
                slots[i + 1] = duplicates.get(i).index;
            }
        } else {
            slots = new int[]{selection.index};
        }

        ClientPacketDistributor.sendToServer(new ChiselButtonPayload(slots));

        menu.chiselSlots(slots);
    }

    private void onModeButtonClick(IChiselMode mode) {
        ItemStack chisel = menu.getChisel();
        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            itemChisel.setMode(chisel, mode);
            ClientPacketDistributor.sendToServer(new ChiselModePayload(getChiselSlot(), mode));

            for (var widget : children()) {
                if (widget instanceof ButtonChiselMode modeButton) {
                    modeButton.active = modeButton.getMode() != mode;
                }
            }
        }
    }

    private void saveSettings() {
        ItemStack chisel = menu.getChisel();
        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            itemChisel.setPreviewType(chisel, previewMode.ordinal());
            itemChisel.setRotate(chisel, autoRotate);
        }
        ClientPacketDistributor.sendToServer(new HitechSettingsPayload(previewMode, autoRotate, getChiselSlot()));
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (chiselButton != null) {
            if (minecraft.hasShiftDown()) {
                chiselButton.setMessage(Component.translatable("chisel.button.chisel_all").withStyle(ChatFormatting.YELLOW));
            } else {
                chiselButton.setMessage(Component.translatable("chisel.button.chisel"));
            }

            chiselButton.active = menu.getSelection() != null && menu.getSelection().hasItem()
                    && menu.getTarget() != null && menu.getTarget().hasItem();
        }
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight, 256, 256);

        renderSlotHighlights(graphics);

        if (menu.canScroll()) {
            renderScrollbar(graphics, mouseX, mouseY);
        }

        if (autoRotate && momentumX == 0 && momentumY == 0 && !panelClicked && System.currentTimeMillis() - lastDragTime > 2000) {
            rotY = initRotY + (2 * partialTick);
        }

        if (panelClicked && clickButton == 0) {
            momentumX = rotX - prevRotX;
            momentumY = rotY - prevRotY;
            prevRotX = rotX;
            prevRotY = rotY;
        }

        render3DPreview(graphics, partialTick);
    }

    private void renderSlotHighlights(GuiGraphicsExtractor graphics) {
        Slot selection = menu.getSelection();
        if (selection != null && selection.hasItem()) {
            drawSlotHighlight(graphics, selection, HIGHLIGHT_SELECTION_U);

            for (Slot dup : menu.getSelectionDuplicates()) {
                drawSlotHighlight(graphics, dup, minecraft.hasShiftDown() ? HIGHLIGHT_SELECTION_U : HIGHLIGHT_DUPLICATE_U);
            }
        }

        Slot target = menu.getTarget();
        if (target != null && target.hasItem()) {
            drawSlotHighlight(graphics, target, HIGHLIGHT_TARGET_U);
        }
    }

    private void drawSlotHighlight(GuiGraphicsExtractor graphics, Slot slot, int u) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos + slot.x - 1, topPos + slot.y - 1, u, HIGHLIGHT_V, 18, 18, 256, 256);
    }

    private void render3DPreview(GuiGraphicsExtractor graphics, float partialTick) {
        ItemStack previewItem = menu.getTargetItem();
        if (previewItem.isEmpty()) {
            previewItem = menu.getSelectionStack();
        }
        if (previewItem == null || previewItem.isEmpty() || !(previewItem.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        BlockState state = blockItem.getBlock().defaultBlockState();

        if (!panelClicked) {
            rotX += (float) momentumX;
            rotY += (float) momentumY;
            momentumX *= 0.98;
            momentumY *= 0.98;
            if (Math.abs(momentumX) < 0.05) momentumX = 0;
            if (Math.abs(momentumY) < 0.05) momentumY = 0;
        }

        rotX = Math.max(-90, Math.min(90, rotX));

        int x0 = leftPos + PREVIEW_X;
        int y0 = topPos + PREVIEW_Y;
        int x1 = x0 + PREVIEW_WIDTH;
        int y1 = y0 + PREVIEW_HEIGHT;

        ((GuiGraphicsExtractorAccessor) graphics).chisel$getGuiRenderState()
                .addPicturesInPictureState(new BlockPreviewRenderState(
                        state,
                        previewMode.getPositions(),
                        rotX,
                        rotY,
                        zoom,
                        previewMode.getScale(),
                        previewMode.getCenterX(),
                        previewMode.getCenterY(),
                        x0, y0, x1, y1,
                        null
                ));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        this.extractTooltip(graphics, mouseX, mouseY);

        Component previewLabel = Component.translatable("chisel.preview");
        graphics.text(font, previewLabel,
                leftPos + PREVIEW_X + (PREVIEW_WIDTH - font.width(previewLabel)) / 2,
                topPos + PREVIEW_Y - 9, 0x404040, false);

        drawButtonTooltips(graphics, mouseX, mouseY);
    }

    protected void drawButtonTooltips(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        for (var widget : children()) {
            if (widget instanceof ButtonChiselMode button && button.isHovered()) {
                IChiselMode mode = button.getMode();
                List<Component> ttLines = List.of(
                        mode.getLocalizedName(),
                        mode.getLocalizedDescription().copy().withStyle(ChatFormatting.GRAY)
                );
                graphics.setComponentTooltipForNextFrame(font, ttLines, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
    }

    @Override
    protected void extractSlot(GuiGraphicsExtractor graphics, Slot slot, int mouseX, int mouseY) {
        if (slot == menu.getInputSlot()) {
            return;
        }
        super.extractSlot(graphics, slot, mouseX, mouseY);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        Slot inputSlot = menu.getInputSlot();
        if (x == inputSlot.x && y == inputSlot.y) {
            return false;
        }
        return super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    private void renderScrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        int x = leftPos + SCROLLBAR_X;
        int y = topPos + SCROLLBAR_SCROLL_Y;

        graphics.fill(x, y, x + SCROLLBAR_WIDTH, y + SCROLLBAR_SCROLL_HEIGHT, 0xFF2B2B2B);

        int maxScroll = menu.getMaxScrollRow();
        int thumbHeight = Math.max(SCROLLBAR_THUMB_MIN_HEIGHT,
                (int) ((float) SCROLLBAR_SCROLL_HEIGHT * SCROLLBAR_SCROLL_HEIGHT /
                        (SCROLLBAR_SCROLL_HEIGHT + maxScroll * 18)));
        int thumbY = maxScroll > 0
                ? y + (int) ((float) menu.getScrollRow() / maxScroll * (SCROLLBAR_SCROLL_HEIGHT - thumbHeight))
                : y;

        boolean hovered = mouseX >= x && mouseX < x + SCROLLBAR_WIDTH
                && mouseY >= thumbY && mouseY < thumbY + thumbHeight;

        int thumbColor = scrollbarDragging ? 0xFFD0D0D0 : (hovered ? 0xFFA0A0A0 : 0xFF808080);
        graphics.fill(x, thumbY, x + SCROLLBAR_WIDTH, thumbY + thumbHeight, thumbColor);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mouseX = event.x();
        double mouseY = event.y();
        int button = event.button();
        if (menu.canScroll() && button == 0) {
            int x = leftPos + SCROLLBAR_X;
            int y = topPos + SCROLLBAR_SCROLL_Y;
            if (mouseX >= x && mouseX < x + SCROLLBAR_WIDTH && mouseY >= y && mouseY < y + SCROLLBAR_SCROLL_HEIGHT) {
                scrollbarDragging = true;
                int maxScroll = menu.getMaxScrollRow();
                int thumbHeight = Math.max(SCROLLBAR_THUMB_MIN_HEIGHT,
                        (int) ((float) SCROLLBAR_SCROLL_HEIGHT * SCROLLBAR_SCROLL_HEIGHT /
                                (SCROLLBAR_SCROLL_HEIGHT + maxScroll * 18)));
                int thumbY = maxScroll > 0
                        ? y + (int) ((float) menu.getScrollRow() / maxScroll * (SCROLLBAR_SCROLL_HEIGHT - thumbHeight))
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

        if (isInPreviewArea(mouseX, mouseY)) {
            panelClicked = true;
            clickButton = button;
            clickX = (int) mouseX;
            clickY = (int) mouseY;
            initRotX = rotX;
            initRotY = rotY;
            initZoom = zoom;
            prevRotX = rotX;
            prevRotY = rotY;
            momentumX = 0;
            momentumY = 0;
            return true;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (scrollbarDragging) {
            scrollbarDragging = false;
            return true;
        }
        if (panelClicked) {
            lastDragTime = System.currentTimeMillis();
            panelClicked = false;
            initRotX = rotX;
            initRotY = rotY;
            initZoom = zoom;
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dragX, double dragY) {
        double mouseX = event.x();
        double mouseY = event.y();
        if (scrollbarDragging) {
            updateScrollFromMouse(mouseY);
            return true;
        }
        if (panelClicked) {
            if (clickButton == 0) {
                rotX = Math.max(-90, Math.min(90, initRotX + ((float) mouseY - clickY)));
                rotY = initRotY + ((float) mouseX - clickX);
            } else if (clickButton == 1) {
                zoom = Math.max(0.5f, initZoom + (clickY - (float) mouseY) * 0.01f);
            }
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isInPreviewArea(mouseX, mouseY)) {
            zoom += (float) scrollY * 0.1F;
            zoom = Math.max(0.5F, Math.min(3.0F, zoom));
            return true;
        }
        if (menu.canScroll()) {
            scrollTo(menu.getScrollRow() - (int) Math.signum(scrollY));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean isInPreviewArea(double mouseX, double mouseY) {
        return mouseX >= leftPos + PREVIEW_X && mouseX < leftPos + PREVIEW_X + PREVIEW_WIDTH
                && mouseY >= topPos + PREVIEW_Y && mouseY < topPos + PREVIEW_Y + PREVIEW_HEIGHT;
    }

    private void scrollTo(int row) {
        menu.setScrollRow(row);
        ClientPacketDistributor.sendToServer(new ChiselScrollPayload(menu.getScrollRow()));
    }

    private void updateScrollFromMouse(double mouseY) {
        int y = topPos + SCROLLBAR_SCROLL_Y;
        int maxScroll = menu.getMaxScrollRow();
        int thumbHeight = Math.max(SCROLLBAR_THUMB_MIN_HEIGHT,
                (int) ((float) SCROLLBAR_SCROLL_HEIGHT * SCROLLBAR_SCROLL_HEIGHT /
                        (SCROLLBAR_SCROLL_HEIGHT + maxScroll * 18)));
        double relativeY = mouseY - scrollbarDragOffset - y;
        double scrollable = SCROLLBAR_SCROLL_HEIGHT - thumbHeight;
        if (scrollable > 0) {
            double ratio = relativeY / scrollable;
            scrollTo((int) Math.round(ratio * maxScroll));
        }
    }

    public enum PreviewMode {
        PANEL(0.5f, 1.5f, 2.5f, new int[][]{
                {0, 1, 0}, {1, 1, 0}, {2, 1, 0},
                {0, 2, 0}, {1, 2, 0}, {2, 2, 0},
                {0, 3, 0}, {1, 3, 0}, {2, 3, 0}
        }),
        HOLLOW(0.5f, 1.5f, 2.5f, new int[][]{
                {0, 1, 0}, {1, 1, 0}, {2, 1, 0},
                {0, 2, 0}, /*hole*/  {2, 2, 0},
                {0, 3, 0}, {1, 3, 0}, {2, 3, 0}
        }),
        PLUS(0.6f, 1.5f, 2.5f, new int[][]{
                {1, 1, 0},
                {1, 2, 0}, {2, 2, 0}, {0, 2, 0},
                {1, 3, 0}
        }),
        SINGLE(1.0f, 0.5f, 0.5f, new int[][]{{0, 0, 0}});

        private final float scale;
        private final float centerX;
        private final float centerY;
        private final int[][] positions;

        PreviewMode(float scale, float centerX, float centerY, int[][] positions) {
            this.scale = scale;
            this.centerX = centerX;
            this.centerY = centerY;
            this.positions = positions;
        }

        public float getScale() {
            return scale;
        }

        public float getCenterX() {
            return centerX;
        }

        public float getCenterY() {
            return centerY;
        }

        public int[][] getPositions() {
            return positions;
        }
    }
}
