package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.Chisel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registration class for Chisel creative mode tabs.
 */
public class ChiselCreativeTabs {

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> CHISEL_TAB =
            ChiselRegistries.CREATIVE_TABS.register("chisel_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.chisel"))
                    .withTabsBefore(CreativeModeTabs.BUILDING_BLOCKS)
                    .icon(() -> ChiselItems.IRON_CHISEL.get().getDefaultInstance())
                    .displayItems((parameters, output) -> {
                        // Add chisel tools first
                        output.accept(ChiselItems.IRON_CHISEL.get());
                        output.accept(ChiselItems.DIAMOND_CHISEL.get());
                        output.accept(ChiselItems.HITECH_CHISEL.get());
                        output.accept(ChiselItems.OFFSET_TOOL.get());

                        // Add all block items
                        ChiselBlocks.ALL_BLOCK_TYPES.forEach(blockType -> {
                            blockType.getAllItems().forEach(item -> output.accept(item.get()));
                        });
                    })
                    .build());

    /**
     * Called during mod initialization to trigger static initialization.
     */
    public static void init() {
        // Static initialization is triggered by loading this class
    }
}
