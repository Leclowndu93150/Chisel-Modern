package com.leclowndu93150.chisel.client;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Map;

/**
 * Client-side event handlers for color tinting and other visual effects.
 */
@EventBusSubscriber(modid = Chisel.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChiselClientEvents {

    // Default water color when not in world
    private static final int DEFAULT_WATER_COLOR = 0x3F76E4;

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        // Register hex plating color handlers - tint index 1 gets dye color
        for (Map.Entry<DyeColor, ChiselBlockType<com.leclowndu93150.chisel.block.BlockCarvable>> entry : ChiselBlocks.HEX_PLATING.entrySet()) {
            DyeColor color = entry.getKey();
            ChiselBlockType<?> blockType = entry.getValue();

            Block[] blocks = blockType.getAllBlocks().stream()
                    .map(DeferredBlock::get)
                    .toArray(Block[]::new);

            event.register((state, level, pos, tintIndex) ->
                    tintIndex == 1 ? color.getTextColor() : -1, blocks);
        }

        // Register waterstone color handlers - tint index 1 gets biome water color
        Block[] waterstoneBlocks = ChiselBlocks.WATERSTONE.getAllBlocks().stream()
                .map(DeferredBlock::get)
                .toArray(Block[]::new);

        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex == 1) {
                return (level != null && pos != null) ? BiomeColors.getAverageWaterColor(level, pos) : DEFAULT_WATER_COLOR;
            }
            return -1;
        }, waterstoneBlocks);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        // Register hex plating item color handlers - tint index 1 gets dye color
        for (Map.Entry<DyeColor, ChiselBlockType<com.leclowndu93150.chisel.block.BlockCarvable>> entry : ChiselBlocks.HEX_PLATING.entrySet()) {
            DyeColor color = entry.getKey();
            ChiselBlockType<?> blockType = entry.getValue();

            var items = blockType.getAllBlocks().stream()
                    .map(block -> block.get().asItem())
                    .toArray(net.minecraft.world.item.Item[]::new);

            event.register((stack, tintIndex) ->
                    tintIndex == 1 ? color.getTextColor() : -1, items);
        }

        // Register waterstone item color handlers - tint index 1 gets default water color
        var waterstoneItems = ChiselBlocks.WATERSTONE.getAllBlocks().stream()
                .map(block -> block.get().asItem())
                .toArray(net.minecraft.world.item.Item[]::new);

        event.register((stack, tintIndex) ->
                tintIndex == 1 ? DEFAULT_WATER_COLOR : -1, waterstoneItems);
    }
}
