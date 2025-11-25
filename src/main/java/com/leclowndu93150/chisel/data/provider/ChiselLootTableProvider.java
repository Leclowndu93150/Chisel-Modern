package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Data provider for generating loot table JSON files.
 */
public class ChiselLootTableProvider extends LootTableProvider {

    public ChiselLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(ChiselBlockLoot::new, LootContextParamSets.BLOCK)
        ), registries);
    }

    private static class ChiselBlockLoot extends BlockLootSubProvider {

        protected ChiselBlockLoot(HolderLookup.Provider provider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), provider);
        }

        @Override
        protected void generate() {
            // Generate drop self loot tables for all chisel blocks
            for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
                for (DeferredBlock<?> deferredBlock : blockType.getAllBlocks()) {
                    Block block = deferredBlock.get();
                    // Default behavior: drop the block itself
                    dropSelf(block);
                }
            }

            // Special loot tables can be added here
            // For example, glowstone drops with fortune/silk touch, ice drops nothing without silk touch, etc.
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ChiselBlocks.getAllBlocks().stream()
                    .map(DeferredBlock::get)
                    .map(block -> (Block) block)
                    .toList();
        }
    }
}
