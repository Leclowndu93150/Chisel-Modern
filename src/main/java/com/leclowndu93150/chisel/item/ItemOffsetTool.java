package com.leclowndu93150.chisel.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * The Offset Tool is used to adjust the CTM offset of blocks.
 * This is useful for making patterns line up across multiple blocks.
 */
public class ItemOffsetTool extends Item {

    public ItemOffsetTool(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("chisel.tooltip.offset_tool.1")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("chisel.tooltip.offset_tool.2")
                .withStyle(ChatFormatting.GRAY));
    }
}
