package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;

public class ChiselLootTableProvider extends LootTableProvider {

    public ChiselLootTableProvider(PackOutput output) {
        super(output, Set.of(), List.of(
                new SubProviderEntry(ChiselBlockLoot::new, LootContextParamSets.BLOCK)
        ));
    }

    private static class ChiselBlockLoot extends BlockLootSubProvider {

        protected ChiselBlockLoot() {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
                for (RegistryObject<?> registryObject : blockType.getAllBlocks()) {
                    Block block = (Block) registryObject.get();
                    dropSelf(block);
                }
            }
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return ChiselBlocks.getAllBlocks().stream()
                    .map(RegistryObject::get)
                    .map(block -> (Block) block)
                    .toList();
        }
    }
}
