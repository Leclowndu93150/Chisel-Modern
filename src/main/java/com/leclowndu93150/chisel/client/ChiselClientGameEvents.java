package com.leclowndu93150.chisel.client;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.client.util.CTMDetection;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Chisel.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ChiselClientGameEvents {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        if (CTMDetection.hasCTM(event.getItemStack().getItem())) {
            event.getToolTip().add(Component.translatable("chisel.tooltip.ctm")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
