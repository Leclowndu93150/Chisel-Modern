package com.leclowndu93150.chisel.data;

import com.leclowndu93150.chisel.api.block.VariationData;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.leclowndu93150.chisel.data.ChiselModelTemplates.*;

/**
 * Predefined variant template lists for common block types.
 * Ported from Chisel 1.18.2's VariantTemplates.java
 *
 * Each variant now stores the actual ModelTemplate function for datagen.
 */
public class VariantTemplates {

    // ==================== RAW VARIANT ====================
    public static final VariationData RAW = new VariationData("raw", "Raw", simpleBlock());

    // ==================== METAL VARIANTS ====================
    public static final List<VariationData> METAL = List.of(
            new VariationData("caution", "Caution", simpleBlock()),
            new VariationData("crate", "Shipping Crate", simpleBlock()),
            new VariationData("thermal", "Thermal", cubeBottomTop()),
            new VariationData("machine", "Machine", simpleBlock()).withTooltip("An Old Relic From The", "Land Of OneTeuFyv"),
            new VariationData("badgreggy", "Egregious", simpleBlock()),
            new VariationData("bolted", "Bolted", simpleBlock()),
            new VariationData("scaffold", "Scaffold", simpleBlock())
    );

    // ==================== METAL TERRAIN VARIANTS ====================
    public static final List<VariationData> METAL_TERRAIN = List.of(
            new VariationData("large_ingot", "Large Ingot", cubeBottomTop()),
            new VariationData("small_ingot", "Small Ingot", cubeBottomTop()),
            new VariationData("brick", "Brick", cubeBottomTop()),
            new VariationData("coin_heads", "Coin (Heads)", cubeBottomTop()),
            new VariationData("coin_tails", "Coin (Tails)", cubeBottomTop()),
            new VariationData("crate_dark", "Dark Crate", cubeBottomTop()),
            new VariationData("crate_light", "Light Crate", cubeBottomTop()),
            new VariationData("plates", "Plates", cubeBottomTop()),
            new VariationData("rivets", "Riveted Plates", cubeBottomTop()),
            new VariationData("space", "Purple Space", simpleBlock()),
            new VariationData("space_black", "Black Space", simpleBlock()),
            new VariationData("simple", "Simple", cubeBottomTop())
    );

    // ==================== STONE VARIANTS ====================
    public static final List<VariationData> STONE = List.of(
            new VariationData("cracked", "Cracked", simpleBlock()),
            new VariationData("solid_bricks", "Bricks", simpleBlock()),
            new VariationData("small_bricks", "Small Bricks", simpleBlock()),
            new VariationData("soft_bricks", "Weathered Bricks", simpleBlock()),
            new VariationData("cracked_bricks", "Cracked Bricks", simpleBlock()),
            new VariationData("triple_bricks", "Wide Bricks", simpleBlock()),
            new VariationData("encased_bricks", "Encased Bricks", simpleBlock()),
            new VariationData("array", "Arrayed Bricks", simpleBlock()),
            new VariationData("tiles_medium", "Tiles", simpleBlock()),
            new VariationData("tiles_large", "Big Tile", simpleBlock()),
            new VariationData("tiles_small", "Small Tiles", simpleBlock()),
            new VariationData("chaotic_medium", "Disordered Tiles", simpleBlock()),
            new VariationData("chaotic_small", "Small Disordered Tiles", simpleBlock()),
            new VariationData("braid", "Braid", simpleBlock()),
            new VariationData("dent", "Dent", simpleBlock()),
            new VariationData("french_1", "French 1", simpleBlock()),
            new VariationData("french_2", "French 2", simpleBlock()),
            new VariationData("jellybean", "Jellybean", simpleBlock()),
            new VariationData("layers", "Layers", simpleBlock()),
            new VariationData("mosaic", "Mosaic", simpleBlock()),
            new VariationData("ornate", "Ornate", simpleBlock()),
            new VariationData("panel", "Panel", simpleBlock()),
            new VariationData("road", "Road", simpleBlock()),
            new VariationData("slanted", "Slanted", simpleBlock()),
            new VariationData("circular", "Circular", simpleBlock()),
            new VariationData("pillar", "Pillar", cubeColumn()),
            new VariationData("twisted", "Twisted", cubeColumn()),
            new VariationData("prism", "Prism", simpleBlock())
    );

