package com.leclowndu93150.chisel.data;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ICarvable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.function.Function;

/**
 * Model templates for generating block models.
 * Ported from Chisel 1.18.2's ModelTemplates.java
 */
public class ChiselModelTemplates {

    @FunctionalInterface
    public interface ModelTemplate {
        void apply(BlockStateProvider prov, Block block);
    }

    /**
     * Gets the registry name path (used for model file names).
     */
    public static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    /**
     * Gets the texture path for a block, using the original block type name
     * to preserve slashes that were converted to underscores in the registry name.
     * e.g., registry: "metals_aluminum/caution" -> texture: "metals/aluminum/caution"
     */
    public static String texturePath(Block block) {
        if (block instanceof ICarvable carvable) {
            String blockType = carvable.getBlockType();
            String variation = carvable.getVariation().getTextureName();
            return blockType + "/" + variation;
        }
        // Fallback for non-ICarvable blocks
        return name(block);
    }

    public static ModelTemplate simpleBlock() {
        return ChiselModelTemplates::simpleBlock;
    }

    private static void simpleBlock(BlockStateProvider prov, Block block) {
        prov.simpleBlock(block, prov.models().cubeAll("block/" + name(block), Chisel.id("block/" + texturePath(block))));
    }

    /**
     * Replaces the variant (last path component) in a texture path.
     */
    public static String replaceVariant(String texPath, String newVariant) {
        int lastSlash = texPath.lastIndexOf('/');
        if (lastSlash >= 0) {
            return texPath.substring(0, lastSlash + 1) + newVariant;
        }
        return newVariant;
    }

    /**
     * Replaces the block type (first path component after "block/") in a texture path.
     */
    public static String replaceBlock(String texPath, String newBlock) {
        return texPath.replaceAll("^block/[^/]+", "block/" + newBlock);
    }

