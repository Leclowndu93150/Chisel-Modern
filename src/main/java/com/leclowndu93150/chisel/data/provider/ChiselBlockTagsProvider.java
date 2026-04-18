package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.concurrent.CompletableFuture;

/**
 * Data provider for generating block tag JSON files.
 */
public class ChiselBlockTagsProvider extends BlockTagsProvider {

    public ChiselBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, Chisel.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            TagKey<Block> carvingTag = blockType.getCarvingGroupTag();

            TagAppender<Block, Block> tagBuilder = tag(carvingTag);

            for (DeferredBlock<?> block : blockType.getAllBlocks()) {
                tagBuilder.add(block.get());
            }

            for (Identifier vanillaId : blockType.getVanillaBlocks()) {
                getOrCreateRawBuilder(carvingTag).addOptionalElement(vanillaId);
            }

            // Add blocks from source tags (e.g., c:storage_blocks/iron)
            // This allows modded blocks to automatically be included in carving groups
            for (TagKey<Block> sourceTag : blockType.getSourceBlockTags()) {
                tagBuilder.addOptionalTag(sourceTag);
            }

            for (TagKey<Block> blockTag : blockType.getBlockTags()) {
                TagAppender<Block, Block> miningTagBuilder = tag(blockTag);
                for (DeferredBlock<?> block : blockType.getAllBlocks()) {
                    miningTagBuilder.add(block.get());
                }
            }
        }
    }
}
