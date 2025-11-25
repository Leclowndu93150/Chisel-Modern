package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.concurrent.CompletableFuture;

/**
 * Data provider for generating item tag JSON files.
 */
public class ChiselItemTagsProvider extends ItemTagsProvider {

    public ChiselItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                  CompletableFuture<TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, Chisel.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // Generate carving group item tags for each block type
        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            // Create item tag for carving group
            TagKey<Item> carvingItemTag = ItemTags.create(Chisel.id("carving/" + blockType.getName().replace("/", "_")));

            IntrinsicTagAppender<Item> tagBuilder = tag(carvingItemTag);

            // Add all chisel variant items to the carving group
            for (DeferredItem<BlockItem> item : blockType.getAllItems()) {
                tagBuilder.add(item.get());
            }

            // Add vanilla block items to the carving group
            for (ResourceLocation vanillaId : blockType.getVanillaBlocks()) {
                tagBuilder.addOptional(vanillaId);
            }

            // Add configured item tags to all variants
            for (TagKey<Item> itemTag : blockType.getItemTags()) {
                IntrinsicTagAppender<Item> itemTagBuilder = tag(itemTag);
                for (DeferredItem<BlockItem> item : blockType.getAllItems()) {
                    itemTagBuilder.add(item.get());
                }
            }
        }
    }
}