    public static ModelTemplate cubeBottomTop() {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().cubeBottomTop(modelName,
                    Chisel.id(texPath + "-side"),
                    Chisel.id(texPath + "-bottom"),
                    Chisel.id(texPath + "-top")));
        };
    }

    public static ModelTemplate cubeBottomTop(String side, String bottom, String top) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().cubeBottomTop(modelName,
                    Chisel.id(replaceVariant(texPath, side)),
                    Chisel.id(replaceVariant(texPath, bottom)),
                    Chisel.id(replaceVariant(texPath, top))));
        };
    }

    public static ModelTemplate cubeAll(String postfix) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().cubeAll(modelName, Chisel.id(texPath + postfix)));
        };
    }

    public static ModelTemplate cubeColumn() {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().cubeColumn(modelName,
                    Chisel.id(texPath + "-side"),
                    Chisel.id(texPath + "-top")));
        };
    }

    public static ModelTemplate cubeColumn(String side, String top) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().cubeColumn(modelName,
                    Chisel.id(replaceVariant(texPath, side)),
                    Chisel.id(replaceVariant(texPath, top))));
        };
    }

    public static ModelTemplate ctm(String variant) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            String texName = replaceVariant(texPath, variant);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("cube_ctm"))
                    .texture("all", texName)
                    .texture("connected_tex", texName + "-ctm"));
        };
    }

    public static ModelTemplate twoLayerWithTop(String top, boolean shade) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id(shade ? "cube_2_layer" : "cube_2_layer_no_shade"))
                    .texture("bot", texPath)
                    .texture("top", replaceVariant(texPath, top)));
        };
    }

    public static ModelTemplate axisFaces() {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/cube_axis"))
                    .texture("x", texPath + "-ew")
                    .texture("y", texPath + "-tb")
                    .texture("z", texPath + "-ns"));
        };
    }

    /**
     * Axis faces without separate top/bottom texture - uses -ns for top.
     */
    public static ModelTemplate axisFacesNoTop() {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/cube_axis"))
                    .texture("x", texPath + "-ew")
                    .texture("y", texPath + "-ns")  // Use -ns for top/bottom
                    .texture("z", texPath + "-ns"));
        };
    }

    public static ModelTemplate mossy(String base) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            String texName = replaceBlock(texPath, base);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/mossy/mossy"))
                    .texture("bot", texName));
        };
    }

    public static ModelTemplate mossyColumn(String base) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            String texName = replaceBlock(texPath, base);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/mossy/mossy_column"))
                    .texture("side", texName + "-side")
                    .texture("end", texName + "-top"));
        };
    }

    public static ModelTemplate mossyCtm(String base, String variant) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            String texName = replaceBlock(texPath, base);
            texName = replaceVariant(texName, variant);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/mossy/mossy_ctm"))
                    .texture("bot", texName)
                    .texture("connect_bot", texName + "-ctm"));
        };
    }

    public static ModelTemplate twoLayerTopShaded(String top, String bottom) {
        return twoLayerTopShaded(top, top, bottom);
    }

    public static ModelTemplate twoLayerTopShaded(String particle, String top, String bottom) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/cube_2_layer_topshaded"))
                    .texture("particle", replaceVariant(texPath, particle))
                    .texture("top", replaceVariant(texPath, top))
                    .texture("bot", replaceVariant(texPath, bottom)));
        };
    }

    public static ModelTemplate threeLayerTopShaded(String particle, String top, String mid, String bottom) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/cube_3_layer_topshaded"))
                    .texture("particle", replaceVariant(texPath, particle))
                    .texture("top", replaceVariant(texPath, top))
                    .texture("mid", replaceVariant(texPath, mid))
                    .texture("bot", replaceVariant(texPath, bottom)));
        };
    }

    public static ModelTemplate cubeCTMTranslucent(String all, String ctm) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/cube_ctm_translucent"))
                    .texture("all", all)
                    .texture("connected_tex", ctm));
        };
    }

    public static ModelTemplate fluidCube(String fluid) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String variant = getVariantName(block);

            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/" + fluid + "stone/cube_" + fluid))
                    .texture("bot", ResourceLocation.withDefaultNamespace("block/" + fluid + "_still"))
                    .texture("top", Chisel.id("block/fluid/" + variant)));
        };
    }

    public static ModelTemplate fluidCubeCTM(String fluid, String variant) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);

            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/" + fluid + "stone/cube_ctm_" + fluid))
                    .texture("bot", ResourceLocation.withDefaultNamespace("block/" + fluid + "_still"))
                    .texture("top", Chisel.id("block/fluid/" + variant))
                    .texture("connect_top", Chisel.id("block/fluid/" + variant + "-ctm")));
        };
    }

    public static ModelTemplate fluidPassCube(String fluid) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String variant = getVariantName(block);

            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/" + fluid + "stone/cube_pass_" + fluid))
                    .texture("bot", ResourceLocation.withDefaultNamespace("block/" + fluid + "_still"))
                    .texture("top", Chisel.id("block/fluid/" + variant)));
        };
    }

    public static ModelTemplate fluidPassColumn(String fluid) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String variant = getVariantName(block);

            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/" + fluid + "stone/column_" + fluid))
                    .texture("bot", ResourceLocation.withDefaultNamespace("block/" + fluid + "_still"))
                    .texture("side", Chisel.id("block/fluid/" + variant + "-side"))
                    .texture("top", Chisel.id("block/fluid/" + variant + "-top")));
        };
    }

    /**
     * Gets the variant name from a block (for fluid textures).
     */
    private static String getVariantName(Block block) {
        if (block instanceof ICarvable carvable) {
            return carvable.getVariation().getTextureName();
        }
        String name = name(block);
        return name.contains("/") ? name.split("/")[1] : name;
    }

    public static ModelTemplate cubeEldritch() {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/cube_eldritch"))
                    .texture("all", texPath));
        };
    }

    public static ModelTemplate columnEldritch(String top) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/column_eldritch"))
                    .texture("end", replaceVariant(texPath, top) + "-top")
                    .texture("side", texPath + "-side"));
        };
    }

    public static ModelTemplate columnPillar() {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            String texPath = "block/" + texturePath(block);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/column_pillar"))
                    .texture("top", texPath + "-top")
                    .texture("pillar", texPath + "-ctmv"));
        };
    }

    public static ModelTemplate hexPlate(String variant) {
        return (prov, block) -> {
            String modelName = "block/" + name(block);
            prov.simpleBlock(block, prov.models().withExistingParent(modelName, Chisel.id("block/cube_2_layer"))
                    .texture("top", Chisel.id("block/hexplating/" + variant))
                    .texture("bot", Chisel.id("block/animations/archetype2")));
        };
    }

    public static ModelTemplate cube(String texture) {
        return (prov, block) -> {
            prov.simpleBlock(block, prov.models().cubeAll("block/" + name(block), Chisel.id(texture)));
        };
    }

    // Pane block model - simplified version for glass panes
    public static ModelTemplate paneBlock(String edge) {
        return (prov, block) -> {
            String name = "block/" + name(block);
            String texture = "block/" + name(block).replace("pane", "");
            paneBlockInternal(prov, block, name, Chisel.id(texture), Chisel.id(edge));
        };
    }

    private static void paneBlockInternal(BlockStateProvider prov, Block block, String name, ResourceLocation texture, ResourceLocation edge) {
        MultiPartBlockStateBuilder builder = prov.getMultipartBuilder(block);

        builder.part().modelFile(prov.models().withExistingParent(name + "_post", Chisel.id("block/pane/post"))
                        .texture("pane", texture)
                        .texture("edge", edge))
                .addModel()
                .condition(CrossCollisionBlock.NORTH, false)
                .condition(CrossCollisionBlock.EAST, false)
                .condition(CrossCollisionBlock.SOUTH, false)
                .condition(CrossCollisionBlock.WEST, false)
                .end();

        // North
        builder.part().modelFile(prov.models().withExistingParent(name + "_n", Chisel.id("block/pane/n"))
                        .texture("pane", texture)
                        .texture("edge", edge))
                .addModel()
                .condition(CrossCollisionBlock.NORTH, true)
                .end();

        // East
        builder.part().modelFile(prov.models().withExistingParent(name + "_e", Chisel.id("block/pane/e"))
                        .texture("pane", texture)
                        .texture("edge", edge))
                .addModel()
                .condition(CrossCollisionBlock.EAST, true)
                .end();

        // South
        builder.part().modelFile(prov.models().withExistingParent(name + "_s", Chisel.id("block/pane/s"))
                        .texture("pane", texture)
                        .texture("edge", edge))
                .addModel()
                .condition(CrossCollisionBlock.SOUTH, true)
                .end();

        // West
        builder.part().modelFile(prov.models().withExistingParent(name + "_w", Chisel.id("block/pane/w"))
                        .texture("pane", texture)
                        .texture("edge", edge))
                .addModel()
                .condition(CrossCollisionBlock.WEST, true)
                .end();
    }

    // Bars model - for iron bars style blocks
    public static ModelTemplate bars(String texture, String edge, String side) {
        return (prov, block) -> {
            String name = "block/" + name(block);
            ResourceLocation tex = Chisel.id(texture);
            ResourceLocation edgeTex = Chisel.id(edge);
            ResourceLocation sideTex = Chisel.id(side);

            MultiPartBlockStateBuilder builder = prov.getMultipartBuilder(block);

            // Post ends (always rendered)
            builder.part().modelFile(prov.models().withExistingParent(name + "_post_ends", Chisel.id("block/bars_post_ends"))
                            .texture("bars", tex)
                            .texture("particle", tex)
                            .texture("edge", edgeTex)
                            .texture("side", sideTex))
                    .addModel()
                    .end();

            // Post (only when no connections)
            builder.part().modelFile(prov.models().withExistingParent(name + "_post", Chisel.id("block/bars_post"))
                            .texture("bars", tex)
                            .texture("particle", tex)
                            .texture("edge", edgeTex)
                            .texture("side", sideTex))
                    .addModel()
                    .condition(CrossCollisionBlock.NORTH, false)
                    .condition(CrossCollisionBlock.EAST, false)
                    .condition(CrossCollisionBlock.SOUTH, false)
                    .condition(CrossCollisionBlock.WEST, false)
                    .end();

            // Sides
            builder.part().modelFile(prov.models().withExistingParent(name + "_side", Chisel.id("block/bars_side"))
                            .texture("bars", tex)
                            .texture("particle", tex)
                            .texture("edge", edgeTex)
                            .texture("side", sideTex))
                    .addModel()
                    .condition(CrossCollisionBlock.NORTH, true)
                    .end();

            builder.part().modelFile(prov.models().getExistingFile(Chisel.id(name + "_side")))
                    .rotationY(90)
                    .addModel()
                    .condition(CrossCollisionBlock.EAST, true)
                    .end();

            builder.part().modelFile(prov.models().withExistingParent(name + "_side_alt", Chisel.id("block/bars_side_alt"))
                            .texture("bars", tex)
                            .texture("particle", tex)
                            .texture("edge", edgeTex)
                            .texture("side", sideTex))
                    .addModel()
                    .condition(CrossCollisionBlock.SOUTH, true)
                    .end();

            builder.part().modelFile(prov.models().getExistingFile(Chisel.id(name + "_side_alt")))
                    .rotationY(90)
                    .addModel()
                    .condition(CrossCollisionBlock.WEST, true)
                    .end();
        };
    }
}
