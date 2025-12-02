package com.leclowndu93150.chisel.compat;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.init.ChiselRegistries;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Compatibility layer for migrating worlds from Chisel Reborn to Chisel Modern.
 * <p>
 * Block ID format differences:
 * <ul>
 *   <li>Chisel Reborn: {@code chisel:{variant}/{base}} (e.g., {@code chisel:array/andesite})</li>
 *   <li>Chisel Modern: {@code chisel:{base}/{variant}} (e.g., {@code chisel:andesite/array})</li>
 * </ul>
 * <p>
 * This class registers aliases so that old Chisel Reborn block IDs resolve to
 * the equivalent Chisel Modern blocks when loading saved worlds.
 * <p>
 * Variant name differences between mods:
 * <ul>
 *   <li>{@code layer} → {@code layers}</li>
 *   <li>{@code slant} → {@code slanted}</li>
 *   <li>{@code twist} → {@code twisted}</li>
 *   <li>{@code large_tile} → {@code tiles_large}</li>
 *   <li>{@code french} → {@code french_1}</li>
 *   <li>{@code cut} → {@code cuts}</li>
 * </ul>
 */
public class ChiselRebornCompat {

    private static final Logger LOGGER = LogUtils.getLogger();

    private static final Set<String> registeredAliases = new HashSet<>();

    private static final String CHIPPED_INTEGRATION_MODID = "chisel_chipped_integration";

