package com.leclowndu93150.chisel.worldgen;

import com.leclowndu93150.chisel.Chisel;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Registers custom BiomeModifier codecs for Chisel's configurable worldgen.
 */
public class ChiselBiomeModifiers {

    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Chisel.MODID);

    /**
     * Codec for ConfigurableOreBiomeModifier.
     * JSON format:
     * {
     *   "type": "chisel:configurable_ore",
     *   "biomes": "#minecraft:is_overworld",
     *   "feature": "chisel:ore_marble",
     *   "ore_type": "marble"
     * }
     */
    public static final RegistryObject<Codec<ConfigurableOreBiomeModifier>> CONFIGURABLE_ORE =
            BIOME_MODIFIER_SERIALIZERS.register("configurable_ore", () ->
                    RecordCodecBuilder.create(builder -> builder.group(
                            Biome.LIST_CODEC.fieldOf("biomes").forGetter(ConfigurableOreBiomeModifier::biomes),
                            ConfiguredFeature.CODEC.fieldOf("feature").forGetter(ConfigurableOreBiomeModifier::feature),
                            Codec.STRING.fieldOf("ore_type").forGetter(ConfigurableOreBiomeModifier::oreType)
                    ).apply(builder, ConfigurableOreBiomeModifier::new)));
}
