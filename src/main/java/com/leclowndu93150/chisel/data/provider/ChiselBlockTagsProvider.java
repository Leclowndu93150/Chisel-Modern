package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.concurrent.CompletableFuture;

/**
 * Data provider for generating block tag JSON files.
 */
public class ChiselBlockTagsProvider extends BlockTagsProvider {

    public ChiselBlockTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Chisel.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            TagKey<Block> carvingTag = blockType.getCarvingGroupTag();

            IntrinsicTagAppender<Block> tagBuilder = tag(carvingTag);

            for (RegistryObject<?> registryObject : blockType.getAllBlocks()) {
                tagBuilder.add((Block) registryObject.get());
            }

            for (ResourceLocation vanillaId : blockType.getVanillaBlocks()) {
                tagBuilder.addOptional(vanillaId);
            }

            // Add blocks from source tags (e.g., c:storage_blocks/iron)
            // This allows modded blocks to automatically be included in carving groups
            for (TagKey<Block> sourceTag : blockType.getSourceBlockTags()) {
                tagBuilder.addOptionalTag(sourceTag);
            }

            for (TagKey<Block> blockTag : blockType.getBlockTags()) {
                IntrinsicTagAppender<Block> miningTagBuilder = tag(blockTag);
                for (RegistryObject<?> registryObject : blockType.getAllBlocks()) {
                    miningTagBuilder.add((Block) registryObject.get());
                }
            }
        }
    }
}