    private static final String[] COLORS = {
        "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink",
        "gray", "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    };

    private static final String[] WOOD_TYPES = {
        "oak", "spruce", "birch", "jungle", "acacia", "dark_oak", "crimson"
    };

    private static final String[] ROCK_VARIANTS_REBORN = {
        "array", "braid", "chaotic_bricks", "chaotic_medium", "chaotic_small",
        "circular", "cracked", "cracked_bricks", "cut", "dent", "encased_bricks",
        "french", "jellybean", "large_tile", "layer", "mosaic", "ornate", "panel",
        "pillar", "prism", "road", "slant", "small_bricks", "soft_bricks",
        "solid_bricks", "tiles_medium", "tiles_small", "triple_bricks", "twist",
        "weaver", "zag"
    };

    private static final String[] PLANK_VARIANTS_REBORN = {
        "braid", "crude_horizontal", "crude_paneling", "crude_vertical",
        "encased", "encased_large", "encased_smooth", "large", "log_bordered",
        "paneling", "shipping_crate", "smooth", "stacked", "vertical"
    };

    /**
     * Registers all Chisel Reborn to Chisel Modern block and item aliases.
     * Must be called BEFORE DeferredRegisters are registered to the event bus.
     */
    public static void registerAliases() {
        LOGGER.info("Registering Chisel Reborn compatibility aliases...");
        int count = 0;

        count += registerRawVariantAliases();
        count += registerRockBlockAliases();
        count += registerConcreteAliases();
        count += registerWoolAliases();
        count += registerPlankAliases();
        count += registerGoldAliases();
        count += registerIronAliases();
        count += registerDiamondAliases();
        count += registerEmeraldAliases();
        count += registerLapisAliases();
        count += registerRedstoneAliases();
        count += registerGlassAliases();
        count += registerGlowstoneAliases();
        count += registerDirtAliases();
        count += registerNetherbrickAliases();
        count += registerFallbackAliases();

        LOGGER.info("Registered {} Chisel Reborn compatibility aliases", count);

        // Chisel Chipped Integration mod aliases
        int chippedCount = registerChiselChippedIntegrationAliases();
        LOGGER.info("Registered {} Chisel Chipped Integration compatibility aliases", chippedCount);
    }

    private static String mapVariant(String rebornVariant) {
        return switch (rebornVariant) {
            case "layer" -> "layers";
            case "slant" -> "slanted";
            case "twist" -> "twisted";
            case "large_tile" -> "tiles_large";
            case "french" -> "french_1";
            case "cut" -> "cuts";
            default -> rebornVariant;
        };
    }

    private static String mapPlankVariant(String rebornVariant) {
        return switch (rebornVariant) {
            case "large" -> "large_planks";
            case "crude_horizontal" -> "crude_horizontal_planks";
            case "vertical" -> "vertical_planks";
            case "crude_vertical" -> "crude_vertical_planks";
            case "encased" -> "encased_planks";
            case "encased_large" -> "encased_large_planks";
            case "log_bordered" -> "braced_planks";
            default -> rebornVariant;
        };
    }

    private static int registerRawVariantAliases() {
        int count = 0;

        // chisel:raw/{block} -> minecraft:{block}
        count += registerAliasToVanilla("raw/andesite", "andesite") ? 1 : 0;
        count += registerAliasToVanilla("raw/granite", "granite") ? 1 : 0;
        count += registerAliasToVanilla("raw/diorite", "diorite") ? 1 : 0;
        count += registerAliasToVanilla("raw/cobblestone", "cobblestone") ? 1 : 0;
        count += registerAliasToVanilla("raw/stone", "stone") ? 1 : 0;
        count += registerAliasToVanilla("raw/bricks", "bricks") ? 1 : 0;
        count += registerAliasToVanilla("raw/sandstone", "sandstone") ? 1 : 0;
        count += registerAliasToVanilla("raw/red_sandstone", "red_sandstone") ? 1 : 0;
        count += registerAliasToVanilla("raw/end_stone", "end_stone") ? 1 : 0;
        count += registerAliasToVanilla("raw/prismarine", "prismarine") ? 1 : 0;
        count += registerAliasToVanilla("raw/ice", "ice") ? 1 : 0;
        count += registerAliasToVanilla("raw/coal_block", "coal_block") ? 1 : 0;
        count += registerAliasToVanilla("raw/deepslate", "deepslate") ? 1 : 0;
        count += registerAliasToVanilla("raw/blackstone", "blackstone") ? 1 : 0;
        count += registerAliasToVanilla("raw/purpur", "purpur_block") ? 1 : 0;
        count += registerAliasToVanilla("raw/quartz", "quartz_block") ? 1 : 0;

        for (String color : COLORS) {
            count += registerAliasToVanilla("raw/" + color + "_concrete", color + "_concrete") ? 1 : 0;
        }

        return count;
    }

    private static int registerRockBlockAliases() {
        int count = 0;

        String[] directMappingBases = {
            "andesite", "bricks", "cobblestone", "diorite", "end_stone",
            "granite", "prismarine", "sandstone", "red_sandstone", "ice"
        };

        for (String base : directMappingBases) {
            for (String rebornVariant : ROCK_VARIANTS_REBORN) {
                String modernVariant = mapVariant(rebornVariant);
                count += registerAlias(rebornVariant + "/" + base, base + "/" + modernVariant) ? 1 : 0;
            }
        }

        // stone -> stone_bricks
        for (String rebornVariant : ROCK_VARIANTS_REBORN) {
            String modernVariant = mapVariant(rebornVariant);
            count += registerAlias(rebornVariant + "/stone", "stone_bricks/" + modernVariant) ? 1 : 0;
        }
        count += registerAlias("largeornate/stone", "stone_bricks/extra/largeornate") ? 1 : 0;
        count += registerAlias("poison/stone", "stone_bricks/extra/poison") ? 1 : 0;
        count += registerAlias("sunken/stone", "stone_bricks/extra/sunken") ? 1 : 0;

        // quartz
        for (String rebornVariant : ROCK_VARIANTS_REBORN) {
            String modernVariant = mapVariant(rebornVariant);
            count += registerAlias(rebornVariant + "/quartz", "quartz/" + modernVariant) ? 1 : 0;
        }

        // purpur
        for (String rebornVariant : ROCK_VARIANTS_REBORN) {
            String modernVariant = mapVariant(rebornVariant);
            count += registerAlias(rebornVariant + "/purpur", "purpur/" + modernVariant) ? 1 : 0;
        }

        // coal_block -> coal
        for (String rebornVariant : ROCK_VARIANTS_REBORN) {
            String modernVariant = mapVariant(rebornVariant);
            count += registerAlias(rebornVariant + "/coal_block", "coal/" + modernVariant) ? 1 : 0;
        }
        count += registerAlias("large_bricks/coal_block", "coal/solid_bricks") ? 1 : 0;
        count += registerAlias("masonry/coal_block", "coal/solid_bricks") ? 1 : 0;

        // sandstone extras
        count += registerAlias("bevel_creeper/sandstone", "sandstone/extra/bevel_creeper") ? 1 : 0;
        count += registerAlias("glyphs/sandstone", "sandstone/extra/glyphs") ? 1 : 0;
        count += registerAlias("small/sandstone", "sandstone/extra/small") ? 1 : 0;

        // red_sandstone extras
        count += registerAlias("bevel_skeleton/red_sandstone", "red_sandstone/extra/bevel_skeleton") ? 1 : 0;
        count += registerAlias("glyphs/red_sandstone", "red_sandstone/extra/glyphs") ? 1 : 0;
        count += registerAlias("small/red_sandstone", "red_sandstone/extra/small") ? 1 : 0;

        return count;
    }

    private static int registerConcreteAliases() {
        int count = 0;

        for (String color : COLORS) {
            for (String rebornVariant : ROCK_VARIANTS_REBORN) {
                String modernVariant = mapVariant(rebornVariant);
                String rebornPath = rebornVariant + "/" + color + "_concrete";
                String modernPath = "concrete_" + color + "/" + modernVariant;
                count += registerAlias(rebornPath, modernPath) ? 1 : 0;
            }
        }

        return count;
    }

    private static int registerWoolAliases() {
        int count = 0;

        String[] woolVariants = {"legacy", "llama"};

        for (String color : COLORS) {
            for (String variant : woolVariants) {
                String rebornPath = variant + "/" + color + "_wool";
                String modernPath = "wool_" + color + "/" + variant;
                count += registerAlias(rebornPath, modernPath) ? 1 : 0;
            }
        }

        return count;
    }

    private static int registerPlankAliases() {
        int count = 0;

        for (String wood : WOOD_TYPES) {
            for (String rebornVariant : PLANK_VARIANTS_REBORN) {
                String modernVariant = mapPlankVariant(rebornVariant);
                String rebornPath = rebornVariant + "/" + wood + "_planks";
                String modernPath = "planks_" + wood + "/" + modernVariant;
                count += registerAlias(rebornPath, modernPath) ? 1 : 0;
            }
        }

        return count;
    }

    private static int registerGoldAliases() {
        int count = 0;

        String[] metalVariants = {"caution", "crate", "thermal", "machine", "badgreggy", "bolted", "scaffold"};
        for (String v : metalVariants) {
            count += registerAlias(v + "/gold_block", "gold/" + v) ? 1 : 0;
        }

        count += registerAlias("largeingot/gold_block", "gold/large_ingot") ? 1 : 0;
        count += registerAlias("smallingot/gold_block", "gold/small_ingot") ? 1 : 0;
        count += registerAlias("brick/gold_block", "gold/brick") ? 1 : 0;
        count += registerAlias("coin_heads/gold_block", "gold/coin_heads") ? 1 : 0;
        count += registerAlias("coin_tails/gold_block", "gold/coin_tails") ? 1 : 0;
        count += registerAlias("crate_dark/gold_block", "gold/crate_dark") ? 1 : 0;
        count += registerAlias("crate_light/gold_block", "gold/crate_light") ? 1 : 0;
        count += registerAlias("plates/gold_block", "gold/plates") ? 1 : 0;
        count += registerAlias("rivets/gold_block", "gold/rivets") ? 1 : 0;
        count += registerAlias("space/gold_block", "gold/space") ? 1 : 0;
        count += registerAlias("spaceblack/gold_block", "gold/space_black") ? 1 : 0;
        count += registerAlias("simple/gold_block", "gold/simple") ? 1 : 0;
        count += registerAlias("cart/gold_block", "gold/cart") ? 1 : 0;
        count += registerAlias("star/gold_block", "gold/star") ? 1 : 0;

        return count;
    }

    private static int registerIronAliases() {
        int count = 0;

        String[] metalVariants = {"caution", "crate", "thermal", "machine", "badgreggy", "bolted", "scaffold"};
        for (String v : metalVariants) {
            count += registerAlias(v + "/iron_block", "iron/" + v) ? 1 : 0;
        }

        count += registerAlias("largeingot/iron_block", "iron/large_ingot") ? 1 : 0;
        count += registerAlias("smallingot/iron_block", "iron/small_ingot") ? 1 : 0;
        count += registerAlias("brick/iron_block", "iron/brick") ? 1 : 0;
        count += registerAlias("coin_heads/iron_block", "iron/coin_heads") ? 1 : 0;
        count += registerAlias("coin_tails/iron_block", "iron/coin_tails") ? 1 : 0;
        count += registerAlias("crate_dark/iron_block", "iron/crate_dark") ? 1 : 0;
        count += registerAlias("crate_light/iron_block", "iron/crate_light") ? 1 : 0;
        count += registerAlias("plates/iron_block", "iron/plates") ? 1 : 0;
        count += registerAlias("rivets/iron_block", "iron/rivets") ? 1 : 0;
        count += registerAlias("space/iron_block", "iron/space") ? 1 : 0;
        count += registerAlias("spaceblack/iron_block", "iron/space_black") ? 1 : 0;
        count += registerAlias("simple/iron_block", "iron/simple") ? 1 : 0;
        count += registerAlias("moon/iron_block", "iron/moon") ? 1 : 0;
        count += registerAlias("vents/iron_block", "iron/vents") ? 1 : 0;
        count += registerAlias("gears/iron_block", "iron/vents") ? 1 : 0;

        return count;
    }

    private static int registerDiamondAliases() {
        int count = 0;

        count += registerAlias("embossed/diamond_block", "diamond/terrain_diamond_embossed") ? 1 : 0;
        count += registerAlias("gem/diamond_block", "diamond/terrain_diamond_gem") ? 1 : 0;
        count += registerAlias("cells/diamond_block", "diamond/terrain_diamond_cells") ? 1 : 0;
        count += registerAlias("space/diamond_block", "diamond/terrain_diamond_space") ? 1 : 0;
        count += registerAlias("spaceblack/diamond_block", "diamond/terrain_diamond_spaceblack") ? 1 : 0;
        count += registerAlias("simple/diamond_block", "diamond/terrain_diamond_simple") ? 1 : 0;
        count += registerAlias("bismuth/diamond_block", "diamond/terrain_diamond_bismuth") ? 1 : 0;
        count += registerAlias("crushed/diamond_block", "diamond/terrain_diamond_crushed") ? 1 : 0;
        count += registerAlias("four/diamond_block", "diamond/terrain_diamond_four") ? 1 : 0;
        count += registerAlias("fourornate/diamond_block", "diamond/terrain_diamond_fourornate") ? 1 : 0;
        count += registerAlias("zelda/diamond_block", "diamond/terrain_diamond_zelda") ? 1 : 0;
        count += registerAlias("ornatelayer/diamond_block", "diamond/terrain_diamond_ornatelayer") ? 1 : 0;

        return count;
    }

    private static int registerEmeraldAliases() {
        int count = 0;

        count += registerAlias("panel/emerald_block", "emerald/panel") ? 1 : 0;
        count += registerAlias("panelclassic/emerald_block", "emerald/panelclassic") ? 1 : 0;
        count += registerAlias("smooth/emerald_block", "emerald/smooth") ? 1 : 0;
        count += registerAlias("chunky/emerald_block", "emerald/chunk") ? 1 : 0;
        count += registerAlias("goldborder/emerald_block", "emerald/goldborder") ? 1 : 0;
        count += registerAlias("zelda/emerald_block", "emerald/zelda") ? 1 : 0;
        count += registerAlias("cells/emerald_block", "emerald/cell") ? 1 : 0;
        count += registerAlias("bismuth/emerald_block", "emerald/cellbismuth") ? 1 : 0;
        count += registerAlias("four/emerald_block", "emerald/four") ? 1 : 0;
        count += registerAlias("fourornate/emerald_block", "emerald/fourornate") ? 1 : 0;
        count += registerAlias("ornate/emerald_block", "emerald/ornate") ? 1 : 0;
        count += registerAlias("masonry/emerald_block", "emerald/masonryemerald") ? 1 : 0;
        count += registerAlias("circular/emerald_block", "emerald/emeraldcircle") ? 1 : 0;
        count += registerAlias("prism/emerald_block", "emerald/emeraldprismatic") ? 1 : 0;

        return count;
    }

    private static int registerLapisAliases() {
        int count = 0;

        count += registerAlias("chunky/lapis_block", "lapis/terrain_lapisblock_chunky") ? 1 : 0;
        count += registerAlias("large_tile/lapis_block", "lapis/terrain_lapisblock_panel") ? 1 : 0;
        count += registerAlias("zelda/lapis_block", "lapis/terrain_lapisblock_zelda") ? 1 : 0;
        count += registerAlias("ornate/lapis_block", "lapis/terrain_lapisornate") ? 1 : 0;
        count += registerAlias("panel/lapis_block", "lapis/terrain_lapistile") ? 1 : 0;
        count += registerAlias("panelclassic/lapis_block", "lapis/a1_blocklapis_panel") ? 1 : 0;
        count += registerAlias("smooth/lapis_block", "lapis/a1_blocklapis_smooth") ? 1 : 0;
        count += registerAlias("ornatelayer/lapis_block", "lapis/a1_blocklapis_ornatelayer") ? 1 : 0;
        count += registerAlias("masonry/lapis_block", "lapis/masonrylapis") ? 1 : 0;

        return count;
    }

    private static int registerRedstoneAliases() {
        int count = 0;

        String[] stoneVariants = {
            "array", "braid", "chaotic_medium", "chaotic_small", "circular",
            "cracked", "cracked_bricks", "dent", "encased_bricks", "french",
            "jellybean", "large_tile", "layer", "mosaic", "ornate", "panel",
            "pillar", "prism", "road", "slant", "small_bricks", "soft_bricks",
            "solid_bricks", "tiles_medium", "tiles_small", "triple_bricks", "twist"
        };

        for (String rebornVariant : stoneVariants) {
            String modernVariant = mapVariant(rebornVariant);
            count += registerAlias(rebornVariant + "/redstone_block", "redstone/" + modernVariant) ? 1 : 0;
        }

        return count;
    }

    private static int registerGlassAliases() {
        int count = 0;

        count += registerAlias("bubble/glass", "glass/terrain_glassbubble") ? 1 : 0;
        count += registerAlias("japanese/glass", "glass/japanese") ? 1 : 0;
        count += registerAlias("dungeon/glass", "glass/terrain_glassdungeon") ? 1 : 0;
        count += registerAlias("light/glass", "glass/terrain_glasslight") ? 1 : 0;
        count += registerAlias("noborder/glass", "glass/terrain_glassnoborder") ? 1 : 0;
        count += registerAlias("ornatesteel/glass", "glass/terrain_glass_ornatesteel") ? 1 : 0;
        count += registerAlias("ornatesteel_old/glass", "glass/terrain_glass_ornatesteel") ? 1 : 0;
        count += registerAlias("screen/glass", "glass/terrain_glass_screen") ? 1 : 0;
        count += registerAlias("shale/glass", "glass/terrain_glassshale") ? 1 : 0;
        count += registerAlias("steelframe/glass", "glass/terrain_glass_steelframe") ? 1 : 0;
        count += registerAlias("stone/glass", "glass/terrain_glassstone") ? 1 : 0;
        count += registerAlias("streak/glass", "glass/terrain_glassstreak") ? 1 : 0;
        count += registerAlias("thick/glass", "glass/terrain_glass_thickgrid") ? 1 : 0;
        count += registerAlias("thin/glass", "glass/terrain_glass_thingrid") ? 1 : 0;
        count += registerAlias("ironfencemodern/glass", "glass/a1_glasswindow_ironfencemodern") ? 1 : 0;
        count += registerAlias("chrono/glass", "glass/chrono") ? 1 : 0;
        count += registerAlias("chinese/glass", "glass/chinese2") ? 1 : 0;
        count += registerAlias("chinese2/glass", "glass/chinese2") ? 1 : 0;
        count += registerAlias("japanese2/glass", "glass/japanese2") ? 1 : 0;

        return count;
    }

    private static int registerGlowstoneAliases() {
        int count = 0;

        String[] stoneVariants = {
            "array", "braid", "chaotic_medium", "chaotic_small", "circular",
            "cracked", "cracked_bricks", "dent", "encased_bricks", "french",
            "jellybean", "large_tile", "layer", "mosaic", "ornate", "panel",
            "pillar", "prism", "road", "slant", "small_bricks", "soft_bricks",
            "solid_bricks", "tiles_medium", "tiles_small", "triple_bricks", "twist"
        };

        for (String rebornVariant : stoneVariants) {
            String modernVariant = mapVariant(rebornVariant);
            count += registerAlias(rebornVariant + "/glowstone", "glowstone/" + modernVariant) ? 1 : 0;
        }

        count += registerAlias("bismuth/glowstone", "glowstone/extra/bismuth") ? 1 : 0;
        count += registerAlias("large_tile_bismuth/glowstone", "glowstone/extra/tiles_large_bismuth") ? 1 : 0;
        count += registerAlias("tiles_medium_bismuth/glowstone", "glowstone/extra/tiles_medium_bismuth") ? 1 : 0;
        count += registerAlias("neon/glowstone", "glowstone/extra/neon") ? 1 : 0;
        count += registerAlias("neon_panel/glowstone", "glowstone/extra/neon_panel") ? 1 : 0;

        return count;
    }

    private static int registerDirtAliases() {
        int count = 0;

        count += registerAlias("bricks/dirt", "dirt/bricks") ? 1 : 0;
        count += registerAlias("bricks2/dirt", "dirt/bricks2") ? 1 : 0;
        count += registerAlias("bricks3/dirt", "dirt/bricks3") ? 1 : 0;
        count += registerAlias("chunky/dirt", "dirt/chunky") ? 1 : 0;
        count += registerAlias("cobble/dirt", "dirt/cobble") ? 1 : 0;
        count += registerAlias("happy/dirt", "dirt/happy") ? 1 : 0;
        count += registerAlias("horizontal/dirt", "dirt/horizontal") ? 1 : 0;
        count += registerAlias("layer/dirt", "dirt/layers") ? 1 : 0;
        count += registerAlias("panel/dirt", "dirt/plate") ? 1 : 0;
        count += registerAlias("reinforced/dirt", "dirt/reinforceddirt") ? 1 : 0;
        count += registerAlias("reinforcedcobble/dirt", "dirt/reinforcedcobbledirt") ? 1 : 0;
        count += registerAlias("vertical/dirt", "dirt/vert") ? 1 : 0;

        return count;
    }

    private static int registerNetherbrickAliases() {
        int count = 0;

        count += registerAlias("pillar/nether_brick", "netherbrick/a1_netherbrick_brinstar") ? 1 : 0;
        count += registerAlias("mosaic/nether_brick", "netherbrick/netherfancybricks") ? 1 : 0;
        count += registerAlias("slant/nether_brick", "netherbrick/a1_netherbrick_red") ? 1 : 0;

        return count;
    }

    private static int registerFallbackAliases() {
        int count = 0;

        // deepslate -> basalt
        for (String rebornVariant : ROCK_VARIANTS_REBORN) {
            String modernVariant = mapVariant(rebornVariant);
            count += registerAlias(rebornVariant + "/deepslate", "basalt/" + modernVariant) ? 1 : 0;
        }
        count += registerAlias("largeornate/deepslate", "basalt/panel") ? 1 : 0;
        count += registerAlias("poison/deepslate", "basalt/panel") ? 1 : 0;
        count += registerAlias("sunken/deepslate", "basalt/panel") ? 1 : 0;

        // blackstone -> basalt
        count += registerAlias("pillar/blackstone", "basalt/pillar") ? 1 : 0;

        return count;
    }

    private static boolean registerAlias(String fromPath, String toPath) {
        if (registeredAliases.contains(fromPath)) {
            LOGGER.debug("Skipping duplicate alias: {} (already registered)", fromPath);
            return false;
        }

        registeredAliases.add(fromPath);

        ResourceLocation from = ResourceLocation.fromNamespaceAndPath(Chisel.MODID, fromPath);
        ResourceLocation to = ResourceLocation.fromNamespaceAndPath(Chisel.MODID, toPath);

        ChiselRegistries.BLOCKS.addAlias(from, to);
        ChiselRegistries.ITEMS.addAlias(from, to);
        return true;
    }

    private static boolean registerAliasToVanilla(String fromPath, String vanillaBlock) {
        if (registeredAliases.contains(fromPath)) {
            LOGGER.debug("Skipping duplicate alias: {} (already registered)", fromPath);
            return false;
        }

        registeredAliases.add(fromPath);

        ResourceLocation from = ResourceLocation.fromNamespaceAndPath(Chisel.MODID, fromPath);
        ResourceLocation to = ResourceLocation.withDefaultNamespace(vanillaBlock);

        ChiselRegistries.BLOCKS.addAlias(from, to);
        ChiselRegistries.ITEMS.addAlias(from, to);
        return true;
    }

    private static boolean registerCrossModAlias(String fromMod, String fromPath, String toPath) {
        String key = fromMod + ":" + fromPath;
        if (registeredAliases.contains(key)) {
            return false;
        }
        registeredAliases.add(key);

        ResourceLocation from = ResourceLocation.fromNamespaceAndPath(fromMod, fromPath);
        ResourceLocation to = ResourceLocation.fromNamespaceAndPath(Chisel.MODID, toPath);

        ChiselRegistries.BLOCKS.addAlias(from, to);
        ChiselRegistries.ITEMS.addAlias(from, to);
        return true;
    }

    private static boolean registerCrossModAliasToVanilla(String fromMod, String fromPath, String vanillaBlock) {
        String key = fromMod + ":" + fromPath;
        if (registeredAliases.contains(key)) {
            return false;
        }
        registeredAliases.add(key);

        ResourceLocation from = ResourceLocation.fromNamespaceAndPath(fromMod, fromPath);
        ResourceLocation to = ResourceLocation.withDefaultNamespace(vanillaBlock);

        ChiselRegistries.BLOCKS.addAlias(from, to);
        ChiselRegistries.ITEMS.addAlias(from, to);
        return true;
    }

    // ==================== Chisel Chipped Integration Aliases ====================

    private static int registerChiselChippedIntegrationAliases() {
        int count = 0;

        // Wool blocks - target format is wool_{color}/{variant}
        for (String color : COLORS) {
            count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "wool_legacy_" + color, "wool_" + color + "/legacy") ? 1 : 0;
            count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "wool_llama_" + color, "wool_" + color + "/llama") ? 1 : 0;
        }

