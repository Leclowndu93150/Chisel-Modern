package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ItemTagsProvider;
import net.neoforged.neoforge.registries.DeferredItem;

import java.util.concurrent.CompletableFuture;

public class ChiselItemTagsProvider extends ItemTagsProvider {

    public ChiselItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Chisel.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            TagKey<Item> carvingItemTag = ItemTags.create(Chisel.id("carving/" + blockType.getName().replace("/", "_")));

            TagAppender<Item, Item> tagBuilder = tag(carvingItemTag);

            for (DeferredItem<BlockItem> item : blockType.getAllItems()) {
                tagBuilder.add(item.get());
            }

            for (Identifier vanillaId : blockType.getVanillaBlocks()) {
                getOrCreateRawBuilder(carvingItemTag).addOptionalElement(vanillaId);
            }

            for (TagKey<Block> sourceBlockTag : blockType.getSourceBlockTags()) {
                TagKey<Item> sourceItemTag = TagKey.create(Registries.ITEM, sourceBlockTag.location());
                tagBuilder.addOptionalTag(sourceItemTag);
            }

            for (TagKey<Item> itemTag : blockType.getItemTags()) {
                TagAppender<Item, Item> itemTagBuilder = tag(itemTag);
                for (DeferredItem<BlockItem> item : blockType.getAllItems()) {
                    itemTagBuilder.add(item.get());
                }
            }
        }
    }
}
