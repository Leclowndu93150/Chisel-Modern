package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.api.block.ICarvable;
import com.leclowndu93150.chisel.api.block.VariationData;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselItems;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Data provider for generating language files (en_us.json).
 */
public class ChiselLanguageProvider extends LanguageProvider {

    public ChiselLanguageProvider(PackOutput output) {
        super(output, Chisel.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // Creative tab
        add("itemGroup.chisel", "Chisel");

        // Generate translations for all blocks
        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            String groupName = blockType.getGroupName();

            for (DeferredBlock<?> deferredBlock : blockType.getAllBlocks()) {
                Block block = deferredBlock.get();
                if (block instanceof ICarvable carvable) {
                    VariationData variation = carvable.getVariation();
                    String displayName = groupName + " - " + variation.localizedName();
                    add(block, displayName);
                }
            }
        }

        // Chisel tools
        add(ChiselItems.IRON_CHISEL.get(), "Iron Chisel");
        add(ChiselItems.DIAMOND_CHISEL.get(), "Diamond Chisel");
        add(ChiselItems.HITECH_CHISEL.get(), "iChisel");
        add(ChiselItems.OFFSET_TOOL.get(), "Offset Tool");

        // GUI and misc translations
        add("chisel.gui.title", "Chisel");
        add("chisel.gui.hitech.title", "iChisel");
        add("chisel.gui.autochisel.title", "Auto Chisel");

        // Chisel modes
        add("chisel.mode.single", "Single");
        add("chisel.mode.panel", "Panel (3x3)");
        add("chisel.mode.column", "Column");
        add("chisel.mode.row", "Row");
        add("chisel.mode.contiguous", "Contiguous");
        add("chisel.mode.contiguous_2d", "Contiguous 2D");

        // Tooltips
        add("chisel.tooltip.chisel", "Right-click to open GUI");
        add("chisel.tooltip.chisel.leftclick", "Left-click blocks to chisel in world");
        add("chisel.tooltip.offset", "Used to offset connected textures");
    }
}
