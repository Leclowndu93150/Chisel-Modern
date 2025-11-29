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
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.awt.geom.Line2D;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static net.minecraft.core.Direction.*;

@Mod.EventBusSubscriber(modid = Chisel.MODID, value = Dist.CLIENT)
public class OffsetToolRenderer {

    @SubscribeEvent
    public static void onBlockHighlight(RenderHighlightEvent.Block event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        boolean holdingOffsetTool = (!mainHand.isEmpty() && mainHand.getItem() instanceof ItemOffsetTool)
                || (!offHand.isEmpty() && offHand.getItem() instanceof ItemOffsetTool);

        if (!holdingOffsetTool) return;

        BlockHitResult target = event.getTarget();
        Direction face = target.getDirection();
        BlockPos pos = target.getBlockPos();
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = event.getMultiBufferSource();

        poseStack.pushPose();

        float x = Math.max(0, face.getStepX());
        float y = Math.max(0, face.getStepY());
        float z = Math.max(0, face.getStepZ());

        Vec3 viewport = mc.gameRenderer.getMainCamera().getPosition();

        poseStack.translate(-viewport.x, -viewport.y, -viewport.z);
        poseStack.translate(pos.getX(), pos.getY(), pos.getZ());
        Matrix4f mat = poseStack.last().pose();

        // Draw the X lines
        VertexConsumer linesBuf = bufferSource.getBuffer(RenderType.lines());

        if (face.getStepX() != 0) {
            linesBuf.vertex(mat, x, 0, 0).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
            linesBuf.vertex(mat, x, 1, 1).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
            linesBuf.vertex(mat, x, 1, 0).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
            linesBuf.vertex(mat, x, 0, 1).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
        } else if (face.getStepY() != 0) {
            linesBuf.vertex(mat, 0, y, 0).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
            linesBuf.vertex(mat, 1, y, 1).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
            linesBuf.vertex(mat, 1, y, 0).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
            linesBuf.vertex(mat, 0, y, 1).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
        } else {
            linesBuf.vertex(mat, 0, 0, z).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
            linesBuf.vertex(mat, 1, 1, z).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
            linesBuf.vertex(mat, 1, 0, z).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
            linesBuf.vertex(mat, 0, 1, z).color(0, 0, 0, 255).normal(0, 1, 0).endVertex();
        }

        // Draw the triangle highlight showing which direction will be moved
        Vec3 hit = target.getLocation();
        Vec3 localHit = hit.subtract(pos.getX(), pos.getY(), pos.getZ());
        Direction moveDir = getMoveDir(face, localHit);

        VertexConsumer triBuf = bufferSource.getBuffer(ClientUtil.OFFSET_OVERLAY);

        int clampedX = Math.max(0, moveDir.getStepX());
        int clampedY = Math.max(0, moveDir.getStepY());
        int clampedZ = Math.max(0, moveDir.getStepZ());
        boolean isX = moveDir.getStepX() != 0;
        boolean isY = moveDir.getStepY() != 0;
        boolean isZ = moveDir.getStepZ() != 0;
        int alpha = 0x55;

        // Small offset to prevent z-fighting
        float offset = 0.001f;

        // Always draw the center point first, then draw the next two points.
        // Use either the move dir offset, or 0/1 if the move dir is not offset in this direction
        if (face.getStepX() != 0) {
            float xOff = x + (face.getStepX() > 0 ? offset : -offset);
            triBuf.vertex(mat, xOff, 0.5f, 0.5f).color(255, 255, 255, alpha).endVertex();
            triBuf.vertex(mat, xOff, isY ? clampedY : 0, isZ ? clampedZ : 0).color(255, 255, 255, alpha).endVertex();
            triBuf.vertex(mat, xOff, isY ? clampedY : 1, isZ ? clampedZ : 1).color(255, 255, 255, alpha).endVertex();
        } else if (face.getStepY() != 0) {
            float yOff = y + (face.getStepY() > 0 ? offset : -offset);
            triBuf.vertex(mat, 0.5f, yOff, 0.5f).color(255, 255, 255, alpha).endVertex();
            triBuf.vertex(mat, isX ? clampedX : 0, yOff, isZ ? clampedZ : 0).color(255, 255, 255, alpha).endVertex();
            triBuf.vertex(mat, isX ? clampedX : 1, yOff, isZ ? clampedZ : 1).color(255, 255, 255, alpha).endVertex();
        } else {
            float zOff = z + (face.getStepZ() > 0 ? offset : -offset);
            triBuf.vertex(mat, 0.5f, 0.5f, zOff).color(255, 255, 255, alpha).endVertex();
            triBuf.vertex(mat, isX ? clampedX : 0, isY ? clampedY : 0, zOff).color(255, 255, 255, alpha).endVertex();
            triBuf.vertex(mat, isX ? clampedX : 1, isY ? clampedY : 1, zOff).color(255, 255, 255, alpha).endVertex();
        }

        poseStack.popPose();
    }

    private static Direction getMoveDir(Direction face, Vec3 hitVec) {
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
