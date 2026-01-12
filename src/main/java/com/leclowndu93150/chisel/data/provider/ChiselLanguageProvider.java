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

public class ChiselLanguageProvider extends LanguageProvider {

    public ChiselLanguageProvider(PackOutput output) {
        super(output, Chisel.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add("itemGroup.chisel", "Chisel");

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

        add(ChiselItems.IRON_CHISEL.get(), "Chisel");
        add(ChiselItems.DIAMOND_CHISEL.get(), "Diamond Chisel");
        add(ChiselItems.HITECH_CHISEL.get(), "iChisel");
        add(ChiselItems.OFFSET_TOOL.get(), "Ender Offset Wand");
        add(ChiselItems.BALL_O_MOSS.get(), "Ball O' Moss");
        add(ChiselItems.CLOUD_IN_A_BOTTLE.get(), "Cloud in a Bottle");

        add("jei.chisel.chiseling", "Chiseling");
        add("jei.chisel.no_craftable", "No Craftable Block");

        add("chisel.tooltip.gui", "Right-click to open GUI");
        add("chisel.tooltip.leftclick.1", "Left-click blocks to cycle variants forward");
        add("chisel.tooltip.leftclick.2", "Shift + Left-click to cycle backwards");
        add("chisel.tooltip.target", "Set a target in the GUI to chisel to a specific variant");
        add("chisel.tooltip.modes", "Shift + Right-click to change mode");
        add("chisel.tooltip.selectedmode", "Selected mode: %s");
        add("chisel.tooltip.fuzzy", "Fuzzy mode: %s");
        add("chisel.tooltip.fuzzy.enabled", "Enabled");
        add("chisel.tooltip.fuzzy.disabled", "Disabled");
        add("chisel.tooltip.fuzzy.hint", "Ctrl + Shift + Right-click to toggle fuzzy mode");

        add("chisel.tooltip.offset_tool.1", "Right-click a block to cycle texture offset");
        add("chisel.tooltip.offset_tool.2", "Sneak + Right-click to reset offset");

        add("container.chisel", "Chisel");
        add("container.chisel.hitech", "iChisel");

        add("chisel.button.preview", "Preview");
        add("chisel.button.chisel", "Chisel");
        add("chisel.button.chisel_all", "Chisel All");

        add("chisel.preview", "Preview");
        add("chisel.preview.panel", "Panel");
        add("chisel.preview.hollow", "Hollow");
        add("chisel.preview.plus", "Plus");
        add("chisel.preview.single", "Single");

        add("chisel.message.mode_changed", "Mode changed to: %s");
        add("chisel.message.cooldown", "Chisel on cooldown: %ss");
        add("chisel.message.fuzzy_enabled", "Fuzzy mode enabled - chisels all variants in the carving group");
        add("chisel.message.fuzzy_disabled", "Fuzzy mode disabled - chisels exact block matches only");

        add("chisel.mode.single", "Single");
        add("chisel.mode.single.desc", "Chisel a single block.");
        add("chisel.mode.panel", "Panel");
        add("chisel.mode.panel.desc", "Chisel a 3x3 square of blocks.");
        add("chisel.mode.column", "Column");
        add("chisel.mode.column.desc", "Chisel a 3x1 column of blocks.");
        add("chisel.mode.row", "Row");
        add("chisel.mode.row.desc", "Chisel a 1x3 row of blocks.");
        add("chisel.mode.contiguous", "Contiguous");
        add("chisel.mode.contiguous.desc", "Chisel an area of alike blocks, extending 10 blocks in any direction.");
        add("chisel.mode.contiguous_2d", "Contiguous (2D)");
        add("chisel.mode.contiguous_2d.desc", "Chisel an area of alike blocks, extending 10 blocks along the plane of the current side.");

        add(ChiselBlocks.AUTO_CHISEL.get(), "Auto Chisel");

        add("chisel.tooltip.power.stored", "%s/%s FE");
        add("chisel.tooltip.power.pertick", "%s FE/t");

        add("chisel.tooltip.brownstone.speed", "Walking on this block increases your speed");
        add("chisel.tooltip.holystone.glow", "Emits a holy glow and sparkles");
        add("chisel.tooltip.ctm", "Has CTM");
    }
}
