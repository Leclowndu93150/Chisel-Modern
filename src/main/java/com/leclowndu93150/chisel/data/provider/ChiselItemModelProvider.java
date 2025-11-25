package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * Data provider for generating item model JSON files.
 */
public class ChiselItemModelProvider extends ItemModelProvider {

    public ChiselItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Chisel.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // Generate item models for all block items (inherit from block model)
        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            for (DeferredItem<BlockItem> item : blockType.getAllItems()) {
                String name = item.getId().getPath();
                withExistingParent(name, Chisel.id("block/" + name));
            }
        }

        // Generate item models for chisel tools
        basicItem(ChiselItems.IRON_CHISEL.get());
        basicItem(ChiselItems.DIAMOND_CHISEL.get());
        basicItem(ChiselItems.HITECH_CHISEL.get());
        basicItem(ChiselItems.OFFSET_TOOL.get());
    }
}
