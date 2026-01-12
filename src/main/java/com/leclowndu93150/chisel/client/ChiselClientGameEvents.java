package com.leclowndu93150.chisel.client;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.client.util.CTMDetection;
import com.leclowndu93150.chisel.network.ChiselNetwork;
import com.leclowndu93150.chisel.network.server.ChiselFuzzyPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = Chisel.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ChiselClientGameEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (CTMDetection.hasCTM(event.getItemStack().getItem())) {
            event.getToolTip().add(Component.translatable("chisel.tooltip.ctm")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @SubscribeEvent
    public static void onMouseInput(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) {
            return;
        }

        // Check for right-click (button 1) press
        if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT && event.getAction() == GLFW.GLFW_PRESS) {
            long window = mc.getWindow().getWindow();
            boolean ctrlHeld = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
            boolean shiftHeld = GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                    || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;

            if (ctrlHeld && shiftHeld) {
                Player player = mc.player;
                ItemStack mainHand = player.getMainHandItem();
                ItemStack offHand = player.getOffhandItem();

                int slot = -1;
                if (mainHand.getItem() instanceof IChiselItem chiselItem && chiselItem.getMode(mainHand) != null) {
                    slot = player.getInventory().selected;
                } else if (offHand.getItem() instanceof IChiselItem chiselItem && chiselItem.getMode(offHand) != null) {
                    slot = 40; // Offhand slot
                }

                if (slot >= 0) {
                    ChiselNetwork.sendToServer(new ChiselFuzzyPacket(slot));
                    event.setCanceled(true);
                }
            }
        }
    }
}
