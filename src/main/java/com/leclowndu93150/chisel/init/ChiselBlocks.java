package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.api.block.VariationData;
import com.leclowndu93150.chisel.block.BlockAutoChisel;
import com.leclowndu93150.chisel.block.BlockCarvable;
import com.leclowndu93150.chisel.block.BlockCarvablePane;
import com.leclowndu93150.chisel.data.VariantTemplates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.leclowndu93150.chisel.data.ChiselModelTemplates.*;

/**
 * Registration class for all Chisel blocks.
 * Complete 1:1 port from Chisel 1.18.2 Features.java and LegacyFeatures.java
 */
public class ChiselBlocks {

    // Collection of all block types for iteration
    public static final List<ChiselBlockType<?>> ALL_BLOCK_TYPES = new ArrayList<>();

    // ==================== METALS (with mod compat tags) ====================

    public static final ChiselBlockType<BlockCarvable> ALUMINUM = registerType(
            new ChiselBlockType<BlockCarvable>("metals/aluminum")
                    .groupName("Aluminum Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> BRONZE = registerType(
            new ChiselBlockType<BlockCarvable>("metals/bronze")
                    .groupName("Bronze Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.TERRACOTTA_ORANGE)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_IRON_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> COBALT = registerType(
            new ChiselBlockType<BlockCarvable>("metals/cobalt")
                    .groupName("Cobalt Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.TERRACOTTA_CYAN)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_IRON_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> COPPER = registerType(
            new ChiselBlockType<BlockCarvable>("metals/copper")
                    .groupName("Copper Block")
                    .addVanillaBlock(Blocks.COPPER_BLOCK)
                    .properties(() -> Blocks.COPPER_BLOCK)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> ELECTRUM = registerType(
            new ChiselBlockType<BlockCarvable>("metals/electrum")
                    .groupName("Electrum Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.TERRACOTTA_YELLOW)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> GOLD = registerType(
            new ChiselBlockType<BlockCarvable>("gold")
                    .groupName("Gold Block")
                    .addVanillaBlock(Blocks.GOLD_BLOCK)
                    .properties(() -> Blocks.GOLD_BLOCK)
                    .mapColor(MapColor.GOLD)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_IRON_TOOL)
                    .variations(VariantTemplates.METAL)
                    .variations(VariantTemplates.METAL_TERRAIN)
                    .variation("cart", "Cart", cubeBottomTop())
                    .variation("star", "Stars", cubeBottomTop())
                    .variation("goldeye", "Goldeye")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> INVAR = registerType(
            new ChiselBlockType<BlockCarvable>("metals/invar")
                    .groupName("Invar Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> IRON = registerType(
            new ChiselBlockType<BlockCarvable>("iron")
                    .groupName("Iron Block")
                    .addVanillaBlock(Blocks.IRON_BLOCK)
                    .properties(() -> Blocks.IRON_BLOCK)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_IRON_TOOL)
                    .variations(VariantTemplates.METAL)
                    .variations(VariantTemplates.METAL_TERRAIN)
                    .variation("moon", "Moon", cubeBottomTop())
                    .variation("vents", "Vents", cubeColumn())
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> LEAD = registerType(
            new ChiselBlockType<BlockCarvable>("metals/lead")
                    .groupName("Lead Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.TERRACOTTA_LIGHT_BLUE)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> NICKEL = registerType(
            new ChiselBlockType<BlockCarvable>("metals/nickel")
                    .groupName("Nickel Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.TERRACOTTA_LIGHT_BLUE)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> PLATINUM = registerType(
            new ChiselBlockType<BlockCarvable>("metals/platinum")
                    .groupName("Platinum Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.TERRACOTTA_LIGHT_BLUE)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> SILVER = registerType(
            new ChiselBlockType<BlockCarvable>("metals/silver")
                    .groupName("Silver Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.COLOR_LIGHT_GRAY)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> STEEL = registerType(
            new ChiselBlockType<BlockCarvable>("metals/steel")
                    .groupName("Steel Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.COLOR_GRAY)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> TIN = registerType(
            new ChiselBlockType<BlockCarvable>("metals/tin")
                    .groupName("Tin Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> URANIUM = registerType(
            new ChiselBlockType<BlockCarvable>("metals/uranium")
                    .groupName("Uranium Block")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.TERRACOTTA_GREEN)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.METAL)
                    .build()
    );

    // ==================== STONE TYPES ====================

    public static final ChiselBlockType<BlockCarvable> ANDESITE = registerType(
            new ChiselBlockType<BlockCarvable>("andesite")
                    .addVanillaBlock(Blocks.ANDESITE)
                    .addVanillaBlock(Blocks.POLISHED_ANDESITE)
                    .properties(() -> Blocks.ANDESITE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> BASALT = registerType(
            new ChiselBlockType<BlockCarvable>("basalt")
                    .addVanillaBlock(Blocks.BASALT)
                    .addVanillaBlock(Blocks.POLISHED_BASALT)
                    .properties(() -> Blocks.BASALT)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> BRICKS = registerType(
            new ChiselBlockType<BlockCarvable>("bricks")
                    .addVanillaBlock(Blocks.BRICKS)
                    .properties(() -> Blocks.BRICKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> COBBLESTONE = registerType(
            new ChiselBlockType<BlockCarvable>("cobblestone")
                    .addVanillaBlock(Blocks.COBBLESTONE)
                    .properties(() -> Blocks.COBBLESTONE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.COBBLESTONE)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> COBBLESTONE_MOSSY = registerType(
            new ChiselBlockType<BlockCarvable>("mossy_cobblestone")
                    .addVanillaBlock(Blocks.MOSSY_COBBLESTONE)
                    .properties(() -> Blocks.MOSSY_COBBLESTONE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.COBBLESTONE_MOSSY)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> DIORITE = registerType(
            new ChiselBlockType<BlockCarvable>("diorite")
                    .addVanillaBlock(Blocks.DIORITE)
                    .addVanillaBlock(Blocks.POLISHED_DIORITE)
                    .properties(() -> Blocks.DIORITE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> ENDSTONE = registerType(
            new ChiselBlockType<BlockCarvable>("end_stone")
                    .addVanillaBlock(Blocks.END_STONE)
                    .addVanillaBlock(Blocks.END_STONE_BRICKS)
                    .properties(() -> Blocks.END_STONE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> GRANITE = registerType(
            new ChiselBlockType<BlockCarvable>("granite")
                    .addVanillaBlock(Blocks.GRANITE)
                    .addVanillaBlock(Blocks.POLISHED_GRANITE)
                    .properties(() -> Blocks.GRANITE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> PRISMARINE = registerType(
            new ChiselBlockType<BlockCarvable>("prismarine")
                    .addVanillaBlock(Blocks.PRISMARINE)
                    .addVanillaBlock(Blocks.PRISMARINE_BRICKS)
                    .addVanillaBlock(Blocks.DARK_PRISMARINE)
                    .properties(() -> Blocks.PRISMARINE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> PURPUR = registerType(
            new ChiselBlockType<BlockCarvable>("purpur")
                    .addVanillaBlock(Blocks.PURPUR_BLOCK)
                    .addVanillaBlock(Blocks.PURPUR_PILLAR)
                    .properties(() -> Blocks.PURPUR_BLOCK)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> QUARTZ = registerType(
            new ChiselBlockType<BlockCarvable>("quartz")
                    .addVanillaBlock(Blocks.QUARTZ_BLOCK)
                    .addVanillaBlock(Blocks.QUARTZ_PILLAR)
                    .addVanillaBlock(Blocks.CHISELED_QUARTZ_BLOCK)
                    .addVanillaBlock(Blocks.SMOOTH_QUARTZ)
                    .properties(() -> Blocks.QUARTZ_BLOCK)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> SANDSTONE = registerType(
            new ChiselBlockType<BlockCarvable>("sandstone")
                    .addVanillaBlock(Blocks.SANDSTONE)
                    .addVanillaBlock(Blocks.CHISELED_SANDSTONE)
                    .addVanillaBlock(Blocks.CUT_SANDSTONE)
                    .addVanillaBlock(Blocks.SMOOTH_SANDSTONE)
                    .properties(() -> Blocks.SANDSTONE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .variation("extra/bevel_creeper", "Bevel Creeper")
                    .variation("extra/glyphs", "Glyphs")
                    .variation("extra/small", "Small")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> RED_SANDSTONE = registerType(
            new ChiselBlockType<BlockCarvable>("red_sandstone")
                    .addVanillaBlock(Blocks.RED_SANDSTONE)
                    .addVanillaBlock(Blocks.CHISELED_RED_SANDSTONE)
                    .addVanillaBlock(Blocks.CUT_RED_SANDSTONE)
                    .addVanillaBlock(Blocks.SMOOTH_RED_SANDSTONE)
                    .properties(() -> Blocks.RED_SANDSTONE)
                    .mapColor(MapColor.COLOR_ORANGE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .variation("extra/bevel_skeleton", "Bevel Skeleton")
                    .variation("extra/glyphs", "Glyphs")
                    .variation("extra/small", "Small")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> STONE_BRICKS = registerType(
            new ChiselBlockType<BlockCarvable>("stone_bricks")
                    .addVanillaBlock(Blocks.STONE)
                    .addVanillaBlock(Blocks.STONE_BRICKS)
                    .addVanillaBlock(Blocks.CHISELED_STONE_BRICKS)
                    .addVanillaBlock(Blocks.CRACKED_STONE_BRICKS)
                    .properties(() -> Blocks.STONE_BRICKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .variation("extra/largeornate", "Large Ornate")
                    .variation("extra/poison", "Poison")
                    .variation("extra/sunken", "Sunken")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> TERRACOTTA = registerType(
            new ChiselBlockType<BlockCarvable>("terracotta")
                    .addVanillaBlock(Blocks.TERRACOTTA)
                    .properties(() -> Blocks.TERRACOTTA)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    // ==================== CUSTOM STONES (No vanilla equivalent) ====================

    public static final ChiselBlockType<BlockCarvable> LIMESTONE = registerType(
            new ChiselBlockType<BlockCarvable>("limestone")
                    .properties(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.TERRACOTTA_GREEN)
                            .strength(1.0f, 5.0f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops())
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variation(VariantTemplates.RAW)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> MARBLE = registerType(
            new ChiselBlockType<BlockCarvable>("marble")
                    .properties(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.QUARTZ)
                            .strength(1.75f, 10.0f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops())
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variation(VariantTemplates.RAW)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> DIABASE = registerType(
            new ChiselBlockType<BlockCarvable>("diabase")
                    .properties(() -> Blocks.BASALT)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation(VariantTemplates.RAW)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    // ==================== GEM BLOCKS ====================

    public static final ChiselBlockType<BlockCarvable> DIAMOND = registerType(
            new ChiselBlockType<BlockCarvable>("diamond")
                    .groupName("Diamond Block")
                    .addVanillaBlock(Blocks.DIAMOND_BLOCK)
                    .properties(() -> Blocks.DIAMOND_BLOCK)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_IRON_TOOL)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .variation("terrain_diamond_embossed", "Embossed", cubeColumn())
                    .variation("terrain_diamond_gem", "Gem", cubeColumn())
                    .variation("terrain_diamond_cells", "Cells")
                    .variation("terrain_diamond_space", "Purple Space")
                    .variation("terrain_diamond_spaceblack", "Black Space")
                    .variation("terrain_diamond_simple", "Simple", cubeColumn())
                    .variation("terrain_diamond_bismuth", "Bismuth")
                    .variation("terrain_diamond_crushed", "Crushed")
                    .variation("terrain_diamond_four", "Tiles")
                    .variation("terrain_diamond_fourornate", "Ornate Tiles")
                    .variation("terrain_diamond_zelda", "Zelda")
                    .variation("terrain_diamond_ornatelayer", "Gold-Encrusted")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> EMERALD = registerType(
            new ChiselBlockType<BlockCarvable>("emerald")
                    .groupName("Emerald Block")
                    .addVanillaBlock(Blocks.EMERALD_BLOCK)
                    .properties(() -> Blocks.EMERALD_BLOCK)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_IRON_TOOL)
                    .tag(BlockTags.BEACON_BASE_BLOCKS)
                    .variation("panel", "Tile")
                    .variation("panelclassic", "Panel")
                    .variation("smooth", "Raw")
                    .variation("chunk", "Cobble")
                    .variation("goldborder", "Gold-Encrusted")
                    .variation("zelda", "Zelda")
                    .variation("cell", "Cell")
                    .variation("cellbismuth", "Cell Bismuth")
                    .variation("four", "Tiles")
                    .variation("fourornate", "Ornate Tiles")
                    .variation("ornate", "Ornate")
                    .variation("masonryemerald", "Masonry")
                    .variation("emeraldcircle", "Circular")
                    .variation("emeraldprismatic", "Prism")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> LAPIS = registerType(
            new ChiselBlockType<BlockCarvable>("lapis")
                    .groupName("Lapis Block")
                    .addVanillaBlock(Blocks.LAPIS_BLOCK)
                    .properties(() -> Blocks.LAPIS_BLOCK)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation("terrain_lapisblock_chunky", "Cobble")
                    .variation("terrain_lapisblock_panel", "Tile")
                    .variation("terrain_lapisblock_zelda", "Zelda")
                    .variation("terrain_lapisornate", "Wood-Framed")
                    .variation("terrain_lapistile", "Panel")
                    .variation("a1_blocklapis_panel", "Shiny Panel")
                    .variation("a1_blocklapis_smooth", "Raw")
                    .variation("a1_blocklapis_ornatelayer", "Gold-Encrusted")
                    .variation("masonrylapis", "Masonry")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> REDSTONE = registerType(
            new ChiselBlockType<BlockCarvable>("redstone")
                    .groupName("Redstone Block")
                    .addVanillaBlock(Blocks.REDSTONE_BLOCK)
                    .properties(() -> Blocks.REDSTONE_BLOCK)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.STONE)
                    .build()
    );

    // ==================== GLOWSTONE ====================

    public static final ChiselBlockType<BlockCarvable> GLOWSTONE = registerType(
            new ChiselBlockType<BlockCarvable>("glowstone")
                    .addVanillaBlock(Blocks.GLOWSTONE)
                    .properties(() -> Blocks.GLOWSTONE)
                    .variations(VariantTemplates.STONE)
                    .variation("extra/bismuth", "Bismuth")
                    .variation("extra/tiles_large_bismuth", "Tiles Large Bismuth")
                    .variation("extra/tiles_medium_bismuth", "Tiles Medium Bismuth")
                    .variation("extra/neon", "Neon")
                    .variation("extra/neon_panel", "Neon Panel")
                    .build()
    );

    // ==================== COAL/CHARCOAL ====================

    public static final ChiselBlockType<BlockCarvable> COAL = registerType(
            new ChiselBlockType<BlockCarvable>("coal")
                    .addVanillaBlock(Blocks.COAL_BLOCK)
                    .properties(() -> Blocks.COAL_BLOCK)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation(VariantTemplates.RAW)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> CHARCOAL = registerType(
            new ChiselBlockType<BlockCarvable>("charcoal")
                    .properties(() -> Blocks.COAL_BLOCK)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation(VariantTemplates.RAW)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    // ==================== ICE ====================

    public static final ChiselBlockType<BlockCarvable> ICE = registerType(
            new ChiselBlockType<BlockCarvable>("ice")
                    .addVanillaBlock(Blocks.ICE)
                    .properties(() -> Blocks.ICE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.ICE)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> ICE_PILLAR = registerType(
            new ChiselBlockType<BlockCarvable>("icepillar")
                    .addVanillaBlock(Blocks.ICE)
                    .properties(() -> Blocks.ICE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.ICE)
                    .variations(VariantTemplates.PILLAR)
                    .build()
    );

    // ==================== SPECIAL BLOCKS ====================

    public static final ChiselBlockType<BlockCarvable> ANTIBLOCK = registerType(
            new ChiselBlockType<BlockCarvable>("antiblock")
                    .properties(() -> Blocks.STONE)
                    .mapColor(MapColor.COLOR_RED)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ANTIBLOCK)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> BROWNSTONE = registerType(
            new ChiselBlockType<BlockCarvable>("brownstone")
                    .properties(BlockBehaviour.Properties.of()
                            .sound(SoundType.STONE)
                            .strength(1.0f))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variation("default", "Raw")
                    .variation("block", "Big Tiled")
                    .variation("blocks", "Tiled")
                    .variation("doubleslab", "Bisected", cubeColumn("doubleslab-side", "block"))
                    .variation("weathered", "Weathered")
                    .variation("weathered_block", "Weathered Big Tile")
                    .variation("weathered_doubleslab", "Weathered Bisected", cubeColumn("weathered_doubleslab-side", "weathered_block"))
                    .variation("weathered_blocks", "Weathered Tiles")
                    .variation("weathered_half", "Half-Weathered", cubeBottomTop("weathered_half-side", "default", "weathered"))
                    .variation("weathered_block_half", "Half-Weathered Big Tile", cubeBottomTop("weathered_block_half-side", "block", "weathered_block"))
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> CLOUD = registerType(
            new ChiselBlockType<BlockCarvable>("cloud")
                    .properties(BlockBehaviour.Properties.of()
                            .sound(SoundType.WOOL)
                            .explosionResistance(0.3f))
                    .variation("cloud", "Cloud")
                    .variation("large", "Bricks")
                    .variation("small", "Small Bricks")
                    .variation("vertical", "Vertical Bricks")
                    .variation("grid", "Small Tiles")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> FACTORY = registerType(
            new ChiselBlockType<BlockCarvable>("factory")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.TERRACOTTA_BROWN)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation("dots", "Dotted Rusty Plate")
                    .variation("rust2", "Rusty Plate")
                    .variation("rust", "Rusty Plate")
                    .variation("platex", "Slightly Rusty Plate")
                    .variation("wireframewhite", "Wireframe")
                    .variation("wireframe", "Purple Wireframe")
                    .variation("hazard", "Yellow-Black Caution Stripes")
                    .variation("hazardorange", "Orange-White Caution Stripes")
                    .variation("circuit", "Circuit")
                    .variation("metalbox", "Metal Box", cubeColumn())
                    .variation("goldplate", "Gold-Plated Circuit")
                    .variation("goldplating", "Gold-Framed Purple Plates")
                    .variation("grinder", "Grinder")
                    .variation("plating", "Old Vents")
                    .variation("rustplates", "Segmented Rusty Plates")
                    .variation("column", "Metal Column", cubeColumn())
                    .variation("frameblue", "Blue-Framed Circuit")
                    .variation("iceiceice", "Ice Ice Ice")
                    .variation("tilemosaic", "Blue Circuits")
                    .variation("vent", "Vents", cubeColumn())
                    .variation("wireframeblue", "Blue Wireframe")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> FUTURA = registerType(
            new ChiselBlockType<BlockCarvable>("futura")
                    .properties(() -> Blocks.STONE)
                    .mapColor(MapColor.COLOR_YELLOW)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variation("screen_metallic", "Gray Screen", twoLayerTopShaded("screen_metallic", "screen_discoherent"))
                    .variation("screen_cyan", "Cyan Screen", twoLayerTopShaded("screen_cyan", "screen_discoherent"))
                    .variation("controller", "ME Controller", threeLayerTopShaded("controller_particle", "lines_plating", "rainbow_lines", "lines_invalid"))
                    .variation("wavy", "Rainbowliciously Wavy", twoLayerTopShaded("rainbow_wave_particle", "rainbow_wave_base", "rainbow_wave"))
                    .variation("controller_purple", "Purple ME Controller", threeLayerTopShaded("controller_unity_particle", "unity_lines_plating", "unity_lines", "lines_invalid"))
                    .variation("uber_wavy", "Fabulously Wavy", twoLayerTopShaded("uber_rainbow", "uber_rainbow"))
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> LABORATORY = registerType(
            new ChiselBlockType<BlockCarvable>("laboratory")
                    .properties(() -> Blocks.STONE)
                    .mapColor(MapColor.SNOW)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variation("wallpanel", "Wall Panel")
                    .variation("dottedpanel", "Dotted Panel")
                    .variation("roundel", "Roundel")
                    .variation("wallvents", "Wall Vents", cubeColumn())
                    .variation("largetile", "Large Tile")
                    .variation("smalltile", "Small Tile")
                    .variation("floortile", "Floor Tile")
                    .variation("checkertile", "Checker Tile")
                    .variation("fuzzscreen", "Fuzz Screen")
                    .variation("largesteel", "Large Steel", cubeColumn())
                    .variation("smallsteel", "Small Steel", cubeColumn())
                    .variation("directionleft", "Direction Left", cubeColumn())
                    .variation("directionright", "Direction Right", cubeColumn())
                    .variation("infocon", "Info Console", cubeColumn())
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> TECHNICAL = registerType(
            new ChiselBlockType<BlockCarvable>("technical")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.TERRACOTTA_LIGHT_GRAY)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation("scaffold", "Rusty Scaffold")
                    .variation("cautiontape", "Caution-Framed Plates")
                    .variation("industrialrelic", "Industrial Relic")
                    .variation("pipeslarge", "Large Pipes")
                    .variation("fanfast", "Fan (Fast)", cubeColumn())
                    .variation("pipessmall", "Small Pipes")
                    .variation("fanstill", "Fan (Off)", cubeColumn())
                    .variation("vent", "Vent")
                    .variation("ventglowing", "Glowing Vent")
                    .variation("insulationv2", "Insulation")
                    .variation("spinningstuffanim", "Gears & Flywheels")
                    .variation("cables", "Cables")
                    .variation("rustyboltedplates", "Rusty Bolted Plates")
                    .variation("grate", "Grate")
                    .variation("malfunctionfan", "Fan (Malfunctioning)", cubeColumn())
                    .variation("graterusty", "Rusty Grate")
                    .variation("scaffoldtransparent", "Rusty Scaffold - Transparent")
                    .variation("fanfasttransparent", "Fan (Fast) - Transparent", cubeColumn())
                    .variation("fanstilltransparent", "Fan (Still) - Transparent", cubeColumn())
                    .variation("massivefan", "Massive Fan")
                    .variation("massivehexplating", "Massive Hexagonal Plating")
                    // Merged from TECHNICAL_NEW
                    .variation("weatheredgreenpanels", "Extremely Corroded Panels")
                    .variation("weatheredorangepanels", "Extremely Rusted Panels")
                    .variation("sturdy", "Sturdy")
                    .variation("megacell", "Megacell Battery", cubeBottomTop())
                    .variation("exhaustplating", "Exhaust Plating")
                    .variation("makeshiftpanels", "Sloppy Plating")
                    .variation("engineering", "Engineer's Pipes")
                    .variation("scaffoldlarge", "Large Rusty Scaffold")
                    .variation("piping", "Pipes")
                    .variation("oldetimeyserveranim", "Oldetimey Server")
                    .variation("tapedrive", "Tape Drive")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> TYRIAN = registerType(
            new ChiselBlockType<BlockCarvable>("tyrian")
                    .properties(() -> Blocks.IRON_BLOCK)
                    .mapColor(MapColor.TERRACOTTA_CYAN)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation("shining", "Disordered Metal Bits")
                    .variation("tyrian", "Metal Plates")
                    .variation("chaotic", "Disordered Purple Bits")
                    .variation("softplate", "Purple Plates")
                    .variation("rust", "Rust")
                    .variation("elaborate", "Shiny Plates")
                    .variation("routes", "Routes")
                    .variation("platform", "Platform")
                    .variation("platetiles", "Small Uneven Tiles")
                    .variation("diagonal", "Diagonal Plates")
                    .variation("dent", "Dent")
                    .variation("blueplating", "Blue Plates")
                    .variation("black", "Black Scaled Plates")
                    .variation("black2", "Black Strips")
                    .variation("opening", "Opening")
                    .variation("plate", "Shiny Plate")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> VALENTINES = registerType(
            new ChiselBlockType<BlockCarvable>("valentines")
                    .properties(BlockBehaviour.Properties.of()
                            .strength(1.5f, 20.0f)
                            .sound(SoundType.STONE))
                    .variation("1", "Pink Marker")
                    .variation("2", "Gray Rocky")
                    .variation("3", "Pink Heart")
                    .variation("4", "Pink Rocky")
                    .variation("5", "Pink Tile")
                    .variation("6", "Pink Cracks")
                    .variation("7", "Pink Studded")
                    .variation("8", "Flame")
                    .variation("9", "Pink Steel")
                    .variation("companion", "Companion Cube")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> VOIDSTONE = registerType(
            new ChiselBlockType<BlockCarvable>("voidstone")
                    .properties(() -> Blocks.OBSIDIAN)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation(VariantTemplates.RAW)
                    .variation("quarters", "Medium Tiles")
                    .variation("smooth", "Smooth")
                    .variation("skulls", "Skulls")
                    .variation("rune", "Rune")
                    .variation("metalborder", "Metallic")
                    .variation("eye", "Eye")
                    .variation("bevel", "Bevel")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> VOIDSTONE_ENERGISED = registerType(
            new ChiselBlockType<BlockCarvable>("voidstonerunic_anim")
                    .groupName("Energised Voidstone")
                    .properties(() -> Blocks.OBSIDIAN)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation("raw", "Raw", twoLayerTopShaded("raw", "background"))
                    .variation("quarters", "Medium Tiles", twoLayerTopShaded("quarters", "background"))
                    .variation("smooth", "Smooth", twoLayerTopShaded("smooth", "background"))
                    .variation("skulls", "Skulls", twoLayerTopShaded("skulls", "background"))
                    .variation("rune", "Rune", twoLayerTopShaded("rune", "background"))
                    .variation("metalborder", "Metallic", twoLayerTopShaded("metalborder", "background"))
                    .variation("eye", "Eye", twoLayerTopShaded("eye", "background"))
                    .variation("bevel", "Bevel", twoLayerTopShaded("bevel", "background"))
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> VOIDSTONE_RUNIC = registerType(
            new ChiselBlockType<BlockCarvable>("voidstonerunic")
                    .groupName("Runic Voidstone")
                    .properties(() -> Blocks.OBSIDIAN)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation("black", "Black")
                    .variation("red", "Red")
                    .variation("green", "Green")
                    .variation("brown", "Brown")
                    .variation("blue", "Blue")
                    .variation("purple", "Purple")
                    .variation("cyan", "Cyan")
                    .variation("lightgray", "Light Gray")
                    .variation("pink", "Pink")
                    .variation("lime", "Lime")
                    .variation("yellow", "Yellow")
                    .variation("lightblue", "Light Blue")
                    .variation("magenta", "Magenta")
                    .variation("orange", "Orange")
                    .build()
    );

    // ==================== LAVASTONE / WATERSTONE ====================
    // These use 2-layer models with fluid textures underneath stone patterns
    // Textures come from block/fluid/ folder, tint index 1 is for lava/water color

    public static final ChiselBlockType<BlockCarvable> LAVASTONE = registerType(
            new ChiselBlockType<BlockCarvable>("lavastone")
                    .properties(BlockBehaviour.Properties.of()
                            .strength(4.0f, 50.0f)
                            .sound(SoundType.STONE)
                            .lightLevel(state -> 15))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.LAVASTONE)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> WATERSTONE = registerType(
            new ChiselBlockType<BlockCarvable>("waterstone")
                    .properties(BlockBehaviour.Properties.of()
                            .strength(4.0f, 50.0f)
                            .sound(SoundType.STONE))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variations(VariantTemplates.WATERSTONE)
                    .build()
    );

    // ==================== LEGACY BLOCKS ====================

    public static final ChiselBlockType<BlockCarvable> DIRT = registerType(
            new ChiselBlockType<BlockCarvable>("dirt")
                    .addVanillaBlock(Blocks.DIRT)
                    .properties(BlockBehaviour.Properties.of()
                            .strength(0.5f, 0.0f)
                            .sound(SoundType.GRAVEL))
                    .tag(BlockTags.MINEABLE_WITH_SHOVEL)
                    .variation("bricks", "Bricks")
                    .variation("netherbricks", "Nether Bricks")
                    .variation("bricks3", "Small Bricks")
                    .variation("cobble", "Cobble")
                    .variation("reinforcedcobbledirt", "Reinforced Cobble")
                    .variation("reinforceddirt", "Reinforced Dirt")
                    .variation("happy", "Happy Souls")
                    .variation("bricks2", "Disordered Bricks")
                    .variation("bricks_dirt2", "Brick-Topped Dirt")
                    .variation("hor", "Horizontal Streaks", cubeColumn("hor-ctmh", "hor-top"))
                    .variation("vert", "Vertical Streaks")
                    .variation("layers", "Layers")
                    .variation("vertical", "Vertical Layers")
                    .variation("chunky", "Chunky")
                    .variation("horizontal", "Horizontal")
                    .variation("plate", "Plate")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> GLASS = registerType(
            new ChiselBlockType<BlockCarvable>("glass")
                    .addVanillaBlock(Blocks.GLASS)
                    .properties(BlockBehaviour.Properties.of()
                            .sound(SoundType.GLASS)
                            .explosionResistance(0.3f)
                            .noOcclusion())
                    .variation("terrain_glassbubble", "Bubble")
                    .variation("japanese", "Japanese")
                    .variation("terrain_glassdungeon", "Dungeon")
                    .variation("terrain_glasslight", "Light")
                    .variation("terrain_glassnoborder", "Gray-Bordered")
                    .variation("terrain_glass_ornatesteel", "Ornate Steel")
                    .variation("terrain_glass_screen", "Screen")
                    .variation("terrain_glassshale", "Shale")
                    .variation("terrain_glass_steelframe", "Steel-Framed")
                    .variation("terrain_glassstone", "Stone-Framed")
                    .variation("terrain_glassstreak", "Streaks")
                    .variation("terrain_glass_thickgrid", "Thick Grid")
                    .variation("terrain_glass_thingrid", "Thin Grid")
                    .variation("a1_glasswindow_ironfencemodern", "Iron Fence")
                    .variation("chrono", "Asymmetrical Leaded Glass")
                    .variation("chinese2", "Chinese")
                    .variation("japanese2", "Japanese 2")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> MARBLE_PILLAR = registerType(
            new ChiselBlockType<BlockCarvable>("marblepillar")
                    .groupName("Marble Pillar")
                    .properties(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.QUARTZ)
                            .strength(1.75f, 10.0f)
                            .sound(SoundType.STONE))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.PILLAR)
                    .variation("simple", "Simple Pillar", columnPillar())
                    .variation("rough", "Rough", columnPillar())
                    .variation("widedecor", "Decor-Capped Wide Pillar", columnPillar())
                    .variation("widegreek", "Greek-Capped Wide Pillar", columnPillar())
                    .variation("wideplain", "Plain-Capped Wide Pillar", columnPillar())
                    .variation("pillar", "Large Pillar", columnPillar())
                    .variation("default", "Small-Concaved Pillar", columnPillar())
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> NETHERBRICK = registerType(
            new ChiselBlockType<BlockCarvable>("netherbrick")
                    .addVanillaBlock(Blocks.NETHER_BRICKS)
                    .addVanillaBlock(Blocks.CHISELED_NETHER_BRICKS)
                    .properties(() -> Blocks.NETHER_BRICKS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variation("a1_netherbrick_brinstar", "Blue")
                    .variation("a1_netherbrick_classicspatter", "Blood-Spattered")
                    .variation("a1_netherbrick_guts", "Meat Bricks")
                    .variation("a1_netherbrick_gutsdark", "Dark Meat Bricks")
                    .variation("a1_netherbrick_gutssmall", "Small Meat Bricks")
                    .variation("a1_netherbrick_lavabrinstar", "Blue Lava Brick")
                    .variation("a1_netherbrick_lavabrown", "Brown Lava Brick")
                    .variation("a1_netherbrick_lavaobsidian", "Dark Lava Brick")
                    .variation("a1_netherbrick_lavastonedark", "Stone Lava Brick")
                    .variation("a1_netherbrick_meat", "Nether Brick made of Meat")
                    .variation("a1_netherbrick_meatred", "Red Nether Brick made of Meat")
                    .variation("a1_netherbrick_meatredsmall", "Small Nether Brick made of Meat")
                    .variation("a1_netherbrick_meatsmall", "Small Red Nether Brick made of Meat")
                    .variation("a1_netherbrick_red", "Red Nether Brick")
                    .variation("a1_netherbrick_redsmall", "Small Red Nether Brick")
                    .variation("netherfancybricks", "Disordered Nether Bricks")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> NETHERRACK = registerType(
            new ChiselBlockType<BlockCarvable>("netherrack")
                    .addVanillaBlock(Blocks.NETHERRACK)
                    .properties(() -> Blocks.NETHERRACK)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variation("a1_netherrack_bloodgravel", "Streaked")
                    .variation("a1_netherrack_bloodrock", "Red Rock")
                    .variation("a1_netherrack_bloodrockgrey", "Gray Rock")
                    .variation("a1_netherrack_brinstar", "Blue Streaked")
                    .variation("a1_netherrack_brinstarshale", "Blue Shale")
                    .variation("a1_netherrack_classic", "Rocky")
                    .variation("a1_netherrack_classicspatter", "Blood-Splatted Rocky")
                    .variation("a1_netherrack_guts", "Raw Guts")
                    .variation("a1_netherrack_gutsdark", "Guts")
                    .variation("a1_netherrack_meat", "Meat")
                    .variation("a1_netherrack_meatred", "Raw Meat")
                    .variation("a1_netherrack_meatrock", "Bloody Rock")
                    .variation("a1_netherrack_red", "Dark Red")
                    .variation("a1_netherrack_wells", "Bloody Blue")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> OBSIDIAN = registerType(
            new ChiselBlockType<BlockCarvable>("obsidian")
                    .addVanillaBlock(Blocks.OBSIDIAN)
                    .properties(BlockBehaviour.Properties.of()
                            .strength(50.0f, 2000.0f)
                            .sound(SoundType.STONE)
                            .requiresCorrectToolForDrops())
                    .tag(BlockTags.DRAGON_IMMUNE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_DIAMOND_TOOL)
                    .variation("pillar", "Large Pillar", cubeColumn())
                    .variation("pillar-quartz", "Pillar", cubeColumn())
                    .variation("chiseled", "Chiseled", cubeColumn())
                    .variation("panel_shiny", "Shiny Panel")
                    .variation("panel", "Panel")
                    .variation("chunks", "Chunks")
                    .variation("growth", "Organic Chunks")
                    .variation("crystal", "Shiny")
                    .variation("map-a", "Map (Eastern)")
                    .variation("map-b", "Map (Western)")
                    .variation("panel_light", "Light Panel")
                    .variation("blocks", "Medium Tiles")
                    .variation("tiles", "Small Tiles")
                    .variation("greek", "Greek", cubeColumn())
                    .variation("crate", "Crate", cubeBottomTop())
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> PAPER = registerType(
            new ChiselBlockType<BlockCarvable>("paper")
                    .properties(BlockBehaviour.Properties.of()
                            .strength(1.5f, 0.0f)
                            .sound(SoundType.GRASS))
                    .variation("box", "Box")
                    .variation("throughmiddle", "Horizontally Striked")
                    .variation("cross", "Crossed")
                    .variation("sixsections", "Six-Pack")
                    .variation("vertical", "Vertical")
                    .variation("horizontal", "Horizontal")
                    .variation("floral", "Floral")
                    .variation("plain", "Plain")
                    .variation("door", "Door")
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> SANDSTONE_SCRIBBLES = registerType(
            new ChiselBlockType<BlockCarvable>("sandstone_scribbles")
                    .groupName("Sandstone Scribbles")
                    .properties(() -> Blocks.SANDSTONE)
                    .mapColor(MapColor.COLOR_YELLOW)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.SCRIBBLES)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> RED_SANDSTONE_SCRIBBLES = registerType(
            new ChiselBlockType<BlockCarvable>("sandstonered_scribbles")
                    .groupName("Red Sandstone Scribbles")
                    .properties(() -> Blocks.RED_SANDSTONE)
                    .mapColor(MapColor.COLOR_ORANGE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.SCRIBBLES)
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> TEMPLE = registerType(
            new ChiselBlockType<BlockCarvable>("temple")
                    .properties(BlockBehaviour.Properties.of()
                            .strength(1.5f, 10.0f)
                            .sound(SoundType.STONE))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation("cobble", "Cobble")
                    .variation("ornate", "Ornate")
                    .variation("plate", "Plate")
                    .variation("plate_cracked", "Cracked Plate")
                    .variation("bricks", "Bricks")
                    .variation("large_bricks", "Large Bricks")
                    .variation("weared_bricks", "Damaged Bricks")
                    .variation("disarray_bricks", "Trodden Bricks")
                    .variation("column", "Column", cubeColumn())
                    .variation("stand", "Stand", cubeColumn())
                    .variation("tiles", "Tiles")
                    .variation("smalltiles", "Small Tiles")
                    .variation("tiles_light", "Light Medium Tiles")
                    .variation("smalltiles_light", "Light Small Tiles")
                    .variation("stand_creeper", "Creeper Stand", cubeColumn())
                    .variation("stand_mosaic", "Mosaic Stand", cubeColumn())
                    .build()
    );

    public static final ChiselBlockType<BlockCarvable> TEMPLE_MOSSY = registerType(
            new ChiselBlockType<BlockCarvable>("templemossy")
                    .groupName("Temple Mossy")
                    .properties(BlockBehaviour.Properties.of()
                            .strength(1.5f, 10.0f)
                            .sound(SoundType.STONE))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation("cobble", "Cobble")
                    .variation("ornate", "Ornate")
                    .variation("plate", "Plate")
                    .variation("plate_cracked", "Cracked Plate")
                    .variation("bricks", "Bricks")
                    .variation("large_bricks", "Large Bricks")
                    .variation("weared_bricks", "Damaged Bricks")
                    .variation("disarray_bricks", "Trodden Bricks")
                    .variation("column", "Column", cubeColumn())
                    .variation("stand", "Stand", cubeColumn())
                    .variation("tiles", "Tiles")
                    .variation("smalltiles", "Small Tiles")
                    .variation("tiles_light", "Light Medium Tiles")
                    .variation("smalltiles_light", "Light Small Tiles")
                    .variation("stand_creeper", "Creeper Stand", cubeColumn())
                    .variation("stand_mosaic", "Mosaic Stand", cubeColumn())
                    .build()
    );

    // ==================== COLORED BLOCKS (CONCRETE, WOOL, etc.) ====================
    // These are generated dynamically for all 16 colors

    public static final Map<DyeColor, ChiselBlockType<BlockCarvable>> CONCRETE = createColoredBlocks("concrete",
            color -> new ChiselBlockType<BlockCarvable>("concrete/" + color.getSerializedName())
                    .groupName(toEnglishName(color.getSerializedName()) + " Concrete")
                    .addVanillaBlock(getConcreteBlock(color))
                    .properties(() -> getConcreteBlock(color))
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .variations(VariantTemplates.ROCK)
                    .build()
    );

    public static final Map<DyeColor, ChiselBlockType<BlockCarvable>> WOOL = createColoredBlocks("wool",
            color -> new ChiselBlockType<BlockCarvable>("wool/" + color.getSerializedName())
                    .groupName(toEnglishName(color.getSerializedName()) + " Wool")
                    .addVanillaBlock(getWoolBlock(color))
                    .properties(() -> getWoolBlock(color))
                    .tag(BlockTags.WOOL)
                    .variation("legacy", "Legacy")
                    .variation("llama", "Llama")
                    .build()
    );

    public static final Map<DyeColor, ChiselBlockType<BlockCarvable>> HEX_PLATING = createColoredBlocks("hexplating",
            color -> new ChiselBlockType<BlockCarvable>("hexplating/" + color.getSerializedName())
                    .groupName(toEnglishName(color.getSerializedName()) + " Hex Plating")
                    .properties(() -> Blocks.STONE)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_STONE_TOOL)
                    .variation("hexbase", "Hex Base", hexPlate("hexbase"))
                    .variation("hexnew", "Hex New", hexPlate("hexbase"))
                    .build()
    );

    // CARPET removed - no textures exist in 1.18.2

    public static final Map<DyeColor, ChiselBlockType<BlockCarvable>> GLASS_STAINED = createColoredBlocks("glass_stained",
            color -> new ChiselBlockType<BlockCarvable>("glass_stained/" + color.getSerializedName())
                    .groupName(toEnglishName(color.getSerializedName()) + " Stained Glass")
                    .addVanillaBlock(getStainedGlassBlock(color))
                    .properties(BlockBehaviour.Properties.of()
                            .sound(SoundType.GLASS)
                            .noOcclusion()
                            .explosionResistance(0.3f))
                    .variation("panel", "Panel")
                    .variation("framed", "Framed")
                    // framed_fancy removed - only -ctm texture exists in 1.18.2
                    .variation("streaks", "Streaks")
                    .variation("rough", "Rough")
                    .variation("brick", "Brick")
                    .build()
    );

    public static final Map<DyeColor, ChiselBlockType<BlockCarvablePane>> GLASSPANE_DYED = createColoredBlocks("glasspanedyed",
            color -> new ChiselBlockType<BlockCarvablePane>("glasspanedyed/" + color.getSerializedName(),
                    (props, data) -> new BlockCarvablePane(props, data, "glasspanedyed/" + color.getSerializedName()))
                    .groupName(toEnglishName(color.getSerializedName()) + " Stained Glass Pane")
                    .addVanillaBlock(getStainedGlassPaneBlock(color))
                    .properties(BlockBehaviour.Properties.of()
                            .sound(SoundType.GLASS)
                            .noOcclusion()
                            .explosionResistance(0.3f))
                    .variation("bubble", "Bubble", paneBlockSideTop())
                    .variation("panel", "Panel", paneBlockSideTop())
                    .variation("panel_fancy", "Fancy Panel", paneBlockSideTop())
                    .variation("quad", "Quad", paneBlockSideTop())
                    .variation("quad_fancy", "Fancy Quad", paneBlockSideTop())
                    .variation("transparent", "Transparent", paneBlockSideTop())
                    .build()
    );

    // ==================== WOOD TYPES (PLANKS, BOOKSHELVES) ====================

    // Wood type names (vanilla woods from 1.18.2) - must be defined before createWoodBlocks is called
    // Only includes woods that have textures in the 1.18.2 asset pack
    private static final List<String> WOOD_TYPES = List.of(
            "oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "crimson"
    );

    public static final Map<String, ChiselBlockType<BlockCarvable>> PLANKS = createWoodBlocks("planks",
            woodName -> new ChiselBlockType<BlockCarvable>("planks/" + woodName)
                    .groupName(toEnglishName(woodName) + " Planks")
                    .addVanillaBlock(getPlanksBlock(woodName))
                    .properties(() -> getPlanksBlock(woodName))
                    .tag(BlockTags.PLANKS)
                    .tag(BlockTags.MINEABLE_WITH_AXE)
                    .variations(VariantTemplates.PLANKS)
                    .build()
    );

    // Bookshelf - single type (not per-wood), textures exist in bookshelf/ folder
    public static final ChiselBlockType<BlockCarvable> BOOKSHELF = registerType(
            new ChiselBlockType<BlockCarvable>("bookshelf")
                    .groupName("Bookshelf")
                    .addVanillaBlock(Blocks.BOOKSHELF)
                    .properties(() -> Blocks.BOOKSHELF)
                    .tag(BlockTags.MINEABLE_WITH_AXE)
                    .variations(VariantTemplates.BOOKSHELF)
                    .build()
    );

    // ==================== PANE BLOCKS (IRON BARS) ====================
    // Note: GLASSPANE_DYED removed - textures use -side/-top format for pane models
    // which requires special pane model generation not currently implemented

    public static final ChiselBlockType<BlockCarvable> IRONPANE = registerType(
            new ChiselBlockType<BlockCarvable>("ironpane")
                    .groupName("Iron Bars")
                    .addVanillaBlock(Blocks.IRON_BARS)
                    .properties(() -> Blocks.IRON_BARS)
                    .tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .tag(BlockTags.NEEDS_IRON_TOOL)
                    .variation("borderless", "Frameless")
                    .variation("borderless-topper", "Frameless Topper")
                    .variation("barbedwire", "Barbed Wire")
                    .variation("cage", "Thick Cage")
                    .variation("thickgrid", "Thick Grid")
                    .variation("thingrid", "Thin Grid")
                    .variation("ornatesteel", "Ornate")
                    .variation("bars", "Straight")
                    .variation("spikes", "Spikes")
                    .variation("classic", "Classic")
                    .variation("classicnew", "Classic Thin")
                    .variation("fence", "Fence")
                    .variation("modern", "Modern Fence")
                    .build()
    );

    // ==================== HELPER METHODS ====================

    private static <T extends Block & com.leclowndu93150.chisel.api.block.ICarvable> ChiselBlockType<T> registerType(ChiselBlockType<T> type) {
        ALL_BLOCK_TYPES.add(type);
        return type;
    }

    private static <T extends Block & com.leclowndu93150.chisel.api.block.ICarvable> Map<DyeColor, ChiselBlockType<T>> createColoredBlocks(
            String baseName,
            java.util.function.Function<DyeColor, ChiselBlockType<T>> factory) {
        Map<DyeColor, ChiselBlockType<T>> map = new EnumMap<>(DyeColor.class);
        for (DyeColor color : DyeColor.values()) {
            ChiselBlockType<T> type = factory.apply(color);
            map.put(color, type);
            ALL_BLOCK_TYPES.add(type);
        }
        return map;
    }

    private static Block getConcreteBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_CONCRETE;
            case ORANGE -> Blocks.ORANGE_CONCRETE;
            case MAGENTA -> Blocks.MAGENTA_CONCRETE;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_CONCRETE;
            case YELLOW -> Blocks.YELLOW_CONCRETE;
            case LIME -> Blocks.LIME_CONCRETE;
            case PINK -> Blocks.PINK_CONCRETE;
            case GRAY -> Blocks.GRAY_CONCRETE;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_CONCRETE;
            case CYAN -> Blocks.CYAN_CONCRETE;
            case PURPLE -> Blocks.PURPLE_CONCRETE;
            case BLUE -> Blocks.BLUE_CONCRETE;
            case BROWN -> Blocks.BROWN_CONCRETE;
            case GREEN -> Blocks.GREEN_CONCRETE;
            case RED -> Blocks.RED_CONCRETE;
            case BLACK -> Blocks.BLACK_CONCRETE;
        };
    }

    private static Block getWoolBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_WOOL;
            case ORANGE -> Blocks.ORANGE_WOOL;
            case MAGENTA -> Blocks.MAGENTA_WOOL;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_WOOL;
            case YELLOW -> Blocks.YELLOW_WOOL;
            case LIME -> Blocks.LIME_WOOL;
            case PINK -> Blocks.PINK_WOOL;
            case GRAY -> Blocks.GRAY_WOOL;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_WOOL;
            case CYAN -> Blocks.CYAN_WOOL;
            case PURPLE -> Blocks.PURPLE_WOOL;
            case BLUE -> Blocks.BLUE_WOOL;
            case BROWN -> Blocks.BROWN_WOOL;
            case GREEN -> Blocks.GREEN_WOOL;
            case RED -> Blocks.RED_WOOL;
            case BLACK -> Blocks.BLACK_WOOL;
        };
    }

    // getCarpetBlock removed - CARPET blocks removed (no textures in 1.18.2)

    private static Block getStainedGlassBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_STAINED_GLASS;
            case ORANGE -> Blocks.ORANGE_STAINED_GLASS;
            case MAGENTA -> Blocks.MAGENTA_STAINED_GLASS;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_STAINED_GLASS;
            case YELLOW -> Blocks.YELLOW_STAINED_GLASS;
            case LIME -> Blocks.LIME_STAINED_GLASS;
            case PINK -> Blocks.PINK_STAINED_GLASS;
            case GRAY -> Blocks.GRAY_STAINED_GLASS;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_STAINED_GLASS;
            case CYAN -> Blocks.CYAN_STAINED_GLASS;
            case PURPLE -> Blocks.PURPLE_STAINED_GLASS;
            case BLUE -> Blocks.BLUE_STAINED_GLASS;
            case BROWN -> Blocks.BROWN_STAINED_GLASS;
            case GREEN -> Blocks.GREEN_STAINED_GLASS;
            case RED -> Blocks.RED_STAINED_GLASS;
            case BLACK -> Blocks.BLACK_STAINED_GLASS;
        };
    }

    private static Block getStainedGlassPaneBlock(DyeColor color) {
        return switch (color) {
            case WHITE -> Blocks.WHITE_STAINED_GLASS_PANE;
            case ORANGE -> Blocks.ORANGE_STAINED_GLASS_PANE;
            case MAGENTA -> Blocks.MAGENTA_STAINED_GLASS_PANE;
            case LIGHT_BLUE -> Blocks.LIGHT_BLUE_STAINED_GLASS_PANE;
            case YELLOW -> Blocks.YELLOW_STAINED_GLASS_PANE;
            case LIME -> Blocks.LIME_STAINED_GLASS_PANE;
            case PINK -> Blocks.PINK_STAINED_GLASS_PANE;
            case GRAY -> Blocks.GRAY_STAINED_GLASS_PANE;
            case LIGHT_GRAY -> Blocks.LIGHT_GRAY_STAINED_GLASS_PANE;
            case CYAN -> Blocks.CYAN_STAINED_GLASS_PANE;
            case PURPLE -> Blocks.PURPLE_STAINED_GLASS_PANE;
            case BLUE -> Blocks.BLUE_STAINED_GLASS_PANE;
            case BROWN -> Blocks.BROWN_STAINED_GLASS_PANE;
            case GREEN -> Blocks.GREEN_STAINED_GLASS_PANE;
            case RED -> Blocks.RED_STAINED_GLASS_PANE;
            case BLACK -> Blocks.BLACK_STAINED_GLASS_PANE;
        };
    }

    private static <T extends Block & com.leclowndu93150.chisel.api.block.ICarvable> Map<String, ChiselBlockType<T>> createWoodBlocks(
            String baseName,
            java.util.function.Function<String, ChiselBlockType<T>> factory) {
        Map<String, ChiselBlockType<T>> map = new LinkedHashMap<>();
        for (String woodName : WOOD_TYPES) {
            ChiselBlockType<T> type = factory.apply(woodName);
            map.put(woodName, type);
            ALL_BLOCK_TYPES.add(type);
        }
        return map;
    }

    private static Block getPlanksBlock(String woodName) {
        return switch (woodName) {
            case "oak" -> Blocks.OAK_PLANKS;
            case "spruce" -> Blocks.SPRUCE_PLANKS;
            case "birch" -> Blocks.BIRCH_PLANKS;
            case "jungle" -> Blocks.JUNGLE_PLANKS;
            case "acacia" -> Blocks.ACACIA_PLANKS;
            case "dark_oak" -> Blocks.DARK_OAK_PLANKS;
            case "crimson" -> Blocks.CRIMSON_PLANKS;
            default -> Blocks.OAK_PLANKS;
        };
    }

    private static String toEnglishName(String name) {
        StringBuilder builder = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : name.toCharArray()) {
            if (c == '_') {
                builder.append(' ');
                capitalizeNext = true;
            } else {
                builder.append(capitalizeNext ? Character.toUpperCase(c) : c);
                capitalizeNext = false;
            }
        }
        return builder.toString();
    }

    /**
     * Get all registered blocks across all types.
     */
    public static List<DeferredBlock<?>> getAllBlocks() {
        return ALL_BLOCK_TYPES.stream()
                .flatMap(type -> type.getAllBlocks().stream())
                .collect(Collectors.toList());
    }

    // ==================== SPECIAL BLOCKS ====================

    public static final DeferredBlock<BlockAutoChisel> AUTO_CHISEL = ChiselRegistries.BLOCKS.register("auto_chisel",
            () -> new BlockAutoChisel(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(2.5F)
                    .sound(SoundType.METAL)
                    .requiresCorrectToolForDrops()
            )
    );

    /**
     * Called during mod initialization to trigger static initialization.
     */
    public static void init() {
    }
}