    // ==================== ROCK VARIANTS ====================
    public static final List<VariationData> ROCK = List.of(
            new VariationData("cracked", "Cracked", simpleBlock()),
            new VariationData("solid_bricks", "Bricks", simpleBlock()),
            new VariationData("small_bricks", "Small Bricks", simpleBlock()),
            new VariationData("soft_bricks", "Weathered Bricks", simpleBlock()),
            new VariationData("cracked_bricks", "Cracked Bricks", simpleBlock()),
            new VariationData("triple_bricks", "Wide Bricks", simpleBlock()),
            new VariationData("encased_bricks", "Encased Bricks", simpleBlock()),
            new VariationData("chaotic_bricks", "Trodden Bricks", simpleBlock()),
            new VariationData("array", "Arrayed Bricks", simpleBlock()),
            new VariationData("tiles_medium", "Tiles", simpleBlock()),
            new VariationData("tiles_large", "Big Tile", simpleBlock()),
            new VariationData("tiles_small", "Small Tiles", simpleBlock()),
            new VariationData("chaotic_medium", "Disordered Tiles", simpleBlock()),
            new VariationData("chaotic_small", "Small Disordered Tiles", simpleBlock()),
            new VariationData("braid", "Braid", simpleBlock()),
            new VariationData("dent", "Dent", simpleBlock()),
            new VariationData("french_1", "French 1", simpleBlock()),
            new VariationData("french_2", "French 2", simpleBlock()),
            new VariationData("jellybean", "Jellybean", simpleBlock()),
            new VariationData("layers", "Layers", simpleBlock()),
            new VariationData("mosaic", "Mosaic", simpleBlock()),
            new VariationData("ornate", "Ornate", simpleBlock()),
            new VariationData("panel", "Panel", simpleBlock()),
            new VariationData("road", "Road", simpleBlock()),
            new VariationData("slanted", "Slanted", simpleBlock()),
            new VariationData("zag", "Zag", simpleBlock()),
            new VariationData("circular", "Circular", simpleBlock()),
            new VariationData("circularct", "Circular", ctm("circular")).withTooltip("Has CTM"),
            new VariationData("weaver", "Celtic", simpleBlock()),
            new VariationData("pillar", "Pillar", cubeColumn()),
            new VariationData("twisted", "Twisted", cubeColumn()),
            new VariationData("prism", "Prism", simpleBlock()),
            new VariationData("cuts", "Cuts", simpleBlock())
    );

    // ==================== COBBLESTONE VARIANTS ====================
    public static final List<VariationData> COBBLESTONE;
    static {
        List<VariationData> cobble = new ArrayList<>(ROCK);
        cobble.add(new VariationData("extra/emboss", "Embossed", simpleBlock()));
        cobble.add(new VariationData("extra/indent", "Indent", simpleBlock()));
        cobble.add(new VariationData("extra/marker", "Marker", simpleBlock()));
        COBBLESTONE = List.copyOf(cobble);
    }

    // ==================== MOSSY COBBLESTONE VARIANTS ====================
    /**
     * Creates a mossy model template for a given base block and variant.
     * Uses overlay system: base cobblestone texture + moss overlay.
     */
    private static ModelTemplate mossyModel(String base, VariationData variant) {
        if (variant.name().equals("circularct")) {
            return mossyCtm(base, "circular");
        } else if (variant.name().equals("pillar") || variant.name().equals("twisted")) {
            return mossyColumn(base);
        } else {
            return mossy(base);
        }
    }

    public static final List<VariationData> COBBLESTONE_MOSSY;
    static {
        List<VariationData> mossy = new ArrayList<>();
        for (VariationData v : COBBLESTONE) {
            mossy.add(new VariationData(v.name(), v.localizedName(), mossyModel("cobblestone", v), v.tooltip(), v.textureOverride()));
        }
        COBBLESTONE_MOSSY = List.copyOf(mossy);
    }

