package com.leclowndu93150.chisel.client.gui;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.leclowndu93150.chisel.carving.ChiselModeRegistry;
import com.leclowndu93150.chisel.inventory.HitechChiselMenu;
import com.leclowndu93150.chisel.item.ItemChisel;
import com.leclowndu93150.chisel.network.ChiselButtonPayload;
import com.leclowndu93150.chisel.network.ChiselModePayload;
import com.leclowndu93150.chisel.network.HitechSettingsPayload;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;

public class HitechChiselScreen extends AbstractContainerScreen<HitechChiselMenu> {

    private static final ResourceLocation TEXTURE = Chisel.id("textures/gui/chiselguihitech.png");
    public static final int GUI_WIDTH = 256;
    public static final int GUI_HEIGHT = 220;

    private static final int PREVIEW_X = 8;
    private static final int PREVIEW_Y = 14;
    private static final int PREVIEW_WIDTH = 74;
    private static final int PREVIEW_HEIGHT = 74;

    private static final int MODE_BUTTON_TOP = 140;
    private static final int PREVIEW_BUTTON_X = 8;
    private static final int PREVIEW_BUTTON_Y = 91;
    private static final int CHISEL_BUTTON_Y = 113;
    private static final int BUTTON_WIDTH = 76;
    private static final int BUTTON_HEIGHT = 20;

    private static final int HIGHLIGHT_SELECTION_U = 0;
    private static final int HIGHLIGHT_SELECTION_V = 220;
    private static final int HIGHLIGHT_TARGET_U = 36;
    private static final int HIGHLIGHT_TARGET_V = 220;

    private float rotX = 165.0F;
    private float rotY = 0.0F;
    private float zoom = 1.0F;
    private float momentumX = 0.0F;
    private float momentumY = 0.0F;
    private PreviewMode previewMode = PreviewMode.SINGLE;
    private boolean rotate = false;
    private boolean isDragging = false;
    private double lastMouseX;
    private double lastMouseY;

