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
        // Generate blockstates and models for all registered block types
        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            for (DeferredBlock<?> deferredBlock : blockType.getAllBlocks()) {
                Block block = deferredBlock.get();
                if (block instanceof ICarvable carvable) {
                    generateBlockStateAndModel(block, carvable.getVariation());
                }
            }
        }
    }

    private void generateBlockStateAndModel(Block block, VariationData variation) {
        ChiselModelTemplates.ModelTemplate template = variation.modelTemplate();

        if (template != null) {
            // Use the stored model template function
            template.apply(this, block);
        } else {
            // Default to simple cube_all if no template specified
            ChiselModelTemplates.simpleBlock().apply(this, block);
        }
    }
}