    // ==================== PILLAR VARIANTS ====================
    public static final List<VariationData> PILLAR = List.of(
            new VariationData("plainplain", "Plain-Capped Plain Pillar", columnPillar()),
            new VariationData("plaingreek", "Greek-Capped Plain Pillar", columnPillar()),
            new VariationData("greekplain", "Plain-Capped Greek Pillar", columnPillar()),
            new VariationData("greekgreek", "Greek-Capped Greek Pillar", columnPillar()),
            new VariationData("convex_plain", "Convexed Pillar", cubeAll("-top")),
            new VariationData("carved", "Scribed Pillar", cubeColumn()),
            new VariationData("ornamental", "Ornamental Pillar", cubeColumn())
    );

    // ==================== PLANKS VARIANTS ====================
    public static final List<VariationData> PLANKS = List.of(
            new VariationData("large_planks", "Large Planks", simpleBlock()),
            new VariationData("crude_horizontal_planks", "Crude Horizontal Planks", simpleBlock()),
            new VariationData("vertical_planks", "Vertical Planks", simpleBlock()),
            new VariationData("crude_vertical_planks", "Crude Vertical Planks", simpleBlock()),
            new VariationData("encased_planks", "Encased Planks", simpleBlock()),
            new VariationData("encased_large_planks", "Encased Large Planks", simpleBlock()),
            new VariationData("braced_planks", "Braced Planks", cubeColumn("log_bordered", "log_bordered")),
            new VariationData("shipping_crate", "Shipping Crate", simpleBlock()),
            new VariationData("paneling", "Paneling", simpleBlock()),
            new VariationData("crude_paneling", "Crude Paneling", simpleBlock()),
            new VariationData("stacked", "Stacked", simpleBlock()),
            new VariationData("smooth", "Smooth", simpleBlock()),
            new VariationData("encased_smooth", "Encased Smooth", simpleBlock()),
            new VariationData("braid", "Braid", simpleBlock()),
            // log_cabin uses -ew and -ns textures, no separate -tb (top uses -ns)
            new VariationData("log_cabin", "Log Cabin", axisFacesNoTop())
    );

    // ==================== COLOR VARIANTS ====================
    public static final List<VariationData> COLORS = Arrays.stream(DyeColor.values())
            .map(color -> new VariationData(color.getSerializedName(), toTitleCase(color.getSerializedName()), simpleBlock()))
            .collect(Collectors.toUnmodifiableList());

    /**
     * Generate color variants with a specific model template.
     */
    public static List<VariationData> colors(ModelTemplate template) {
        return COLORS.stream()
                .map(v -> v.withModelTemplate(template))
                .collect(Collectors.toUnmodifiableList());
    }

    // ==================== WOOL/CARPET VARIANTS ====================
    public static final List<VariationData> WOOL_CARPET = List.of(
            new VariationData("legacy", "Legacy", simpleBlock()),
            new VariationData("llama", "Llama", simpleBlock())
    );

    // ==================== BOOKSHELF VARIANTS ====================
    public static final List<VariationData> BOOKSHELF = List.of(
            new VariationData("rainbow", "Rainbow", simpleBlock()),
            new VariationData("novice_necromancer", "Novice Necromancer", simpleBlock()),
            new VariationData("necromancer", "Necromancer", simpleBlock()),
            new VariationData("redtomes", "Red Tomes", simpleBlock()),
            new VariationData("abandoned", "Abandoned", simpleBlock()),
            new VariationData("hoarder", "Hoarder", simpleBlock()),
            new VariationData("brim", "Brim", simpleBlock()),
            new VariationData("historian", "Historian", simpleBlock()),
            new VariationData("cans", "Cans", simpleBlock()),
            new VariationData("papers", "Stacks Of Papers", simpleBlock())
    );

