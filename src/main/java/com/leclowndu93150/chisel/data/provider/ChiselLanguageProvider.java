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
        add(ChiselItems.IRON_CHISEL.get(), "Chisel");
        add(ChiselItems.DIAMOND_CHISEL.get(), "Diamond Chisel");
        add(ChiselItems.HITECH_CHISEL.get(), "iChisel");
        add(ChiselItems.OFFSET_TOOL.get(), "Ender Offset Wand");

        // Chisel item tooltips (used in ItemChisel.java)
        add("chisel.tooltip.gui", "Right-click to open GUI");
        add("chisel.tooltip.leftclick.1", "Left-click to chisel blocks in the world");
        add("chisel.tooltip.leftclick.2", "Target a block by leaving it in the inventory");
        add("chisel.tooltip.modes", "Has multiple chiseling modes");
        add("chisel.tooltip.selectedmode", "Selected mode: %s");

        // Offset tool tooltips (used in ItemOffsetTool.java)
        add("chisel.tooltip.offset_tool.1", "Right-click a block to cycle texture offset");
        add("chisel.tooltip.offset_tool.2", "Sneak + Right-click to reset offset");

        // Container/GUI titles (used in ItemChisel.java)
        add("container.chisel", "Chisel");
        add("container.chisel.hitech", "iChisel");

        // Hitech GUI buttons (used in HitechChiselScreen.java)
        add("chisel.button.preview", "Preview");
        add("chisel.button.chisel", "Chisel");

        // Mode change message (used in ItemChisel.java)
        add("chisel.message.mode_changed", "Mode changed to: %s");

        // Chisel modes (used via IChiselMode.getUnlocName() -> "chisel.mode.<name>")
        add("chisel.mode.single", "Single");
        add("chisel.mode.panel", "Panel");
        add("chisel.mode.column", "Column");
        add("chisel.mode.row", "Row");
        add("chisel.mode.contiguous", "Contiguous");
        add("chisel.mode.contiguous_2d", "Contiguous (2D)");

        // Auto Chisel power tooltips (used in AutoChiselScreen.java)
        add("chisel.tooltip.power.stored", "%s/%s FE");
        add("chisel.tooltip.power.pertick", "%s FE/t");
    }
}
