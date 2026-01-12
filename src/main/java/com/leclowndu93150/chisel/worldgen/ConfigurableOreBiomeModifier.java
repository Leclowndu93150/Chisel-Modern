package com.leclowndu93150.chisel.worldgen;

import com.leclowndu93150.chisel.ChiselConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.List;

/**
 * A custom BiomeModifier that reads worldgen config values at runtime.
 * This allows users to configure ore generation (enable/disable, vein count, vein size, Y-levels)
 * without needing to regenerate data or restart the game.
 */
public record ConfigurableOreBiomeModifier(
        HolderSet<Biome> biomes,
        Holder<ConfiguredFeature<?, ?>> feature,
        String oreType
) implements BiomeModifier {

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD || !this.biomes.contains(biome)) {
            return;
        }

        ChiselConfig.OreGenConfig config = ChiselConfig.getOreConfig(oreType);
        if (config == null || !config.enabled() || config.veinCount() <= 0) {
            return;
        }

        Holder<ConfiguredFeature<?, ?>> featureToUse = feature;

        ConfiguredFeature<?, ?> originalFeature = feature.value();
        if (originalFeature.config() instanceof OreConfiguration oreConfig && oreConfig.size != config.veinSize()) {
            OreConfiguration newOreConfig = new OreConfiguration(
                    oreConfig.targetStates,
                    config.veinSize(),
                    oreConfig.discardChanceOnAirExposure
            );

            @SuppressWarnings("unchecked")
            Feature<OreConfiguration> oreFeature = (Feature<OreConfiguration>) originalFeature.feature();
            ConfiguredFeature<OreConfiguration, Feature<OreConfiguration>> newFeature =
                    new ConfiguredFeature<>(oreFeature, newOreConfig);

            featureToUse = Holder.direct(newFeature);
        }

        List<PlacementModifier> placements = List.of(
                CountPlacement.of(config.veinCount()),
                InSquarePlacement.spread(),
                HeightRangePlacement.uniform(
                        VerticalAnchor.absolute(config.minY()),
                        VerticalAnchor.absolute(config.maxY())
                ),
                BiomeFilter.biome()
        );

        BiomeGenerationSettingsBuilder generationSettings = builder.getGenerationSettings();
        generationSettings.addFeature(
                GenerationStep.Decoration.UNDERGROUND_ORES,
                Holder.direct(new PlacedFeature(featureToUse, placements))
        );
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return ChiselBiomeModifiers.CONFIGURABLE_ORE.get();
    }
}