    // ==================== SCRIBBLES VARIANTS ====================
    public static final List<VariationData> SCRIBBLES = List.of(
            new VariationData("scribbles_0", "Hieroglyphs 1", cubeColumn("scribbles_0-side", "scribbles_0-top")),
            new VariationData("scribbles_1", "Hieroglyphs 2", cubeColumn("scribbles_1-side", "scribbles_0-top")),
            new VariationData("scribbles_2", "Hieroglyphs 3", cubeColumn("scribbles_2-side", "scribbles_0-top")),
            new VariationData("scribbles_3", "Skull 1", cubeColumn("scribbles_3-side", "scribbles_0-top")),
            new VariationData("scribbles_4", "Eye of Horus", cubeColumn("scribbles_4-side", "scribbles_0-top")),
            new VariationData("scribbles_5", "Bird", cubeColumn("scribbles_5-side", "scribbles_0-top")),
            new VariationData("scribbles_6", "Halo", cubeColumn("scribbles_6-side", "scribbles_0-top")),
            new VariationData("scribbles_7", "Hieroglyphs 4", cubeColumn("scribbles_7-side", "scribbles_0-top")),
            new VariationData("scribbles_8", "Man with Staff", cubeColumn("scribbles_8-side", "scribbles_0-top")),
            new VariationData("scribbles_9", "Waves", cubeColumn("scribbles_9-side", "scribbles_0-top")),
            new VariationData("scribbles_10", "Landscape 1", cubeColumn("scribbles_10-side", "scribbles_0-top")),
            new VariationData("scribbles_11", "Skull Landscape", cubeColumn("scribbles_11-side", "scribbles_0-top")),
            new VariationData("scribbles_12", "Pattern 1", cubeColumn("scribbles_12-side", "scribbles_0-top")),
            new VariationData("scribbles_13", "Pattern 2", cubeColumn("scribbles_13-side", "scribbles_0-top")),
            new VariationData("scribbles_14", "Hieroglyphs 5", cubeColumn("scribbles_14-side", "scribbles_0-top")),
            new VariationData("scribbles_15", "Hieroglyphs 6", cubeColumn("scribbles_15-side", "scribbles_0-top"))
    );

    // ==================== ANTIBLOCK VARIANTS ====================
    public static final List<VariationData> ANTIBLOCK = List.of(
            new VariationData("white", "White", simpleBlock()),
            new VariationData("orange", "Orange", simpleBlock()),
            new VariationData("magenta", "Magenta", simpleBlock()),
            new VariationData("light_blue", "Light Blue", simpleBlock()),
            new VariationData("yellow", "Yellow", simpleBlock()),
            new VariationData("lime", "Lime", simpleBlock()),
            new VariationData("pink", "Pink", simpleBlock()),
            new VariationData("gray", "Gray", simpleBlock()),
            new VariationData("light_gray", "Light Gray", simpleBlock()),
            new VariationData("cyan", "Cyan", simpleBlock()),
            new VariationData("purple", "Purple", simpleBlock()),
            new VariationData("blue", "Blue", simpleBlock()),
            new VariationData("brown", "Brown", simpleBlock()),
            new VariationData("green", "Green", simpleBlock()),
            new VariationData("red", "Red", simpleBlock()),
            new VariationData("black", "Black", simpleBlock())
    );

    // ==================== HEX PLATING VARIANTS (uses color tinting) ====================
    public static final List<VariationData> HEX_PLATING = List.of(
            new VariationData("hexbase", "Hex Base", hexPlate("hexbase")),
            new VariationData("hexnew", "Hex New", hexPlate("hexnew"))
    );

