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
}
