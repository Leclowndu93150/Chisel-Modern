package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTest;
import net.minecraft.world.level.levelgen.structure.templatesystem.TagMatchTest;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.BiomeModifiers;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ChiselWorldGenProvider extends DatapackBuiltinEntriesProvider {

    // Configured Features (what to generate)
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_MARBLE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Chisel.id("ore_marble"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_LIMESTONE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Chisel.id("ore_limestone"));
    public static final ResourceKey<ConfiguredFeature<?, ?>> ORE_DIABASE = ResourceKey.create(
            Registries.CONFIGURED_FEATURE, Chisel.id("ore_diabase"));

    // Placed Features (where to generate)
    public static final ResourceKey<PlacedFeature> ORE_MARBLE_UPPER = ResourceKey.create(
            Registries.PLACED_FEATURE, Chisel.id("ore_marble_upper"));
    public static final ResourceKey<PlacedFeature> ORE_MARBLE_LOWER = ResourceKey.create(
            Registries.PLACED_FEATURE, Chisel.id("ore_marble_lower"));
    public static final ResourceKey<PlacedFeature> ORE_LIMESTONE_UPPER = ResourceKey.create(
            Registries.PLACED_FEATURE, Chisel.id("ore_limestone_upper"));
    public static final ResourceKey<PlacedFeature> ORE_LIMESTONE_LOWER = ResourceKey.create(
            Registries.PLACED_FEATURE, Chisel.id("ore_limestone_lower"));
    public static final ResourceKey<PlacedFeature> ORE_DIABASE_PLACED = ResourceKey.create(
            Registries.PLACED_FEATURE, Chisel.id("ore_diabase"));

    // Biome Modifiers (add to biomes)
    public static final ResourceKey<BiomeModifier> ADD_MARBLE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, Chisel.id("add_marble"));
    public static final ResourceKey<BiomeModifier> ADD_LIMESTONE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, Chisel.id("add_limestone"));
    public static final ResourceKey<BiomeModifier> ADD_DIABASE = ResourceKey.create(
            NeoForgeRegistries.Keys.BIOME_MODIFIERS, Chisel.id("add_diabase"));

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.CONFIGURED_FEATURE, context -> {
                RuleTest stoneOreReplaceables = new TagMatchTest(net.minecraft.tags.BlockTags.BASE_STONE_OVERWORLD);

                if (ChiselBlocks.MARBLE.getBlock("raw") != null) {
                    context.register(ORE_MARBLE, new ConfiguredFeature<>(Feature.ORE,
                            new OreConfiguration(stoneOreReplaceables,
                                    ChiselBlocks.MARBLE.getBlock("raw").get().defaultBlockState(), 64)));
                }

                if (ChiselBlocks.LIMESTONE.getBlock("raw") != null) {
                    context.register(ORE_LIMESTONE, new ConfiguredFeature<>(Feature.ORE,
                            new OreConfiguration(stoneOreReplaceables,
                                    ChiselBlocks.LIMESTONE.getBlock("raw").get().defaultBlockState(), 64)));
                }

                if (ChiselBlocks.DIABASE.getBlock("raw") != null) {
                    context.register(ORE_DIABASE, new ConfiguredFeature<>(Feature.ORE,
                            new OreConfiguration(stoneOreReplaceables,
                                    ChiselBlocks.DIABASE.getBlock("raw").get().defaultBlockState(), 32)));
                }
            })
            .add(Registries.PLACED_FEATURE, context -> {
                var configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);

                if (ChiselBlocks.MARBLE.getBlock("raw") != null) {
                    context.register(ORE_MARBLE_UPPER, new PlacedFeature(
                            configuredFeatures.getOrThrow(ORE_MARBLE),
                            commonOrePlacement(6, HeightRangePlacement.uniform(
                                    VerticalAnchor.absolute(64), VerticalAnchor.absolute(128)))));

                    context.register(ORE_MARBLE_LOWER, new PlacedFeature(
                            configuredFeatures.getOrThrow(ORE_MARBLE),
                            commonOrePlacement(2, HeightRangePlacement.uniform(
                                    VerticalAnchor.absolute(0), VerticalAnchor.absolute(60)))));
                }

                if (ChiselBlocks.LIMESTONE.getBlock("raw") != null) {
                    context.register(ORE_LIMESTONE_UPPER, new PlacedFeature(
                            configuredFeatures.getOrThrow(ORE_LIMESTONE),
                            commonOrePlacement(6, HeightRangePlacement.uniform(
                                    VerticalAnchor.absolute(64), VerticalAnchor.absolute(128)))));

                    context.register(ORE_LIMESTONE_LOWER, new PlacedFeature(
                            configuredFeatures.getOrThrow(ORE_LIMESTONE),
                            commonOrePlacement(2, HeightRangePlacement.uniform(
                                    VerticalAnchor.absolute(0), VerticalAnchor.absolute(60)))));
                }

                // Diabase generates near lava pools (Y=-64 to Y=0, concentrated around lava level)
                if (ChiselBlocks.DIABASE.getBlock("raw") != null) {
                    context.register(ORE_DIABASE_PLACED, new PlacedFeature(
                            configuredFeatures.getOrThrow(ORE_DIABASE),
                            commonOrePlacement(8, HeightRangePlacement.triangle(
                                    VerticalAnchor.absolute(-64), VerticalAnchor.absolute(0)))));
                }
            })
            .add(NeoForgeRegistries.Keys.BIOME_MODIFIERS, context -> {
                var biomes = context.lookup(Registries.BIOME);
                var placedFeatures = context.lookup(Registries.PLACED_FEATURE);
                var overworldTag = biomes.getOrThrow(BiomeTags.IS_OVERWORLD);

                if (ChiselBlocks.MARBLE.getBlock("raw") != null) {
                    context.register(ADD_MARBLE, new BiomeModifiers.AddFeaturesBiomeModifier(
                            overworldTag,
                            HolderSet.direct(
                                    placedFeatures.getOrThrow(ORE_MARBLE_UPPER),
                                    placedFeatures.getOrThrow(ORE_MARBLE_LOWER)
                            ),
                            GenerationStep.Decoration.UNDERGROUND_ORES));
                }

                if (ChiselBlocks.LIMESTONE.getBlock("raw") != null) {
                    context.register(ADD_LIMESTONE, new BiomeModifiers.AddFeaturesBiomeModifier(
                            overworldTag,
                            HolderSet.direct(
                                    placedFeatures.getOrThrow(ORE_LIMESTONE_UPPER),
                                    placedFeatures.getOrThrow(ORE_LIMESTONE_LOWER)
                            ),
                            GenerationStep.Decoration.UNDERGROUND_ORES));
                }

                if (ChiselBlocks.DIABASE.getBlock("raw") != null) {
                    context.register(ADD_DIABASE, new BiomeModifiers.AddFeaturesBiomeModifier(
                            overworldTag,
                            HolderSet.direct(
                                    placedFeatures.getOrThrow(ORE_DIABASE_PLACED)
                            ),
                            GenerationStep.Decoration.UNDERGROUND_ORES));
                }
            });

    public ChiselWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(Chisel.MODID));
    }

    private static List<PlacementModifier> commonOrePlacement(int count, PlacementModifier heightRange) {
        return List.of(
                CountPlacement.of(count),
                InSquarePlacement.spread(),
                heightRange,
                BiomeFilter.biome()
        );
    }

    private static List<PlacementModifier> rareOrePlacement(int chance, PlacementModifier heightRange) {
        return List.of(
                RarityFilter.onAverageOnceEvery(chance),
                InSquarePlacement.spread(),
                heightRange,
                BiomeFilter.biome()
        );
    }

    @Override
    public String getName() {
        return "Chisel World Generation";
    }
}