    // ==================== LAVASTONE VARIANTS ====================
    public static final List<VariationData> LAVASTONE = List.of(
            new VariationData("cracked", "Cracked", fluidCube("lava")),
            new VariationData("soft_bricks", "Weathered Bricks", fluidCube("lava")),
            new VariationData("triple_bricks", "Wide Bricks", fluidCube("lava")),
            new VariationData("encased_bricks", "Encased Bricks", fluidPassCube("lava")),
            new VariationData("braid", "Braid", fluidCube("lava")),
            new VariationData("array", "Arrayed Bricks", fluidPassCube("lava")),
            new VariationData("tiles_large", "Big Tile", fluidPassCube("lava")),
            new VariationData("tiles_small", "Small Tiles", fluidCube("lava")),
            new VariationData("chaotic_medium", "Disordered Tiles", fluidCube("lava")),
            new VariationData("chaotic_small", "Small Disordered Tiles", fluidCube("lava")),
            new VariationData("dent", "Dent", fluidPassCube("lava")),
            new VariationData("french_1", "French 1", fluidCube("lava")),
            new VariationData("french_2", "French 2", fluidCube("lava")),
            new VariationData("jellybean", "Jellybean", fluidPassCube("lava")),
            new VariationData("layers", "Layers", fluidCube("lava")),
            new VariationData("mosaic", "Mosaic", fluidPassCube("lava")),
            new VariationData("ornate", "Ornate", fluidCube("lava")),
            new VariationData("panel", "Panel", fluidCube("lava")),
            new VariationData("road", "Road", fluidCube("lava")),
            new VariationData("slanted", "Slanted", fluidPassCube("lava")),
            new VariationData("zag", "Zag", fluidCube("lava")),
            new VariationData("circularct", "Circular", fluidCubeCTM("lava", "circular")),
            new VariationData("weaver", "Celtic", fluidPassCube("lava")),
            new VariationData("solid_bricks", "Bricks", fluidCube("lava")),
            new VariationData("small_bricks", "Small Bricks", fluidCube("lava")),
            new VariationData("circular", "Circular", fluidCube("lava")),
            new VariationData("tiles_medium", "Tiles", fluidCube("lava")),
            new VariationData("pillar", "Pillar", fluidPassColumn("lava")),
            new VariationData("twisted", "Twisted", fluidPassColumn("lava")),
            new VariationData("prism", "Prism", fluidCube("lava")),
            new VariationData("chaotic_bricks", "Trodden Bricks", fluidPassCube("lava")),
            new VariationData("cuts", "Cuts", fluidPassCube("lava"))
    );

    // ==================== WATERSTONE VARIANTS ====================
    public static final List<VariationData> WATERSTONE = List.of(
            new VariationData("cracked", "Cracked", fluidCube("water")),
            new VariationData("soft_bricks", "Weathered Bricks", fluidCube("water")),
            new VariationData("triple_bricks", "Wide Bricks", fluidCube("water")),
            new VariationData("encased_bricks", "Encased Bricks", fluidPassCube("water")),
            new VariationData("braid", "Braid", fluidCube("water")),
            new VariationData("array", "Arrayed Bricks", fluidPassCube("water")),
            new VariationData("tiles_large", "Big Tile", fluidPassCube("water")),
            new VariationData("tiles_small", "Small Tiles", fluidCube("water")),
            new VariationData("chaotic_medium", "Disordered Tiles", fluidCube("water")),
            new VariationData("chaotic_small", "Small Disordered Tiles", fluidCube("water")),
            new VariationData("dent", "Dent", fluidPassCube("water")),
            new VariationData("french_1", "French 1", fluidCube("water")),
            new VariationData("french_2", "French 2", fluidCube("water")),
            new VariationData("jellybean", "Jellybean", fluidPassCube("water")),
            new VariationData("layers", "Layers", fluidCube("water")),
            new VariationData("mosaic", "Mosaic", fluidPassCube("water")),
            new VariationData("ornate", "Ornate", fluidCube("water")),
            new VariationData("panel", "Panel", fluidCube("water")),
            new VariationData("road", "Road", fluidCube("water")),
            new VariationData("slanted", "Slanted", fluidPassCube("water")),
            new VariationData("zag", "Zag", fluidCube("water")),
            new VariationData("circularct", "Circular", fluidCubeCTM("water", "circular")),
            new VariationData("weaver", "Celtic", fluidPassCube("water")),
            new VariationData("solid_bricks", "Bricks", fluidCube("water")),
            new VariationData("small_bricks", "Small Bricks", fluidCube("water")),
            new VariationData("circular", "Circular", fluidCube("water")),
            new VariationData("tiles_medium", "Tiles", fluidCube("water")),
            new VariationData("pillar", "Pillar", fluidPassColumn("water")),
            new VariationData("twisted", "Twisted", fluidPassColumn("water")),
            new VariationData("prism", "Prism", fluidCube("water")),
            new VariationData("chaotic_bricks", "Trodden Bricks", fluidPassCube("water")),
            new VariationData("cuts", "Cuts", fluidPassCube("water"))
    );

    // Helper method
    private static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (c == '_' || c == ' ') {
                result.append(' ');
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
