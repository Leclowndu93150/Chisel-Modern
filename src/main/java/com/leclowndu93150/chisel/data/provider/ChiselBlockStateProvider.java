package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.api.block.ICarvable;
import com.leclowndu93150.chisel.api.block.VariationData;
import com.leclowndu93150.chisel.data.ChiselModelTemplates;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

/**
 * Data provider for generating blockstate and block model JSON files.
 * Uses the ModelTemplate system ported from Chisel 1.18.2.
 */
public class ChiselBlockStateProvider extends BlockStateProvider {

    public ChiselBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Chisel.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            ChiselModelTemplates.ModelTemplate defaultTemplate = blockType.getDefaultModelTemplate();
            for (DeferredBlock<?> deferredBlock : blockType.getAllBlocks()) {
                Block block = deferredBlock.get();
                if (block instanceof ICarvable carvable) {
                    generateBlockStateAndModel(block, carvable.getVariation(), defaultTemplate);
                }
            }
        }
    }

    private void generateBlockStateAndModel(Block block, VariationData variation, ChiselModelTemplates.ModelTemplate defaultTemplate) {
        ChiselModelTemplates.ModelTemplate template = variation.modelTemplate();

        if (template != null) {
            template.apply(this, block);
        } else if (defaultTemplate != null) {
            defaultTemplate.apply(this, block);
        } else {
            ChiselModelTemplates.simpleBlock().apply(this, block);
        }
    }
}
