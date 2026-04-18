package com.leclowndu93150.chisel.client.render;

import com.leclowndu93150.chisel.client.gui.PreviewBlockGetter;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.QuadInstance;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockStateModelSet;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelPart;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class BlockPreviewRenderer extends PictureInPictureRenderer<BlockPreviewRenderState> {

    public BlockPreviewRenderer(MultiBufferSource.BufferSource bufferSource) {
        super(bufferSource);
    }

    @Override
    public Class<BlockPreviewRenderState> getRenderStateClass() {
        return BlockPreviewRenderState.class;
    }

    @Override
    protected float getTranslateY(int height, int guiScale) {
        return height / 2.0F;
    }

    @Override
    protected void renderToTexture(BlockPreviewRenderState state, PoseStack poseStack) {
        BlockState blockState = state.blockState();

        float scale = 30 * state.zoom() * state.previewScale();
        poseStack.scale(scale, scale, scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.XP.rotationDegrees(state.rotX()));
        poseStack.mulPose(Axis.YP.rotationDegrees(state.rotY()));
        poseStack.translate(-state.centerX(), -state.centerY(), -0.5);

        BlockStateModelSet modelSet = Minecraft.getInstance().getModelManager().getBlockStateModelSet();
        BlockStateModel model = modelSet.get(blockState);
        PreviewBlockGetter blockGetter = new PreviewBlockGetter(blockState, state.positions());
        RandomSource random = RandomSource.create();
        QuadInstance quadInstance = new QuadInstance();

        for (int[] pos : state.positions()) {
            poseStack.pushPose();
            poseStack.translate(pos[0], pos[1], pos[2]);

            BlockPos blockPos = new BlockPos(pos[0], pos[1], pos[2]);

            random.setSeed(42L);
            List<BlockStateModelPart> parts = new ArrayList<>();
            model.collectParts(blockGetter, blockPos, blockState, random, parts);

            for (BlockStateModelPart part : parts) {
                renderPartWithFaceCulling(poseStack, blockState, part, blockGetter, blockPos, quadInstance);
            }

            poseStack.popPose();
        }
    }

    private void renderPartWithFaceCulling(
            PoseStack poseStack,
            BlockState state,
            BlockStateModelPart part,
            PreviewBlockGetter blockGetter,
            BlockPos pos,
            QuadInstance quadInstance
    ) {
        for (Direction direction : Direction.values()) {
            BlockPos adjacentPos = pos.relative(direction);
            BlockState adjacentState = blockGetter.getBlockState(adjacentPos);

            if (state.skipRendering(adjacentState, direction)) {
                continue;
            }

            for (BakedQuad quad : part.getQuads(direction)) {
                quadInstance.setColor(-1);
                quadInstance.setLightCoords(15728880);
                VertexConsumer buffer = this.bufferSource.getBuffer(getRenderTypeForLayer(quad.materialInfo().layer()));
                buffer.putBakedQuad(poseStack.last(), quad, quadInstance);
            }
        }

        for (BakedQuad quad : part.getQuads(null)) {
            quadInstance.setColor(-1);
            quadInstance.setLightCoords(15728880);
            VertexConsumer buffer = this.bufferSource.getBuffer(getRenderTypeForLayer(quad.materialInfo().layer()));
            buffer.putBakedQuad(poseStack.last(), quad, quadInstance);
        }
    }

    private static RenderType getRenderTypeForLayer(ChunkSectionLayer layer) {
        return switch (layer) {
            case SOLID -> RenderTypes.solidMovingBlock();
            case CUTOUT -> RenderTypes.cutoutMovingBlock();
            case TRANSLUCENT -> RenderTypes.translucentMovingBlock();
        };
    }

    @Override
    protected String getTextureLabel() {
        return "chisel block preview";
    }
}
