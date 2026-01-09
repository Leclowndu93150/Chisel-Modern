package com.leclowndu93150.chisel.worldgen;

import com.leclowndu93150.chisel.ChiselConfig;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.*;
import net.neoforged.neoforge.common.world.BiomeGenerationSettingsBuilder;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;

import java.util.List;

/**
 * A custom BiomeModifier that reads worldgen config values at runtime.
 * This allows users to configure ore generation (enable/disable, vein count, Y-levels)
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
                Holder.direct(new PlacedFeature(feature, placements))
        );
    }

    @Override
    public MapCodec<? extends BiomeModifier> codec() {
        return ChiselBiomeModifiers.CONFIGURABLE_ORE.get();
    }
}
