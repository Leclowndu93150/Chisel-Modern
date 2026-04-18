package com.leclowndu93150.chisel.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.client.util.ClientUtil;
import com.leclowndu93150.chisel.item.ItemOffsetTool;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.BlockOutlineRenderState;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.CustomBlockOutlineRenderer;
import net.neoforged.neoforge.client.event.ExtractBlockOutlineRenderStateEvent;
import org.joml.Matrix4f;

import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static net.minecraft.core.Direction.*;

@EventBusSubscriber(modid = Chisel.MODID, value = Dist.CLIENT)
public class OffsetToolRenderer {

    @SubscribeEvent
    public static void onExtractBlockOutline(ExtractBlockOutlineRenderStateEvent event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        boolean holdingOffsetTool = (!mainHand.isEmpty() && mainHand.getItem() instanceof ItemOffsetTool)
                || (!offHand.isEmpty() && offHand.getItem() instanceof ItemOffsetTool);

        if (!holdingOffsetTool) return;

        HitResult hitResult = mc.hitResult;
        if (!(hitResult instanceof BlockHitResult blockHit)) return;

        Direction face = blockHit.getDirection();
        BlockPos pos = event.getBlockPos();

        event.addCustomRenderer(new OffsetToolOutlineRenderer(pos, face, blockHit));
    }

    private record OffsetToolOutlineRenderer(BlockPos pos, Direction face, BlockHitResult target) implements CustomBlockOutlineRenderer {
        @Override
        public boolean render(BlockOutlineRenderState renderState, MultiBufferSource.BufferSource bufferSource, PoseStack poseStack, boolean translucentPass, LevelRenderState levelRenderState) {
            if (translucentPass) return false;

            Minecraft mc = Minecraft.getInstance();

            poseStack.pushPose();

            float x = Math.max(0, face.getStepX());
            float y = Math.max(0, face.getStepY());
            float z = Math.max(0, face.getStepZ());

            var viewport = mc.gameRenderer.getMainCamera().position();

            poseStack.translate(-viewport.x, -viewport.y, -viewport.z);
            poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
            Matrix4f mat = poseStack.last().pose();

            VertexConsumer linesBuf = bufferSource.getBuffer(RenderTypes.lines());

            if (face.getStepX() != 0) {
                linesBuf.addVertex(mat, x, 0, 0).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
                linesBuf.addVertex(mat, x, 1, 1).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
                linesBuf.addVertex(mat, x, 1, 0).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
                linesBuf.addVertex(mat, x, 0, 1).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
            } else if (face.getStepY() != 0) {
                linesBuf.addVertex(mat, 0, y, 0).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
                linesBuf.addVertex(mat, 1, y, 1).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
                linesBuf.addVertex(mat, 1, y, 0).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
                linesBuf.addVertex(mat, 0, y, 1).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
            } else {
                linesBuf.addVertex(mat, 0, 0, z).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
                linesBuf.addVertex(mat, 1, 1, z).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
                linesBuf.addVertex(mat, 1, 0, z).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
                linesBuf.addVertex(mat, 0, 1, z).setColor(0, 0, 0, 255).setNormal(0, 1, 0).setLineWidth(1.0f);
            }

            var hit = target.getLocation();
            var localHit = hit.subtract(pos.getX(), pos.getY(), pos.getZ());
            Direction moveDir = getMoveDir(face, localHit);

            VertexConsumer triBuf = bufferSource.getBuffer(ClientUtil.OFFSET_OVERLAY);

            int clampedX = Math.max(0, moveDir.getStepX());
            int clampedY = Math.max(0, moveDir.getStepY());
            int clampedZ = Math.max(0, moveDir.getStepZ());
            boolean isX = moveDir.getStepX() != 0;
            boolean isY = moveDir.getStepY() != 0;
            boolean isZ = moveDir.getStepZ() != 0;
            int alpha = 0x55;

            float offset = 0.001f;

            if (face.getStepX() != 0) {
                float xOff = x + (face.getStepX() > 0 ? offset : -offset);
                triBuf.addVertex(mat, xOff, 0.5f, 0.5f).setColor(255, 255, 255, alpha);
                triBuf.addVertex(mat, xOff, isY ? clampedY : 0, isZ ? clampedZ : 0).setColor(255, 255, 255, alpha);
                triBuf.addVertex(mat, xOff, isY ? clampedY : 1, isZ ? clampedZ : 1).setColor(255, 255, 255, alpha);
            } else if (face.getStepY() != 0) {
                float yOff = y + (face.getStepY() > 0 ? offset : -offset);
                triBuf.addVertex(mat, 0.5f, yOff, 0.5f).setColor(255, 255, 255, alpha);
                triBuf.addVertex(mat, isX ? clampedX : 0, yOff, isZ ? clampedZ : 0).setColor(255, 255, 255, alpha);
                triBuf.addVertex(mat, isX ? clampedX : 1, yOff, isZ ? clampedZ : 1).setColor(255, 255, 255, alpha);
            } else {
                float zOff = z + (face.getStepZ() > 0 ? offset : -offset);
                triBuf.addVertex(mat, 0.5f, 0.5f, zOff).setColor(255, 255, 255, alpha);
                triBuf.addVertex(mat, isX ? clampedX : 0, isY ? clampedY : 0, zOff).setColor(255, 255, 255, alpha);
                triBuf.addVertex(mat, isX ? clampedX : 1, isY ? clampedY : 1, zOff).setColor(255, 255, 255, alpha);
            }

            poseStack.popPose();
            return false;
        }
    }

    private static Direction getMoveDir(Direction face, net.minecraft.world.phys.Vec3 hitVec) {
        Map<Double, Direction> map = Maps.newHashMap();
        if (face.getStepX() != 0) {
            fillMap(map, hitVec.z - (int) hitVec.z, hitVec.y - (int) hitVec.y, DOWN, UP, NORTH, SOUTH);
        } else if (face.getStepY() != 0) {
            fillMap(map, hitVec.x - (int) hitVec.x, hitVec.z - (int) hitVec.z, NORTH, SOUTH, WEST, EAST);
        } else if (face.getStepZ() != 0) {
            fillMap(map, hitVec.x - (int) hitVec.x, hitVec.y - (int) hitVec.y, DOWN, UP, WEST, EAST);
        }
        List<Double> keys = Lists.newArrayList(map.keySet());
        Collections.sort(keys);
        return map.isEmpty() ? DOWN : map.get(keys.get(0));
    }

    private static void fillMap(Map<Double, Direction> map, double x, double y, Direction... dirs) {
        map.put(Line2D.ptLineDistSq(0, 0, 1, 0, x, y), dirs[0]);
        map.put(Line2D.ptLineDistSq(0, 1, 1, 1, x, y), dirs[1]);
        map.put(Line2D.ptLineDistSq(0, 0, 0, 1, x, y), dirs[2]);
        map.put(Line2D.ptLineDistSq(1, 0, 1, 1, x, y), dirs[3]);
    }
}
