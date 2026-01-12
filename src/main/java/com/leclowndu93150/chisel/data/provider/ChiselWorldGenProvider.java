package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.worldgen.ConfigurableOreBiomeModifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ChiselWorldGenProvider extends DatapackBuiltinEntriesProvider {

    // Configured Features (what to generate - ore type and base vein size)
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_MARBLE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Chisel.id("ore_marble"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_LIMESTONE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Chisel.id("ore_limestone"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIABASE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Chisel.id("ore_diabase"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_BASALT = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Chisel.id("ore_basalt"));

    // Biome Modifiers (configurable at runtime)
    public static final ResourceKey<BiomeModifier> ADD_MARBLE = ResourceKey.create(
            ForgeRegistries.Keys.BIOME_MODIFIERS, Chisel.id("add_marble"));
    public static final ResourceKey<BiomeModifier> ADD_LIMESTONE = ResourceKey.create(
            ForgeRegistries.Keys.BIOME_MODIFIERS, Chisel.id("add_limestone"));
    public static final ResourceKey<BiomeModifier> ADD_DIABASE = ResourceKey.create(
            ForgeRegistries.Keys.BIOME_MODIFIERS, Chisel.id("add_diabase"));
    public static final ResourceKey<BiomeModifier> ADD_BASALT = ResourceKey.create(
            ForgeRegistries.Keys.BIOME_MODIFIERS, Chisel.id("add_basalt"));

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            // Register Configured Features (ore definitions with default vein sizes)
            .add(Registries.CONFIGURED_FEATURE, context -> {
                RuleTest stoneOreReplaceables = new TagMatchTest(net.minecraft.tags.BlockTags.BASE_STONE_OVERWORLD);

                // Marble - default vein size 64
                if (ChiselBlocks.MARBLE.getBlock("raw") != null) {
                    context.register(ORE_MARBLE, new ConfiguredFeature<>(Feature.ORE,
                            new OreConfiguration(stoneOreReplaceables,
                                    ChiselBlocks.MARBLE.getBlock("raw").get().defaultBlockState(), 64)));
                }

                // Limestone - default vein size 64
                if (ChiselBlocks.LIMESTONE.getBlock("raw") != null) {
                    context.register(ORE_LIMESTONE, new ConfiguredFeature<>(Feature.ORE,
                            new OreConfiguration(stoneOreReplaceables,
                                    ChiselBlocks.LIMESTONE.getBlock("raw").get().defaultBlockState(), 64)));
                }

                // Diabase - default vein size 32
                if (ChiselBlocks.DIABASE.getBlock("raw") != null) {
                    context.register(ORE_DIABASE, new ConfiguredFeature<>(Feature.ORE,
                            new OreConfiguration(stoneOreReplaceables,
                                    ChiselBlocks.DIABASE.getBlock("raw").get().defaultBlockState(), 32)));
                }

                // Basalt - default vein size 32 (disabled by default in config)
                if (ChiselBlocks.BASALT.getBlock("raw") != null) {
                    context.register(ORE_BASALT, new ConfiguredFeature<>(Feature.ORE,
                            new OreConfiguration(stoneOreReplaceables,
                                    ChiselBlocks.BASALT.getBlock("raw").get().defaultBlockState(), 32)));
                }
            })
            // Register Biome Modifiers using our custom configurable type
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
                var biomes = context.lookup(Registries.BIOME);
                var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
                HolderSet<Biome> overworldTag = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);

                // Marble biome modifier
                if (ChiselBlocks.MARBLE.getBlock("raw") != null) {
                    context.register(ADD_MARBLE, new ConfigurableOreBiomeModifier(
                            overworldTag,
                            configuredFeatures.getOrThrow(ORE_MARBLE),
                            "marble"
                    ));
                }

                // Limestone biome modifier
                if (ChiselBlocks.LIMESTONE.getBlock("raw") != null) {
                    context.register(ADD_LIMESTONE, new ConfigurableOreBiomeModifier(
                            overworldTag,
                            configuredFeatures.getOrThrow(ORE_LIMESTONE),
                            "limestone"
                    ));
                }

                // Diabase biome modifier
                if (ChiselBlocks.DIABASE.getBlock("raw") != null) {
                    context.register(ADD_DIABASE, new ConfigurableOreBiomeModifier(
                            overworldTag,
                            configuredFeatures.getOrThrow(ORE_DIABASE),
                            "diabase"
                    ));
                }

                // Basalt biome modifier (disabled by default in config)
                if (ChiselBlocks.BASALT.getBlock("raw") != null) {
                    context.register(ADD_BASALT, new ConfigurableOreBiomeModifier(
                            overworldTag,
                            configuredFeatures.getOrThrow(ORE_BASALT),
                            "basalt"
                    ));
                }
            });

    public ChiselWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Chisel.MODID));
    }

    @Override
    public String getName() {
        return "Chisel World Generation";
    }
}
