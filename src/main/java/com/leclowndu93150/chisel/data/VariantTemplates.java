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

    public static final VariationData RAW = new VariationData("raw", "Raw", simpleBlock());

    public static final List<VariationData> METAL = List.of(
            new VariationData("caution", "Caution", simpleBlock()),
            new VariationData("crate", "Shipping Crate", simpleBlock()),
            new VariationData("thermal", "Thermal", cubeBottomTop()),
            new VariationData("machine", "Machine", simpleBlock()).withTooltip("An Old Relic From The", "Land Of OneTeuFyv"),
            new VariationData("badgreggy", "Egregious", simpleBlock()),
            new VariationData("bolted", "Bolted", simpleBlock()),
            new VariationData("scaffold", "Scaffold", simpleBlock())
    );

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
            new VariationData("circularct", "Circular", ctm("circularct")).withTooltip("Has CTM"),
            new VariationData("weaver", "Celtic", simpleBlock()),
            new VariationData("pillar", "Pillar", cubeColumn()),
            new VariationData("twisted", "Twisted", cubeColumn()),
            new VariationData("prism", "Prism", simpleBlock()),
            new VariationData("cuts", "Cuts", simpleBlock())
    );

    public static final List<VariationData> COBBLESTONE;
    static {
        List<VariationData> cobble = new ArrayList<>(ROCK);
        cobble.add(new VariationData("extra/emboss", "Embossed", simpleBlock()));
        cobble.add(new VariationData("extra/indent", "Indent", simpleBlock()));
        cobble.add(new VariationData("extra/marker", "Marker", simpleBlock()));
        COBBLESTONE = List.copyOf(cobble);
    }

    private static ModelTemplate mossyModel(String base, VariationData variant) {
        if (variant.name().equals("circularct")) {
            return mossyCtm(base, "circularct");
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

    public static final List<VariationData> ICE_PILLAR = List.of(
            new VariationData("plainplain", "Plain-Capped Plain Pillar", cubeAll("-top")),
            new VariationData("plaingreek", "Greek-Capped Plain Pillar", cubeAll("-top")),
            new VariationData("greekplain", "Plain-Capped Greek Pillar", cubeAll("-top")),
            new VariationData("greekgreek", "Greek-Capped Greek Pillar", cubeAll("-top")),
            new VariationData("convexplain", "Convexed-Capped Plain Pillar", cubeAll("-top")),
            new VariationData("carved", "Scribed Pillar", cubeColumn()),
            new VariationData("ornamental", "Ornamental Pillar", cubeColumn())
    );

    public static final List<VariationData> MARBLE_PILLAR = List.of(
            new VariationData("pillar", "Large Pillar", cubeAll("-top")),
            new VariationData("default", "Small-Concaved Pillar", cubeAll("-top")),
            new VariationData("simple", "Simple Pillar", cubeAll("-top")),
            new VariationData("convex", "Convexed Pillar", cubeAll("-top")),
            new VariationData("rough", "Rough", cubeAll("-top")),
            new VariationData("greekdecor", "Decor-Capped Greek Pillar", cubeAll("-top")),
            new VariationData("greekgreek", "Greek-Capped Greek Pillar", cubeAll("-top")),
            new VariationData("greekplain", "Plain-Capped Greek Pillar", cubeAll("-top")),
            new VariationData("plaindecor", "Decor-Capped Plain Pillar", cubeAll("-top")),
            new VariationData("plaingreek", "Greek-Capped Plain Pillar", cubeAll("-top")),
            new VariationData("plainplain", "Plain-Capped Plain Pillar", cubeAll("-top")),
            new VariationData("widedecor", "Decor-Capped Wide Pillar", cubeAll("-top")),
            new VariationData("widegreek", "Greek-Capped Wide Pillar", cubeAll("-top")),
            new VariationData("wideplain", "Plain-Capped Wide Pillar", cubeAll("-top")),
            new VariationData("carved", "Scribed Pillar", cubeColumn()),
            new VariationData("ornamental", "Ornamental Pillar", cubeColumn())
    );

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
            new VariationData("log_cabin", "Log Cabin", axisFacesNoTop())
    );

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

    public static final List<VariationData> WOOL_CARPET = List.of(
            new VariationData("legacy", "Legacy", simpleBlock()),
            new VariationData("llama", "Llama", simpleBlock())
    );

    public static final List<VariationData> BOOKSHELF = List.of(
            new VariationData("rainbow", "Rainbow", bookshelf()),
            new VariationData("novice_necromancer", "Novice Necromancer", bookshelf()),
            new VariationData("necromancer", "Necromancer", bookshelf()),
            new VariationData("redtomes", "Red Tomes", bookshelf()),
            new VariationData("abandoned", "Abandoned", bookshelf()),
            new VariationData("hoarder", "Hoarder", bookshelf()),
            new VariationData("brim", "Brim", bookshelf()),
            new VariationData("historian", "Historian", bookshelf()),
            new VariationData("cans", "Cans", bookshelf()),
            new VariationData("papers", "Stacks Of Papers", bookshelf())
    );

    /**
     * Generate bookshelf variants for a specific wood type.
     */
    public static List<VariationData> bookshelfForWood(String woodType) {
        return List.of(
                new VariationData("rainbow", "Rainbow", bookshelf(woodType)),
                new VariationData("novice_necromancer", "Novice Necromancer", bookshelf(woodType)),
                new VariationData("necromancer", "Necromancer", bookshelf(woodType)),
                new VariationData("redtomes", "Red Tomes", bookshelf(woodType)),
                new VariationData("abandoned", "Abandoned", bookshelf(woodType)),
                new VariationData("hoarder", "Hoarder", bookshelf(woodType)),
                new VariationData("brim", "Brim", bookshelf(woodType)),
                new VariationData("historian", "Historian", bookshelf(woodType)),
                new VariationData("cans", "Cans", bookshelf(woodType)),
                new VariationData("papers", "Stacks Of Papers", bookshelf(woodType))
        );
    }

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

    public static final List<VariationData> ANTIBLOCK = List.of(
            new VariationData("white", "White", twoLayerWithTop("antiblock", false)),
            new VariationData("orange", "Orange", twoLayerWithTop("antiblock", false)),
            new VariationData("magenta", "Magenta", twoLayerWithTop("antiblock", false)),
            new VariationData("light_blue", "Light Blue", twoLayerWithTop("antiblock", false)),
            new VariationData("yellow", "Yellow", twoLayerWithTop("antiblock", false)),
            new VariationData("lime", "Lime", twoLayerWithTop("antiblock", false)),
            new VariationData("pink", "Pink", twoLayerWithTop("antiblock", false)),
            new VariationData("gray", "Gray", twoLayerWithTop("antiblock", false)),
            new VariationData("light_gray", "Light Gray", twoLayerWithTop("antiblock", false)),
            new VariationData("cyan", "Cyan", twoLayerWithTop("antiblock", false)),
            new VariationData("purple", "Purple", twoLayerWithTop("antiblock", false)),
            new VariationData("blue", "Blue", twoLayerWithTop("antiblock", false)),
            new VariationData("brown", "Brown", twoLayerWithTop("antiblock", false)),
            new VariationData("green", "Green", twoLayerWithTop("antiblock", false)),
            new VariationData("red", "Red", twoLayerWithTop("antiblock", false)),
            new VariationData("black", "Black", twoLayerWithTop("antiblock", false))
    );

    public static final List<VariationData> HEX_PLATING = List.of(
            new VariationData("hexbase", "Hex Base", hexPlate("hexbase")),
            new VariationData("hexnew", "Hex New", hexPlate("hexnew"))
    );

    public static final List<VariationData> CUBITS = List.of(
            new VariationData("1", "Cubit 1", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("2", "Cubit 2", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("3", "Cubit 3", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("4", "Cubit 4", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("5", "Cubit 5", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("6", "Cubit 6", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("7", "Cubit 7", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("8", "Cubit 8", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("9", "Cubit 9", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("10", "Cubit 10", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("11", "Cubit 11", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("12", "Cubit 12", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("13", "Cubit 13", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("14", "Cubit 14", simpleBlock()).withTooltip("Mobs cannot spawn on this block"),
            new VariationData("15", "Cubit 15", simpleBlock()).withTooltip("Mobs cannot spawn on this block")
    );

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
            new VariationData("circularct", "Circular", fluidCubeCTM("lava", "circularct")),
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
            new VariationData("circularct", "Circular", fluidCubeCTM("water", "circularct")),
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

    // Fantasy Block variants (1.7.10 port)
    public static final List<VariationData> FANTASY = List.of(
            new VariationData("brick", "Brick", cubeColumn("brick_side", "brick_top")),
            new VariationData("brick_faded", "Brick Faded", cubeColumn("brick_faded_side", "brick_faded_top")),
            new VariationData("brick_wear", "Brick Wear", cubeColumn("brick_wear_side", "brick_wear_top")),
            new VariationData("bricks", "Bricks", cubeColumn("bricks_side", "bricks_top")),
            new VariationData("decor", "Decor", cubeColumn("decor_side", "decor_top")),
            new VariationData("decor_block", "Decor Block", cubeColumn("decor_block_side", "decor_block_top")),
            new VariationData("pillar", "Pillar", cubeColumn("pillar_side", "pillar_top")),
            new VariationData("pillar_decorated", "Pillar Decorated", cubeColumn("pillar_decorated_side", "pillar_decorated_top")),
            new VariationData("gold_decor_1", "Gold Decor 1", cubeColumn("gold_decor_1_side", "gold_decor_1_top")),
            new VariationData("gold_decor_2", "Gold Decor 2", cubeColumn("gold_decor_2_side", "gold_decor_2_top")),
            new VariationData("gold_decor_3", "Gold Decor 3", cubeColumn("gold_decor_3_side", "gold_decor_3_top")),
            new VariationData("gold_decor_4", "Gold Decor 4", cubeColumn("gold_decor_4_side", "gold_decor_4_top")),
            new VariationData("plate", "Plate", cubeColumn("plate_side", "plate_top")),
            new VariationData("block", "Block", simpleBlock()).withTooltip("Has CTM"),
            new VariationData("bricks_chaotic", "Bricks Chaotic", simpleBlock()),
            new VariationData("bricks_wear", "Bricks Wear", cubeColumn("bricks_wear_side", "bricks_wear_top"))
    );

    // Warning Sign variants (1.7.10 port)
    public static final List<VariationData> WARNING = List.of(
            new VariationData("radiation", "Radiation", simpleBlock()),
            new VariationData("biohazard", "Biohazard", simpleBlock()),
            new VariationData("fire", "Fire", simpleBlock()),
            new VariationData("explosion", "Explosion", simpleBlock()),
            new VariationData("death", "Death", simpleBlock()),
            new VariationData("falling", "Falling", simpleBlock()),
            new VariationData("fall", "Fall", simpleBlock()),
            new VariationData("voltage", "Voltage", simpleBlock()),
            new VariationData("generic", "Generic", simpleBlock()),
            new VariationData("acid", "Acid", simpleBlock()),
            new VariationData("underconstruction", "Under Construction", simpleBlock()),
            new VariationData("sound", "Sound", simpleBlock()),
            new VariationData("noentry", "No Entry", simpleBlock()),
            new VariationData("cryogenic", "Cryogenic", simpleBlock()),
            new VariationData("oxygen", "Oxygen", simpleBlock())
    );

    // Holystone variants (1.7.10 port)
    public static final List<VariationData> HOLYSTONE = List.of(
            new VariationData("raw", "Holystone", simpleBlock()),
            new VariationData("smooth", "Smooth", simpleBlock()),
            new VariationData("love", "Love", simpleBlock()),
            new VariationData("chiseled", "Chiseled", cubeColumn("chiseled_side", "chiseled_top")),
            new VariationData("blocks", "Blocks", simpleBlock()),
            new VariationData("blocks_rough", "Blocks Rough", simpleBlock()),
            new VariationData("brick", "Brick", simpleBlock()),
            new VariationData("large_bricks", "Large Bricks", simpleBlock()),
            new VariationData("platform", "Platform", cubeBottomTop("platform_side", "platform_bottom", "platform_top")),
            new VariationData("platform_tiles", "Platform Tiles", cubeBottomTop("platform_tiles_side", "platform_tiles_bottom", "platform_tiles_top")),
            new VariationData("construction", "Construction", simpleBlock()),
            new VariationData("fancy_tiles", "Fancy Tiles", simpleBlock()),
            new VariationData("plate", "Plate", simpleBlock()),
            new VariationData("plate_rough", "Plate Rough", simpleBlock())
    );

    // Futura variants (1.7.10 port)
    public static final List<VariationData> FUTURA = List.of(
            new VariationData("screen_metallic", "Screen Metallic", simpleBlock()),
            new VariationData("screen_cyan", "Screen Cyan", simpleBlock()),
            new VariationData("controller", "Controller", simpleBlock()),
            new VariationData("wavy", "Wavy", simpleBlock()),
            new VariationData("controller_purple", "Controller Purple", simpleBlock()),
            new VariationData("uber_wavy", "Uber Wavy", simpleBlock())
    );

    public static final List<VariationData> GRIMSTONE = List.of(
            new VariationData("raw", "Grimstone", simpleBlock()),
            new VariationData("smooth", "Smooth", simpleBlock()),
            new VariationData("hate", "Hate", simpleBlock()),
            new VariationData("chiseled", "Chiseled", cubeColumn("chiseled_side", "chiseled_top")),
            new VariationData("blocks", "Blocks", simpleBlock()),
            new VariationData("blocks_rough", "Rough Blocks", simpleBlock()),
            new VariationData("brick", "Brick", simpleBlock()),
            new VariationData("large_bricks", "Large Bricks", simpleBlock()),
            new VariationData("platform", "Platform", cubeBottomTop("platform_side", "platform_bottom", "platform_top")),
            new VariationData("platform_tiles", "Platform Tiles", cubeBottomTop("platform_tiles_side", "platform_tiles_bottom", "platform_tiles_top")),
            new VariationData("construction", "Construction", simpleBlock()),
            new VariationData("fancy_tiles", "Fancy Tiles", simpleBlock()),
            new VariationData("plate", "Plate", simpleBlock()),
            new VariationData("plate_rough", "Rough Plate", simpleBlock()),
            new VariationData("flaky", "Flaky", simpleBlock())
    );

    public static final List<VariationData> SHINGLES = List.of(
            new VariationData("shingle_1", "Red Diagonal", simpleBlock()),
            new VariationData("shingle_2", "Red Small Bricks", simpleBlock()),
            new VariationData("shingle_3", "Red Large Bricks", simpleBlock()),
            new VariationData("shingle_4", "Black Small Bricks", simpleBlock()),
            new VariationData("shingle_5", "Black Diagonal", simpleBlock()),
            new VariationData("shingle_6", "Black Large Bricks", simpleBlock())
    );

    public static final List<VariationData> BIRDSTONE = List.of(
            new VariationData("smooth", "Smooth", simpleBlock()),
            new VariationData("rough", "Rough", simpleBlock()),
            new VariationData("smallbrick", "Small Brick", simpleBlock()),
            new VariationData("smalltile", "Small Tile", simpleBlock()),
            new VariationData("smallbroken", "Small Broken", simpleBlock()),
            new VariationData("fourtile", "Four Tile", simpleBlock()),
            new VariationData("marker", "Marker", simpleBlock()),
            new VariationData("emboss", "Emboss", simpleBlock()),
            new VariationData("dent", "Dent", simpleBlock()),
            new VariationData("darkpanel", "Dark Panel", simpleBlock()),
            new VariationData("french", "French", simpleBlock()),
            new VariationData("french2", "French Alt", simpleBlock()),
            new VariationData("french_creep", "French Creeper", simpleBlock()),
            new VariationData("smooth_creep", "Smooth Creeper", simpleBlock()),
            new VariationData("ornate1", "Ornate 1", simpleBlock()),
            new VariationData("ornate2", "Ornate 2", simpleBlock())
    );

    public static final List<VariationData> MILITARY_IMPERIAL = List.of(
            new VariationData("imperial_camo", "Camo", simpleBlock()),
            new VariationData("imperial_camo_secluded", "Secluded Camo", simpleBlock()),
            new VariationData("imperial_plate", "Plate", simpleBlock()),
            new VariationData("imperial_caution_white", "White Caution", simpleBlock()),
            new VariationData("imperial_caution_orange", "Orange Caution", simpleBlock())
    );

    public static final List<VariationData> MILITARY_REBEL = List.of(
            new VariationData("rebel_camo", "Camo", simpleBlock()),
            new VariationData("rebel_camo_secluded", "Secluded Camo", simpleBlock()),
            new VariationData("rebel_plate", "Plate", simpleBlock()),
            new VariationData("rebel_caution_white", "White Caution", simpleBlock()),
            new VariationData("rebel_caution_red", "Red Caution", simpleBlock())
    );

    public static final List<VariationData> MECHANICAL2 = List.of(
            new VariationData("gear", "Gear", simpleBlock()),
            new VariationData("hex", "Hex", simpleBlock()),
            new VariationData("vent_glow", "Glowing Vent", simpleBlock()),
            new VariationData("vent2", "Vent", simpleBlock())
    );

    public static final List<VariationData> LEAVES = List.of(
            new VariationData("dead", "Dead Leaves", simpleBlockCutout()),
            new VariationData("fancy", "Fancy Leaves", simpleBlockCutout()),
            new VariationData("pinkpetal", "Pink Petals", simpleBlockCutout()),
            new VariationData("red_roses", "Red Rose", simpleBlockCutout()),
            new VariationData("roses", "White Rose", simpleBlockCutout()),
            new VariationData("christmas_balls", "Christmas Balls", simpleBlockCutout()),
            new VariationData("christmas_lights", "Christmas Lights", simpleBlockCutout())
    );

    public static final List<VariationData> AMBER = List.of(
            new VariationData("amber", "Amber", cubeColumn("amber", "amber_top"))
    );

    public static final List<VariationData> THAUMIUM = List.of(
            new VariationData("raw", "Thaumium", simpleBlock()),
            new VariationData("ornate", "Ornate Pattern", simpleBlock()),
            new VariationData("totem", "Totem Faces", simpleBlock()),
            new VariationData("big_bricks", "Bricks", simpleBlock()),
            new VariationData("small", "Small Tiles", simpleBlock()),
            new VariationData("lattice", "Lattice", simpleBlock()),
            new VariationData("planks", "Bounded Planks", simpleBlock()),
            new VariationData("chunks", "Chunks", simpleBlock()),
            new VariationData("bevel", "Bevel", simpleBlock())
    );

    public static final List<VariationData> TALLOW = List.of(
            new VariationData("raw", "Tallow", cubeColumn("raw", "raw_top")),
            new VariationData("smooth", "Smooth Tallow", simpleBlock()),
            new VariationData("faces", "Ensouled Tallow", simpleBlock())
    );

    public static final List<VariationData> ARCANE = List.of(
            new VariationData("moon_engrave", "Engraved Moon", simpleBlock()),
            new VariationData("moon_glow", "Glowing Moon", simpleBlock()),
            new VariationData("arcane_tile", "Beveled Tile", simpleBlock()),
            new VariationData("runes", "Engraved Symbols", simpleBlock()),
            new VariationData("runes_glow", "Glowing Symbols", simpleBlock()),
            new VariationData("big_brick", "Bricks", simpleBlock()),
            new VariationData("conduit_glow", "Temporal Conduit", simpleBlock()),
            new VariationData("border_brain", "Braaainz", simpleBlock()),
            new VariationData("arcane_border", "Infernally Arcane", simpleBlock()),
            new VariationData("arcane_matrix", "Nasty Nazca Lines", simpleBlock()),
            new VariationData("thaumcraft_logo", "Fine Thaumaturge's Emblem", simpleBlock()),
            new VariationData("arcane_crack", "Cracked Rock leaking Eldritch Glow", simpleBlock()),
            new VariationData("single_rune", "Single Rune", simpleBlock())
    );

    public static final List<VariationData> BLOOD_RUNE = List.of(
            new VariationData("blankrune", "Blank Rune", simpleBlock()),
            new VariationData("bloodrunearranged", "Arranged Blood Rune", simpleBlock()),
            new VariationData("bloodrunebricks", "Blood Rune Bricks", cubeColumn("bloodrunebricks", "bloodrunebrickstop")),
            new VariationData("bloodrunecarved", "Carved Blood Rune", simpleBlock()),
            new VariationData("bloodrunecarvedradial", "Radial Carved Blood Rune", simpleBlock()),
            new VariationData("bloodruneclassicpanel", "Classic Panel Blood Rune", simpleBlock()),
            new VariationData("bloodrunetiles", "Blood Rune Tiles", simpleBlock())
    );

    public static final List<VariationData> BRICK_CUSTOM = List.of(
            new VariationData("large", "Large Bricks", simpleBlock()),
            new VariationData("mortarless", "Mortarless Bricks", simpleBlock()),
            new VariationData("varied", "Varied Bricks", simpleBlock()),
            new VariationData("aged", "Aged Bricks", simpleBlock()),
            new VariationData("yellow", "Yellow Bricks", simpleBlock())
    );

    public static final List<VariationData> VOIDSTONE_PILLAR = List.of(
            new VariationData("pillar", "Striped", columnPillar())
    );

    public static final List<VariationData> ROAD_LINE = List.of(
            new VariationData("white", "White Line", roadLine()),
            new VariationData("double_white", "Double White Line", roadLine()),
            new VariationData("yellow", "Yellow Line", roadLine()),
            new VariationData("double_yellow", "Double Yellow Line", roadLine())
    );

    public static final List<VariationData> MAGMA = List.of(
            new VariationData("raw", "Raw", fluidCubeLocal("lava")),
            new VariationData("cracked", "Cracked", fluidCubeLocal("lava")),
            new VariationData("solid_bricks", "Bricks", fluidCubeLocal("lava")),
            new VariationData("small_bricks", "Small Bricks", fluidCubeLocal("lava")),
            new VariationData("soft_bricks", "Weathered Bricks", fluidCubeLocal("lava")),
            new VariationData("cracked_bricks", "Cracked Bricks", fluidCubeLocal("lava")),
            new VariationData("triple_bricks", "Wide Bricks", fluidCubeLocal("lava")),
            new VariationData("encased_bricks", "Encased Bricks", fluidPassCubeLocal("lava")),
            new VariationData("chaotic_bricks", "Trodden Bricks", fluidPassCubeLocal("lava")),
            new VariationData("array", "Arrayed Bricks", fluidPassCubeLocal("lava")),
            new VariationData("tiles_medium", "Tiles", fluidCubeLocal("lava")),
            new VariationData("tiles_large", "Big Tile", fluidPassCubeLocal("lava")),
            new VariationData("tiles_small", "Small Tiles", fluidCubeLocal("lava")),
            new VariationData("chaotic_medium", "Disordered Tiles", fluidCubeLocal("lava")),
            new VariationData("chaotic_small", "Small Disordered Tiles", fluidCubeLocal("lava")),
            new VariationData("braid", "Braid", fluidCubeLocal("lava")),
            new VariationData("dent", "Dent", fluidPassCubeLocal("lava")),
            new VariationData("french_1", "French 1", fluidCubeLocal("lava")),
            new VariationData("french_2", "French 2", fluidCubeLocal("lava")),
            new VariationData("jellybean", "Jellybean", fluidPassCubeLocal("lava")),
            new VariationData("layers", "Layers", fluidCubeLocal("lava")),
            new VariationData("mosaic", "Mosaic", fluidPassCubeLocal("lava")),
            new VariationData("ornate", "Ornate", fluidCubeLocal("lava")),
            new VariationData("panel", "Panel", fluidCubeLocal("lava")),
            new VariationData("road", "Road", fluidCubeLocal("lava")),
            new VariationData("slanted", "Slanted", fluidPassCubeLocal("lava")),
            new VariationData("zag", "Zag", fluidCubeLocal("lava")),
            new VariationData("circular", "Circular", fluidCubeLocal("lava")),
            new VariationData("circularct", "Circular", fluidCubeCTMLocal("lava", "circularct")).withTooltip("Has CTM"),
            new VariationData("weaver", "Celtic", fluidPassCubeLocal("lava")),
            new VariationData("pillar", "Pillar", fluidPassColumnLocal("lava")),
            new VariationData("twisted", "Twisted", fluidPassColumnLocal("lava")),
            new VariationData("prism", "Prism", fluidCubeLocal("lava")),
            new VariationData("cuts", "Cuts", fluidPassCubeLocal("lava"))
    );

    public static final List<VariationData> PUMPKIN;
    static {
        List<VariationData> faces = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            faces.add(new VariationData("face_" + i, "Face " + i, carvedPumpkin()));
        }
        PUMPKIN = List.copyOf(faces);
    }

}
