package com.leclowndu93150.chisel.client;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.block.BlockBrownstone;
import com.leclowndu93150.chisel.block.BlockHolystone;
import com.leclowndu93150.chisel.client.util.CTMDetection;
import com.leclowndu93150.chisel.item.ItemChisel;
import com.leclowndu93150.chisel.network.server.ChiselFuzzyPayload;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = Chisel.MODID, value = Dist.CLIENT)
public class ChiselClientGameEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (CTMDetection.hasCTM(event.getItemStack().getItem())) {
            event.getToolTip().add(Component.translatable("chisel.tooltip.ctm")
                    .withStyle(ChatFormatting.GRAY));
        }

        if (event.getItemStack().getItem() instanceof BlockItem blockItem) {
            if (blockItem.getBlock() instanceof BlockBrownstone) {
                event.getToolTip().add(Component.translatable("chisel.tooltip.brownstone.speed")
                        .withStyle(ChatFormatting.GRAY));
            } else if (blockItem.getBlock() instanceof BlockHolystone) {
                event.getToolTip().add(Component.translatable("chisel.tooltip.holystone.glow")
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @SubscribeEvent
    public static void onMouseClick(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null || mc.screen != null) {
            return;
        }

        if (event.isUseItem() && mc.hasControlDown() && mc.hasShiftDown()) {
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();

            if (mainHand.getItem() instanceof IChiselItem chiselItem) {
                if (mainHand.getItem() instanceof ItemChisel itemChisel && itemChisel.getChiselType().hasModes()) {
                    int slot = player.getInventory().getSelectedSlot();
                    ClientPacketDistributor.sendToServer(new ChiselFuzzyPayload(slot));
                    chiselItem.toggleFuzzyMode(mainHand);
                    event.setCanceled(true);
                    event.setSwingHand(false);
                    return;
                }
            }

            if (offHand.getItem() instanceof IChiselItem chiselItem) {
                if (offHand.getItem() instanceof ItemChisel itemChisel && itemChisel.getChiselType().hasModes()) {
                    int slot = 40;
                    ClientPacketDistributor.sendToServer(new ChiselFuzzyPayload(slot));
                    chiselItem.toggleFuzzyMode(offHand);
                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            }
        }
    }
}