    public HitechChiselScreen(HitechChiselMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(Button.builder(Component.translatable("chisel.button.preview"), this::onPreviewModeClick)
                .bounds(leftPos + PREVIEW_BUTTON_X, topPos + PREVIEW_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        addRenderableWidget(Button.builder(Component.translatable("chisel.button.chisel"), this::onChiselClick)
                .bounds(leftPos + PREVIEW_BUTTON_X, topPos + CHISEL_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        addModeButtons();
    }

    private void addModeButtons() {
        int buttonX = leftPos + PREVIEW_BUTTON_X;
        int buttonY = topPos + MODE_BUTTON_TOP;
        int modesAdded = 0;

        for (IChiselMode mode : ChiselModeRegistry.INSTANCE.getAllModes()) {
            if (modesAdded >= 4) break;

            final IChiselMode finalMode = mode;
            addRenderableWidget(new ButtonChiselMode(
                    buttonX + (modesAdded % 2) * 38,
                    buttonY + (modesAdded / 2) * 22,
                    20, 20,
                    mode,
                    button -> onModeButtonClick(finalMode)
            ));
            modesAdded++;
        }
    }

    private int getChiselSlot() {
        return menu.getHand() == InteractionHand.MAIN_HAND
                ? minecraft.player.getInventory().selected
                : 40; // Offhand slot
    }

    private void onPreviewModeClick(Button button) {
        previewMode = PreviewMode.values()[(previewMode.ordinal() + 1) % PreviewMode.values().length];
        // Send to server
        PacketDistributor.sendToServer(new HitechSettingsPayload(previewMode, rotate, getChiselSlot()));
    }

    private void onChiselClick(Button button) {
        // Collect all slots from player inventory that should be chiseled
        int targetSlot = menu.getTargetSlot();
        if (targetSlot < 0) return;

        ItemStack targetItem = menu.getTargetItem();
        if (targetItem.isEmpty()) return;

        // Gather all player inventory slots (excluding armor)
        int[] slots = new int[36];
        for (int i = 0; i < 36; i++) {
            slots[i] = i;
        }
        PacketDistributor.sendToServer(new ChiselButtonPayload(slots));
    }

    private void onModeButtonClick(IChiselMode mode) {
        ItemStack chisel = menu.getChiselStack();
        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            itemChisel.setMode(chisel, mode);
            // Send to server
            PacketDistributor.sendToServer(new ChiselModePayload(getChiselSlot(), mode));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        if (!isDragging) {
            rotX += momentumX;
            rotY += momentumY;
            momentumX *= 0.98F;
            momentumY *= 0.98F;
        }

        render3DPreview(graphics, partialTick);
        renderSlotHighlights(graphics);
    }

    private void render3DPreview(GuiGraphics graphics, float partialTick) {
        ItemStack selectedItem = getSelectedItem();
        if (selectedItem.isEmpty() || !(selectedItem.getItem() instanceof BlockItem blockItem)) {
            return;
        }

        BlockState state = blockItem.getBlock().defaultBlockState();
        int centerX = leftPos + PREVIEW_X + PREVIEW_WIDTH / 2;
        int centerY = topPos + PREVIEW_Y + PREVIEW_HEIGHT / 2;

        graphics.enableScissor(
                leftPos + PREVIEW_X,
                topPos + PREVIEW_Y,
                leftPos + PREVIEW_X + PREVIEW_WIDTH,
                topPos + PREVIEW_Y + PREVIEW_HEIGHT
        );

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 100);
        poseStack.scale(30 * zoom, -30 * zoom, 30 * zoom);
        poseStack.mulPose(Axis.XP.rotationDegrees(rotX));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
        poseStack.translate(-0.5, -0.5, -0.5);

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        BakedModel model = blockRenderer.getBlockModel(state);

        // Render for multiple render types to support transparent/cutout blocks
        for (RenderType renderType : model.getRenderTypes(state, minecraft.level.random, net.neoforged.neoforge.client.model.data.ModelData.EMPTY)) {
            blockRenderer.getModelRenderer().renderModel(
                    poseStack.last(),
                    bufferSource.getBuffer(renderType),
                    state,
                    model,
                    1.0F, 1.0F, 1.0F,
                    LightTexture.FULL_BRIGHT,
                    OverlayTexture.NO_OVERLAY,
                    net.neoforged.neoforge.client.model.data.ModelData.EMPTY,
                    renderType
            );
        }

        bufferSource.endBatch();
        poseStack.popPose();
        graphics.disableScissor();
    }

    private void renderSlotHighlights(GuiGraphics graphics) {
        int selectedSlot = menu.getSelectedSlot();
        int targetSlot = menu.getTargetSlot();

        for (int i = 0; i < menu.getSelectionSize(); i++) {
            Slot slot = menu.getSlot(i);
            int x = leftPos + slot.x - 1;
            int y = topPos + slot.y - 1;

            if (i == selectedSlot) {
                graphics.blit(TEXTURE, x, y, HIGHLIGHT_SELECTION_U, HIGHLIGHT_SELECTION_V, 18, 18);
            } else if (i == targetSlot) {
                graphics.blit(TEXTURE, x, y, HIGHLIGHT_TARGET_U, HIGHLIGHT_TARGET_V, 18, 18);
            }
        }
    }

    private ItemStack getSelectedItem() {
        int selected = menu.getSelectedSlot();
        if (selected >= 0 && selected < menu.getSelectionSize()) {
            return menu.getSlot(selected).getItem();
        }
        return ItemStack.EMPTY;
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isInPreviewArea(mouseX, mouseY)) {
            isDragging = true;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            return true;
        }

        Slot slot = findSlot(mouseX, mouseY);
        if (slot != null && slot.index < menu.getSelectionSize()) {
            if (button == 0) {
                menu.setSelectedSlot(slot.index);
            } else if (button == 1) {
                menu.setTargetSlot(slot.index);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (isDragging) {
            isDragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging) {
            double dx = mouseX - lastMouseX;
            double dy = mouseY - lastMouseY;
            lastMouseX = mouseX;
            lastMouseY = mouseY;

            rotY += (float) dx;
            rotX += (float) dy;
            momentumX = (float) dy * 0.5F;
            momentumY = (float) dx * 0.5F;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isInPreviewArea(mouseX, mouseY)) {
            zoom += (float) scrollY * 0.1F;
            zoom = Math.max(0.5F, Math.min(3.0F, zoom));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean isInPreviewArea(double mouseX, double mouseY) {
        return mouseX >= leftPos + PREVIEW_X && mouseX < leftPos + PREVIEW_X + PREVIEW_WIDTH
                && mouseY >= topPos + PREVIEW_Y && mouseY < topPos + PREVIEW_Y + PREVIEW_HEIGHT;
    }

    private Slot findSlot(double mouseX, double mouseY) {
        for (Slot slot : menu.slots) {
            if (isHovering(slot.x, slot.y, 16, 16, mouseX, mouseY)) {
                return slot;
            }
        }
        return null;
    }

    /**
     * Preview modes for the 3D display.
     */
    public enum PreviewMode {
        SINGLE,
        PANEL_3X3,
        COLUMN_3X1
    }
}