        // Carpet blocks -> vanilla carpets
        for (String color : COLORS) {
            count += registerCrossModAliasToVanilla(CHIPPED_INTEGRATION_MODID, "carpet_legacy_" + color, color + "_carpet") ? 1 : 0;
            count += registerCrossModAliasToVanilla(CHIPPED_INTEGRATION_MODID, "carpet_llama_" + color, color + "_carpet") ? 1 : 0;
        }

        // Factory blocks
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_blue_circuits", "factory/tilemosaic") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_blue_framed_circuit", "factory/frameblue") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_blue_wireframe", "factory/wireframeblue") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_circuit", "factory/circuit") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_dotted_rusty_plate", "factory/dots") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_gold_framed_purple_plates", "factory/goldplating") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_gold_plated_circuit", "factory/goldplate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_grinder", "factory/grinder") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_ice_ice_ice", "factory/iceiceice") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_metalbox", "factory/metalbox") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_metal_column", "factory/column") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_old_vents", "factory/plating") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_orange_white_caution_stripes", "factory/hazardorange") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_purple_wireframe", "factory/wireframe") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_rusty_plate", "factory/rust2") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_segmented_rusty_plates", "factory/rustplates") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_slighly_rusty_plate", "factory/platex") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_vents", "factory/vent") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_very_rusty_plate", "factory/rust") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_wireframe", "factory/wireframewhite") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "factory_yellow_black_caution_stripes", "factory/hazard") ? 1 : 0;

        // Laboratory blocks
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_checkered_tiles", "laboratory/checkertile") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_console", "laboratory/infocon") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_dark_medium_tiles", "laboratory/floortile") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_dotted_panel", "laboratory/dottedpanel") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_fuzzy_screen", "laboratory/fuzzscreen") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_large_steel", "laboratory/largesteel") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_large_tiles", "laboratory/largetile") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_left_faced_arrows", "laboratory/directionleft") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_medium_tiles", "laboratory/smalltile") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_right_faced_arrows", "laboratory/directionright") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_roundel", "laboratory/roundel") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_screen", "laboratory/fuzzscreen") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_small_steel", "laboratory/smallsteel") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_tiles", "laboratory/floortile") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_vents", "laboratory/wallvents") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "laboratory_white_panel", "laboratory/wallpanel") ? 1 : 0;

        // Technical blocks
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_cables", "technical/cables") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_caution_framed_plates", "technical/cautiontape") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_engineering_pipes_0", "technical/engineering") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_engineering_pipes_1", "technical/engineering") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_engineering_pipes_2", "technical/engineering") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_engineering_pipes_3", "technical/engineering") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_exhaust_plating", "technical/exhaustplating") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_extremely_corroded_panels", "technical/weatheredgreenpanels") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_extremely_rusted_panels", "technical/weatheredorangepanels") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_fan", "technical/fanfast") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_gears", "technical/spinningstuffanim") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_glowing_vent", "technical/ventglowing") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_grate", "technical/grate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_industrial_relic", "technical/industrialrelic") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_insulation", "technical/insulationv2") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_large_pipes", "technical/pipeslarge") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_large_rusty_scaffold", "technical/scaffoldlarge") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_make_shift_plating", "technical/makeshiftpanels") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_malfunction_fan", "technical/malfunctionfan") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_massive_fan", "technical/massivefan") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_massive_hexagonal_plating", "technical/massivehexplating") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_megacell", "technical/megacell") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_old_vent", "technical/vent") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_pipes", "technical/piping") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_rusty_bolted_plates", "technical/rustyboltedplates") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_rusty_grate", "technical/graterusty") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_rusty_scaffold", "technical/scaffold") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_small_pipes", "technical/pipessmall") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_still_fan", "technical/fanstill") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_sturdy", "technical/sturdy") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_transparent_fan", "technical/fanfasttransparent") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_transparent_rusty_scaffold", "technical/scaffoldtransparent") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_transparent_still_fan", "technical/fanstilltransparent") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "technical_vent", "technical/vent") ? 1 : 0;

        // Tyrian blocks
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_black_scaled_plates", "tyrian/black") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_black_strips", "tyrian/black2") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_blue_plates", "tyrian/blueplating") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_dent", "tyrian/dent") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_diagonal_plates", "tyrian/diagonal") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_disordered_metal_bits", "tyrian/shining") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_disordered_purple_bits", "tyrian/chaotic") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_metal_plates", "tyrian/tyrian") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_opening", "tyrian/opening") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_platform", "tyrian/platform") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_purple_plates", "tyrian/softplate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_routes", "tyrian/routes") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_rust", "tyrian/rust") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_shiny_plate", "tyrian/plate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_shiny_plates", "tyrian/elaborate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "tyrian_small_uneven_tiles", "tyrian/platetiles") ? 1 : 0;

        // Metal blocks - iron and gold use "iron/{variant}" and "gold/{variant}"
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_iron_bolted", "iron/bolted") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_iron_caution", "iron/caution") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_iron_egregious", "iron/badgreggy") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_iron_industrial_relic", "iron/machine") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_iron_scaffold", "iron/scaffold") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_iron_shipping_crate", "iron/crate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_iron_thermal", "iron/thermal") ? 1 : 0;

        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_gold_bolted", "gold/bolted") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_gold_caution", "gold/caution") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_gold_egregious", "gold/badgreggy") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_gold_industrial_relic", "gold/machine") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_gold_scaffold", "gold/scaffold") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_gold_shipping_crate", "gold/crate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_gold_thermal", "gold/thermal") ? 1 : 0;

        // Other metals use "metals_{metal}/{variant}" format (metals/aluminum -> metals_aluminum)
        String[] otherMetals = {"aluminum", "bronze", "cobalt", "copper", "electrum", "invar", "lead", "nickel", "platinum", "silver", "steel", "tin", "uranium"};
        for (String metal : otherMetals) {
            count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_" + metal + "_bolted", "metals_" + metal + "/bolted") ? 1 : 0;
            count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_" + metal + "_caution", "metals_" + metal + "/caution") ? 1 : 0;
            count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_" + metal + "_egregious", "metals_" + metal + "/badgreggy") ? 1 : 0;
            count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_" + metal + "_industrial_relic", "metals_" + metal + "/machine") ? 1 : 0;
            count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_" + metal + "_scaffold", "metals_" + metal + "/scaffold") ? 1 : 0;
            count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_" + metal + "_shipping_crate", "metals_" + metal + "/crate") ? 1 : 0;
            count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "metal_" + metal + "_thermal", "metals_" + metal + "/thermal") ? 1 : 0;
        }

        // Futura blocks
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "futura_me_controller", "futura/controller") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "futura_gray_screen", "futura/screen_metallic") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "futura_cyan_screen", "futura/screen_cyan") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "futura_rainbowliciously_wavy", "futura/wavy") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "futura_purple_me_controller", "futura/controller_purple") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "futura_fabulously_wavy", "futura/uber_wavy") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "futura_me_controller_ae_2", "futura/controller") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "futura_mysterious_cube", "futura/controller") ? 1 : 0;

        // Diamond blocks
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_bismuth", "diamond/terrain_diamond_bismuth") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_cells", "diamond/terrain_diamond_cells") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_crushed", "diamond/terrain_diamond_crushed") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_embossed", "diamond/terrain_diamond_embossed") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_gem", "diamond/terrain_diamond_gem") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_gold_encrusted", "diamond/terrain_diamond_ornatelayer") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_ornate_tiles", "diamond/terrain_diamond_fourornate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_simple", "diamond/terrain_diamond_simple") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_purple_space", "diamond/terrain_diamond_space") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_space", "diamond/terrain_diamond_spaceblack") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_tiles", "diamond/terrain_diamond_four") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_diamond_zelda", "diamond/terrain_diamond_zelda") ? 1 : 0;

        // Emerald blocks
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_bismuth", "emerald/cellbismuth") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_cell", "emerald/cell") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_circular", "emerald/emeraldcircle") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_cobble", "emerald/chunk") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_gold_encrusted", "emerald/goldborder") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_masonry", "emerald/masonryemerald") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_ornate", "emerald/ornate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_ornate_tiles", "emerald/fourornate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_panel", "emerald/panelclassic") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_prism", "emerald/emeraldprismatic") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_smooth", "emerald/smooth") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_tile", "emerald/panel") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_tiles", "emerald/four") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_emerald_zelda", "emerald/zelda") ? 1 : 0;

        // Lapis blocks
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_lapis_cobble", "lapis/terrain_lapisblock_chunky") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_lapis_gold_encrusted", "lapis/a1_blocklapis_ornatelayer") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_lapis_masonry", "lapis/masonrylapis") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_lapis_panel", "lapis/terrain_lapistile") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_lapis_shiny_tile", "lapis/a1_blocklapis_panel") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_lapis_smooth", "lapis/a1_blocklapis_smooth") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_lapis_tile", "lapis/terrain_lapisblock_panel") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_lapis_wood_framed", "lapis/terrain_lapisornate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_lapis_zelda", "lapis/terrain_lapisblock_zelda") ? 1 : 0;

        // Redstone blocks
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_arrayed_bricks", "redstone/array") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_big_tile", "redstone/tiles_large") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_braid", "redstone/braid") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_bricks", "redstone/solid_bricks") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_circular", "redstone/circular") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_cracked", "redstone/cracked") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_cracked_bricks", "redstone/cracked_bricks") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_dent", "redstone/dent") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_disordered_tiles", "redstone/chaotic_medium") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_encased_bricks", "redstone/encased_bricks") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_french_1", "redstone/french_1") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_french_2", "redstone/french_2") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_jellybean", "redstone/jellybean") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_layers", "redstone/layers") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_mosaic", "redstone/mosaic") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_ornate", "redstone/ornate") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_panel", "redstone/panel") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_pillar", "redstone/pillar") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_prism", "redstone/prism") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_road", "redstone/road") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_slanted", "redstone/slanted") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_small_bricks", "redstone/small_bricks") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_small_disordered_tiles", "redstone/chaotic_small") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_small_tiles", "redstone/tiles_small") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_tiles", "redstone/tiles_medium") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_twisted", "redstone/twisted") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_weathered_bricks", "redstone/soft_bricks") ? 1 : 0;
        count += registerCrossModAlias(CHIPPED_INTEGRATION_MODID, "gem_redstone_wide_bricks", "redstone/triple_bricks") ? 1 : 0;

        return count;
    }
}
