package com.leclowndu93150.chisel.client.gui;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.leclowndu93150.chisel.carving.ChiselModeRegistry;
import com.leclowndu93150.chisel.inventory.HitechChiselMenu;
import com.leclowndu93150.chisel.item.ItemChisel;
import com.leclowndu93150.chisel.network.ChiselNetwork;
import com.leclowndu93150.chisel.network.server.ChiselButtonPacket;
import com.leclowndu93150.chisel.network.server.ChiselModePacket;
import com.leclowndu93150.chisel.network.server.HitechSettingsPacket;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;

public class HitechChiselScreen extends AbstractContainerScreen<HitechChiselMenu> {

    private static final ResourceLocation TEXTURE = Chisel.id("textures/gui/chiselguihitech.png");
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
    private int clickButton;
    private int clickX, clickY;
    private long lastDragTime;

    private Button chiselButton;

    public HitechChiselScreen(HitechChiselMenu menu, Inventory playerInv, Component title) {
        super(menu, playerInv, title);
        this.imageWidth = GUI_WIDTH;
        this.imageHeight = GUI_HEIGHT;
    }

    @Override
    protected void init() {
        super.init();

        addRenderableWidget(Button.builder(getPreviewModeText(), this::onPreviewModeClick)
                .bounds(leftPos + PREVIEW_BUTTON_X, topPos + PREVIEW_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build());

        chiselButton = addRenderableWidget(new Button.Builder(Component.translatable("chisel.button.chisel"), this::onChiselClick)
                .bounds(leftPos + PREVIEW_BUTTON_X, topPos + CHISEL_BUTTON_Y, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build(builder -> new Button(builder) {
                    @Override
                    public void playDownSound(SoundManager handler) {
                        // don't play anything
                    }
                }));

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
                ? minecraft.player.getInventory().selected
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
        if (hasShiftDown()) {
            List<Slot> duplicates = menu.getSelectionDuplicates();
            slots = new int[1 + duplicates.size()];
            slots[0] = selection.index;
            for (int i = 0; i < duplicates.size(); i++) {
                slots[i + 1] = duplicates.get(i).index;
            }
        } else {
            slots = new int[]{selection.index};
        }

        ChiselNetwork.sendToServer(new ChiselButtonPacket(slots));

        menu.chiselSlots(slots);
    }

    private void onModeButtonClick(IChiselMode mode) {
        ItemStack chisel = menu.getChisel();
        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            itemChisel.setMode(chisel, mode);
            ChiselNetwork.sendToServer(new ChiselModePacket(getChiselSlot(), mode));

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
        ChiselNetwork.sendToServer(new HitechSettingsPacket((byte) previewMode.ordinal(), autoRotate, getChiselSlot()));
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        if (chiselButton != null) {
            if (hasShiftDown()) {
                chiselButton.setMessage(Component.translatable("chisel.button.chisel_all").withStyle(ChatFormatting.YELLOW));
            } else {
                chiselButton.setMessage(Component.translatable("chisel.button.chisel"));
            }

            chiselButton.active = menu.getSelection() != null && menu.getSelection().hasItem()
                    && menu.getTarget() != null && menu.getTarget().hasItem();
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        renderSlotHighlights(graphics);

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

    private void renderSlotHighlights(GuiGraphics graphics) {
        Slot selection = menu.getSelection();
        if (selection != null && selection.hasItem()) {
            drawSlotHighlight(graphics, selection, HIGHLIGHT_SELECTION_U);

            for (Slot dup : menu.getSelectionDuplicates()) {
                drawSlotHighlight(graphics, dup, hasShiftDown() ? HIGHLIGHT_SELECTION_U : HIGHLIGHT_DUPLICATE_U);
            }
        }

        Slot target = menu.getTarget();
        if (target != null && target.hasItem()) {
            drawSlotHighlight(graphics, target, HIGHLIGHT_TARGET_U);
        }
    }

    private void drawSlotHighlight(GuiGraphics graphics, Slot slot, int u) {
        graphics.blit(TEXTURE, leftPos + slot.x - 1, topPos + slot.y - 1, u, HIGHLIGHT_V, 18, 18);
    }

    private void render3DPreview(GuiGraphics graphics, float partialTick) {
        ItemStack previewItem = menu.getTargetItem();
        if (previewItem.isEmpty()) {
            previewItem = menu.getSelectionStack();
        }
        if (previewItem == null || previewItem.isEmpty() || !(previewItem.getItem() instanceof BlockItem blockItem)) {
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

        if (!panelClicked) {
            rotX += (float) momentumX;
            rotY += (float) momentumY;
            momentumX *= 0.98;
            momentumY *= 0.98;
            if (Math.abs(momentumX) < 0.05) momentumX = 0;
            if (Math.abs(momentumY) < 0.05) momentumY = 0;
        }

        rotX = Math.max(-90, Math.min(90, rotX));

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(centerX, centerY, 100);

        float scale = 30 * zoom * previewMode.getScale();
        poseStack.scale(scale, -scale, scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(rotX));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));

        poseStack.translate(-previewMode.getCenterX(), -previewMode.getCenterY(), -0.5);

        BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        BakedModel model = blockRenderer.getBlockModel(state);

        PreviewBlockGetter blockGetter = new PreviewBlockGetter(state, previewMode.getPositions());

        for (int[] pos : previewMode.getPositions()) {
            poseStack.pushPose();
            poseStack.translate(pos[0], pos[1], pos[2]);

            BlockPos blockPos = new BlockPos(pos[0], pos[1], pos[2]);
            ModelData modelData = model.getModelData(blockGetter, blockPos, state, ModelData.EMPTY);

            for (RenderType renderType : model.getRenderTypes(state, minecraft.level.random, modelData)) {
                renderModelWithFaceCulling(
                        poseStack.last(),
                        bufferSource.getBuffer(renderType),
                        state,
                        model,
                        blockGetter,
                        blockPos,
                        modelData,
                        renderType
                );
            }

            poseStack.popPose();
        }

        bufferSource.endBatch();
        poseStack.popPose();
        graphics.disableScissor();
    }

    /**
     * Renders a block model with proper face culling for glass-like blocks.
     * Checks skipRendering for each face direction before rendering quads for that face.
     */
    private void renderModelWithFaceCulling(
            PoseStack.Pose pose,
            VertexConsumer buffer,
            BlockState state,
            BakedModel model,
            PreviewBlockGetter blockGetter,
            BlockPos pos,
            ModelData modelData,
            RenderType renderType
    ) {
        RandomSource random = RandomSource.create();

        for (Direction direction : Direction.values()) {
            random.setSeed(42L);
            BlockPos adjacentPos = pos.relative(direction);
            BlockState adjacentState = blockGetter.getBlockState(adjacentPos);

            if (state.skipRendering(adjacentState, direction)) {
                continue;
            }

            for (BakedQuad quad : model.getQuads(state, direction, random, modelData, renderType)) {
                buffer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            }
        }

        random.setSeed(42L);
        for (BakedQuad quad : model.getQuads(state, null, random, modelData, renderType)) {
            buffer.putBulkData(pose, quad, 1.0F, 1.0F, 1.0F, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);

        Component previewLabel = Component.translatable("chisel.preview");
        graphics.drawString(font, previewLabel,
                leftPos + PREVIEW_X + (PREVIEW_WIDTH - font.width(previewLabel)) / 2,
                topPos + PREVIEW_Y - 9, 0x404040, false);

        drawButtonTooltips(graphics, mouseX, mouseY);
    }

    protected void drawButtonTooltips(GuiGraphics graphics, int mouseX, int mouseY) {
        for (var widget : children()) {
            if (widget instanceof ButtonChiselMode button && button.isHovered()) {
                IChiselMode mode = button.getMode();
                List<Component> ttLines = List.of(
                        mode.getLocalizedName(),
                        mode.getLocalizedDescription().copy().withStyle(ChatFormatting.GRAY)
                );
                graphics.renderComponentTooltip(font, ttLines, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    public void renderSlot(GuiGraphics graphics, Slot slot) {
        if (slot == menu.getInputSlot()) {
            return;
        }
        super.renderSlot(graphics, slot);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        Slot inputSlot = menu.getInputSlot();
        if (x == inputSlot.x && y == inputSlot.y) {
            return false;
        }
        return super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
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

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (panelClicked) {
            lastDragTime = System.currentTimeMillis();
            panelClicked = false;
            initRotX = rotX;
            initRotY = rotY;
            initZoom = zoom;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (panelClicked) {
            if (clickButton == 0) {
                rotX = Math.max(-90, Math.min(90, initRotX + ((float) mouseY - clickY)));
                rotY = initRotY + ((float) mouseX - clickX);
            } else if (clickButton == 1) {
                zoom = Math.max(0.5f, initZoom + (clickY - (float) mouseY) * 0.01f);
            }
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (isInPreviewArea(mouseX, mouseY)) {
            zoom += (float) delta * 0.1F;
            zoom = Math.max(0.5F, Math.min(3.0F, zoom));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    private boolean isInPreviewArea(double mouseX, double mouseY) {
        return mouseX >= leftPos + PREVIEW_X && mouseX < leftPos + PREVIEW_X + PREVIEW_WIDTH
                && mouseY >= topPos + PREVIEW_Y && mouseY < topPos + PREVIEW_Y + PREVIEW_HEIGHT;
    }

    public enum PreviewMode {
        PANEL(0.5f, 1.5f, 2.0f, new int[][]{
                {0, 1, 0}, {1, 1, 0}, {2, 1, 0},
                {0, 2, 0}, {1, 2, 0}, {2, 2, 0},
                {0, 3, 0}, {1, 3, 0}, {2, 3, 0}
        }),
        HOLLOW(0.5f, 1.5f, 2.0f, new int[][]{
                {0, 1, 0}, {1, 1, 0}, {2, 1, 0},
                {0, 2, 0}, /*hole*/  {2, 2, 0},
                {0, 3, 0}, {1, 3, 0}, {2, 3, 0}
        }),
        PLUS(0.6f, 1.0f, 2.0f, new int[][]{
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
