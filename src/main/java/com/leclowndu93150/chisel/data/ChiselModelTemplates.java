package com.leclowndu93150.chisel.data;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ICarvable;
import com.leclowndu93150.chisel.mixin.BlockModelGeneratorsAccessor;
import com.leclowndu93150.chisel.mixin.TextureSlotAccessor;
import com.mojang.datafixers.functions.PointFreeRule;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.client.color.item.Constant;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelLocationUtils;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.Optional;

/**
 * Model templates for generating block models.
 * Ported from Chisel 1.18.2's ModelTemplates.java, updated for NeoForge 26.1 datagen.
 * <p>
 * Uses BlockModelGenerators with blockStateOutput/modelOutput consumers
 * instead of the removed BlockStateProvider.
 */
public class ChiselModelTemplates {

    /**
     * Functional interface for applying a model template to a block.
     * Renamed from ModelTemplate to ChiselModelTemplate to avoid conflict with
     * {@link net.minecraft.client.data.models.model.ModelTemplate}.
     */
    @FunctionalInterface
    public interface ChiselModelTemplate {
        void apply(Block block, BlockModelGenerators blockModels);
    }

    // =====================================================================
    // Utility methods
    // =====================================================================

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
        return name(block);
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

    /**
     * Gets the variant name from a block (for fluid textures).
     */
    private static String getVariantName(Block block) {
        if (block instanceof ICarvable carvable) {
            return carvable.getVariation().getTextureName();
        }
        String n = name(block);
        return n.contains("/") ? n.split("/")[1] : n;
    }

    // =====================================================================
    // Internal helper methods
    // =====================================================================

    /**
     * Creates a custom TextureSlot with the given name.
     * Uses a mixin accessor since TextureSlot.create() is private in vanilla.
     */
    private static TextureSlot slot(String name) {
        return TextureSlotAccessor.chisel$create(name);
    }

    /**
     * Registers a simple block blockstate (single variant pointing to the model).
     * Uses mixin accessor since createSimpleBlock/plainVariant are private in vanilla.
     */
    private static void simpleBlockState(Block block, Identifier modelLoc, BlockModelGenerators blockModels) {
        blockModels.blockStateOutput.accept(
                BlockModelGeneratorsAccessor.chisel$createSimpleBlock(block,
                        BlockModelGeneratorsAccessor.chisel$plainVariant(modelLoc)));
    }

    /**
     * Creates a Material from an existing Identifier.
     */
    private static Material mat(Identifier id) {
        return new Material(id);
    }

    /**
     * Creates a Material from a Chisel-namespaced path string.
     */
    private static Material mat(String path) {
        return new Material(Chisel.id(path));
    }

    private static Identifier cubeAllModel(Block block, Material texture, BlockModelGenerators blockModels) {
        TextureMapping mapping = new TextureMapping().put(TextureSlot.ALL, texture);
        return ModelTemplates.CUBE_ALL.create(block, mapping, blockModels.modelOutput);
    }

    private static Identifier cubeAllTranslucentModel(Block block, Material texture, BlockModelGenerators blockModels) {
        TextureMapping mapping = new TextureMapping().put(TextureSlot.ALL, texture).forceAllTranslucent();
        return ModelTemplates.CUBE_ALL.create(block, mapping, blockModels.modelOutput);
    }

    private static Identifier cubeColumnModel(Block block, Material side, Material end, BlockModelGenerators blockModels) {
        TextureMapping mapping = new TextureMapping().put(TextureSlot.SIDE, side).put(TextureSlot.END, end);
        return ModelTemplates.CUBE_COLUMN.create(block, mapping, blockModels.modelOutput);
    }

    private static Identifier cubeBottomTopModel(Block block, Material side, Material bottom, Material top, BlockModelGenerators blockModels) {
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.SIDE, side)
                .put(TextureSlot.BOTTOM, bottom)
                .put(TextureSlot.TOP, top);
        return ModelTemplates.CUBE_BOTTOM_TOP.create(block, mapping, blockModels.modelOutput);
    }

    private static Identifier parentModel(Block block, Identifier parent, TextureMapping mapping, BlockModelGenerators blockModels, TextureSlot... slots) {
        var template = new net.minecraft.client.data.models.model.ModelTemplate(Optional.of(parent), Optional.empty(), slots);
        return template.create(block, mapping, blockModels.modelOutput);
    }

    // =====================================================================
    // Simple cube_all templates
    // =====================================================================

    /**
     * Simple cube_all block with the block's own texture.
     */
    public static ChiselModelTemplate simpleBlock() {
        return (block, blockModels) -> {
            Material tex = mat("block/" + texturePath(block));
            Identifier model = cubeAllModel(block, tex, blockModels);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate simpleBlockCutout() {
        return simpleBlock();
    }

    public static ChiselModelTemplate simpleBlockTranslucent() {
        return (block, blockModels) -> {
            Material tex = mat("block/" + texturePath(block));
            Identifier model = cubeAllTranslucentModel(block, tex, blockModels);
            simpleBlockState(block, model, blockModels);
        };
    }

    /**
     * Cloud block template - translucent with no culling between same blocks.
     * Uses a custom parent model for the translucent no-cull behavior.
     */
    public static ChiselModelTemplate cloudBlock() {
        return (block, blockModels) -> {
            TextureMapping mapping = new TextureMapping()
                    .put(TextureSlot.ALL, mat("block/" + texturePath(block)));
            Identifier model = parentModel(block, Chisel.id("block/cube_all_translucent"),
                    mapping, blockModels, TextureSlot.ALL);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate cubeBottomTop() {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            Identifier model = cubeBottomTopModel(block,
                    mat(texPath + "-side"),
                    mat(texPath + "-bottom"),
                    mat(texPath + "-top"),
                    blockModels);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate cubeBottomTop(String side, String bottom, String top) {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            Identifier model = cubeBottomTopModel(block,
                    mat(replaceVariant(texPath, side)),
                    mat(replaceVariant(texPath, bottom)),
                    mat(replaceVariant(texPath, top)),
                    blockModels);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate cubeAll(String postfix) {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            Identifier model = cubeAllModel(block, mat(texPath + postfix), blockModels);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate cubeColumn() {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            Identifier model = cubeColumnModel(block,
                    mat(texPath + "-side"),
                    mat(texPath + "-top"),
                    blockModels);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate cubeColumnCutout() {
        return cubeColumn();
    }

    public static ChiselModelTemplate cubeColumn(String side, String top) {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            Identifier model = cubeColumnModel(block,
                    mat(replaceVariant(texPath, side)),
                    mat(replaceVariant(texPath, top)),
                    blockModels);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate bookshelf() {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureSlot overlay = slot("overlay");
            TextureMapping mapping = new TextureMapping().put(overlay, mat(texPath));
            Identifier model = parentModel(block, Chisel.id("block/bookshelf_base"), mapping, blockModels, overlay);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate bookshelf(String woodType) {
        return (block, blockModels) -> {
            String variantName = getVariantName(block);
            TextureSlot overlay = slot("overlay");
            TextureMapping mapping = new TextureMapping().put(overlay, mat("block/bookshelf/" + variantName));
            Identifier model = parentModel(block, Chisel.id("block/bookshelf_base_" + woodType), mapping, blockModels, overlay);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate ctm(String variant) {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            String texName = replaceVariant(texPath, variant);
            String ctmVariant = variant.endsWith("ct") ? variant.substring(0, variant.length() - 2) : variant;
            String ctmTexName = replaceVariant(texPath, ctmVariant);
            TextureSlot allSlot = TextureSlot.ALL;
            TextureSlot ctmSlot = slot("connected_tex");
            TextureMapping mapping = new TextureMapping().put(allSlot, mat(texName)).put(ctmSlot, mat(ctmTexName + "-ctm"));
            Identifier model = parentModel(block, Chisel.id("block/cube_ctm"), mapping, blockModels, allSlot, ctmSlot);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate twoLayerWithTop(String top, boolean shade) {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureSlot botSlot = slot("bot");
            TextureSlot topSlot = slot("top");
            TextureMapping mapping = new TextureMapping().put(botSlot, mat(texPath)).put(topSlot, mat(replaceVariant(texPath, top)));
            Identifier parent = Chisel.id(shade ? "block/cube_2_layer" : "block/cube_2_layer_no_shade");
            Identifier model = parentModel(block, parent, mapping, blockModels, botSlot, topSlot);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate axisFaces() {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureSlot x = slot("x");
            TextureSlot y = slot("y");
            TextureSlot z = slot("z");
            TextureMapping mapping = new TextureMapping()
                    .put(x, mat(texPath + "-ew"))
                    .put(y, mat(texPath + "-tb"))
                    .put(z, mat(texPath + "-ns"));
            Identifier model = parentModel(block, Chisel.id("block/cube_axis"), mapping, blockModels, x, y, z);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate axisFacesNoTop() {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureSlot x = slot("x");
            TextureSlot y = slot("y");
            TextureSlot z = slot("z");
            TextureMapping mapping = new TextureMapping()
                    .put(x, mat(texPath + "-ew"))
                    .put(y, mat(texPath + "-ns"))
                    .put(z, mat(texPath + "-ns"));
            Identifier model = parentModel(block, Chisel.id("block/cube_axis"), mapping, blockModels, x, y, z);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate mossy(String base) {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            String texName = replaceBlock(texPath, base);
            TextureSlot bot = slot("bot");
            TextureMapping mapping = new TextureMapping().put(bot, mat(texName));
            Identifier model = parentModel(block, Chisel.id("block/mossy/mossy"), mapping, blockModels, bot);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate mossyColumn(String base) {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            String texName = replaceBlock(texPath, base);
            TextureSlot side = slot("side");
            TextureSlot end = slot("end");
            TextureMapping mapping = new TextureMapping()
                    .put(side, mat(texName + "-side"))
                    .put(end, mat(texName + "-top"));
            Identifier model = parentModel(block, Chisel.id("block/mossy/mossy_column"), mapping, blockModels, side, end);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate mossyCtm(String base, String variant) {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            String texName = replaceVariant(replaceBlock(texPath, base), variant);
            String ctmVariant = variant.endsWith("ct") ? variant.substring(0, variant.length() - 2) : variant;
            String ctmTexName = replaceVariant(replaceBlock(texPath, base), ctmVariant);
            TextureSlot bot = slot("bot");
            TextureSlot connectBot = slot("connect_bot");
            TextureMapping mapping = new TextureMapping()
                    .put(bot, mat(texName))
                    .put(connectBot, mat(ctmTexName + "-ctm"));
            Identifier model = parentModel(block, Chisel.id("block/mossy/mossy_ctm"), mapping, blockModels, bot, connectBot);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate twoLayerTopShaded(String top, String bottom) {
        return twoLayerTopShaded(top, top, bottom);
    }

    public static ChiselModelTemplate twoLayerTopShaded(String particle, String top, String bottom) {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureSlot pSlot = TextureSlot.PARTICLE;
            TextureSlot tSlot = slot("top");
            TextureSlot bSlot = slot("bot");
            TextureMapping mapping = new TextureMapping()
                    .put(pSlot, mat(replaceVariant(texPath, particle)))
                    .put(tSlot, mat(replaceVariant(texPath, top)))
                    .put(bSlot, mat(replaceVariant(texPath, bottom)));
            Identifier model = parentModel(block, Chisel.id("block/cube_2_layer_topshaded"), mapping, blockModels, pSlot, tSlot, bSlot);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate threeLayerTopShaded(String particle, String top, String mid, String bottom) {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureSlot pSlot = TextureSlot.PARTICLE;
            TextureSlot tSlot = slot("top");
            TextureSlot mSlot = slot("mid");
            TextureSlot bSlot = slot("bot");
            TextureMapping mapping = new TextureMapping()
                    .put(pSlot, mat(replaceVariant(texPath, particle)))
                    .put(tSlot, mat(replaceVariant(texPath, top)))
                    .put(mSlot, mat(replaceVariant(texPath, mid)))
                    .put(bSlot, mat(replaceVariant(texPath, bottom)));
            Identifier model = parentModel(block, Chisel.id("block/cube_3_layer_topshaded"), mapping, blockModels, pSlot, tSlot, mSlot, bSlot);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate cubeCTMTranslucent(String all, String ctm) {
        return (block, blockModels) -> {
            TextureSlot allSlot = TextureSlot.ALL;
            TextureSlot ctmSlot = slot("connected_tex");
            TextureMapping mapping = new TextureMapping().put(allSlot, mat(Identifier.parse(all))).put(ctmSlot, mat(Identifier.parse(ctm))).forceAllTranslucent();
            Identifier model = parentModel(block, Chisel.id("block/cube_ctm_translucent"), mapping, blockModels, allSlot, ctmSlot);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate fluidCube(String fluid) {
        return (block, blockModels) -> {
            String variant = getVariantName(block);
            TextureSlot bot = slot("bot");
            TextureSlot top = slot("top");
            TextureMapping mapping = new TextureMapping()
                    .put(bot, mat(Identifier.withDefaultNamespace("block/" + fluid + "_still")))
                    .put(top, mat("block/fluid/" + variant))
                    .forceAllTranslucent();
            Identifier model = parentModel(block, Chisel.id("block/" + fluid + "stone/cube_" + fluid), mapping, blockModels, bot, top);
            simpleBlockState(block, model, blockModels);
            registerFluidTintedItem(block, model, blockModels, fluid);
        };
    }

    public static ChiselModelTemplate fluidCubeCTM(String fluid, String variant) {
        return (block, blockModels) -> {
            String ctmVariant = variant.endsWith("ct") ? variant.substring(0, variant.length() - 2) : variant;
            TextureSlot bot = slot("bot");
            TextureSlot top = slot("top");
            TextureSlot connectTop = slot("connect_top");
            TextureMapping mapping = new TextureMapping()
                    .put(bot, mat(Identifier.withDefaultNamespace("block/" + fluid + "_still")))
                    .put(top, mat("block/fluid/" + variant))
                    .put(connectTop, mat("block/fluid/" + ctmVariant + "-ctm"))
                    .forceAllTranslucent();
            Identifier model = parentModel(block, Chisel.id("block/" + fluid + "stone/cube_ctm_" + fluid), mapping, blockModels, bot, top, connectTop);
            simpleBlockState(block, model, blockModels);
            registerFluidTintedItem(block, model, blockModels, fluid);
        };
    }

    public static ChiselModelTemplate fluidPassCube(String fluid) {
        return (block, blockModels) -> {
            String variant = getVariantName(block);
            TextureSlot bot = slot("bot");
            TextureSlot top = slot("top");
            TextureMapping mapping = new TextureMapping()
                    .put(bot, mat(Identifier.withDefaultNamespace("block/" + fluid + "_still")))
                    .put(top, mat("block/fluid/" + variant))
                    .forceAllTranslucent();
            Identifier model = parentModel(block, Chisel.id("block/" + fluid + "stone/cube_pass_" + fluid), mapping, blockModels, bot, top);
            simpleBlockState(block, model, blockModels);
            registerFluidTintedItem(block, model, blockModels, fluid);
        };
    }

    public static ChiselModelTemplate fluidPassColumn(String fluid) {
        return (block, blockModels) -> {
            String variant = getVariantName(block);
            TextureSlot bot = slot("bot");
            TextureSlot side = slot("side");
            TextureSlot top = slot("top");
            TextureMapping mapping = new TextureMapping()
                    .put(bot, mat(Identifier.withDefaultNamespace("block/" + fluid + "_still")))
                    .put(side, mat("block/fluid/" + variant + "-side"))
                    .put(top, mat("block/fluid/" + variant + "-top"))
                    .forceAllTranslucent();
            Identifier model = parentModel(block, Chisel.id("block/" + fluid + "stone/column_" + fluid), mapping, blockModels, bot, side, top);
            simpleBlockState(block, model, blockModels);
            registerFluidTintedItem(block, model, blockModels, fluid);
        };
    }

    private static void registerFluidTintedItem(Block block, Identifier model, BlockModelGenerators blockModels, String fluid) {
        if (!"water".equals(fluid)) return;
        registerTintedBlockItem(block, model, blockModels,
                new Constant(0xFFFFFF),
                new Constant(0x3F76E4));
    }

    public static ChiselModelTemplate cubeEldritch() {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureMapping mapping = new TextureMapping().put(TextureSlot.ALL, mat(texPath));
            Identifier model = parentModel(block, Chisel.id("block/cube_eldritch"), mapping, blockModels, TextureSlot.ALL);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate columnEldritch(String top) {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureSlot end = slot("end");
            TextureSlot side = slot("side");
            TextureMapping mapping = new TextureMapping()
                    .put(end, mat(replaceVariant(texPath, top) + "-top"))
                    .put(side, mat(texPath + "-side"));
            Identifier model = parentModel(block, Chisel.id("block/column_eldritch"), mapping, blockModels, end, side);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate columnPillar() {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureSlot top = slot("top");
            TextureSlot pillar = slot("pillar");
            TextureMapping mapping = new TextureMapping()
                    .put(top, mat(texPath + "-top"))
                    .put(pillar, mat(texPath + "-ctmv"));
            Identifier model = parentModel(block, Chisel.id("block/column_pillar"), mapping, blockModels, top, pillar);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate hexPlate(String variant) {
        return (block, blockModels) -> {
            TextureSlot top = slot("top");
            TextureSlot bot = slot("bot");
            TextureMapping mapping = new TextureMapping()
                    .put(top, mat("block/hexplating/" + variant))
                    .put(bot, mat("block/animations/archetype2"));
            Identifier model = parentModel(block, Chisel.id("block/cube_2_layer"), mapping, blockModels, top, bot);
            simpleBlockState(block, model, blockModels);
            int color = dyeColorFromBlockPath(block, "hexplating_");
            registerTintedBlockItem(block, model, blockModels,
                    new Constant(0xFFFFFF),
                    new Constant(color));
        };
    }

    public static ChiselModelTemplate cube(String texture) {
        return (block, blockModels) -> {
            Identifier model = cubeAllModel(block, mat(texture), blockModels);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate paneBlock(String edge) {
        return (block, blockModels) -> {
            String texture = "block/" + name(block).replace("pane", "");
            Material paneTex = mat(texture);
            Material edgeTex = mat(edge);
            createPaneMultipart(block, paneTex, edgeTex, blockModels);
            registerFlatBlockItemFromTexture(block, paneTex, blockModels);
        };
    }

    public static ChiselModelTemplate paneBlockSideTop() {
        return (block, blockModels) -> {
            String basePath = "block/" + texturePath(block);
            Material paneTex = mat(basePath + "-side");
            Material edgeTex = mat(basePath + "-top");
            createPaneMultipart(block, paneTex, edgeTex, blockModels);
            registerFlatBlockItemFromTexture(block, paneTex, blockModels);
        };
    }

    private static void registerFlatBlockItem(Block block, BlockModelGenerators blockModels) {
        var accessor = (BlockModelGeneratorsAccessor) blockModels;
        Item item = block.asItem();
        if (item != Items.AIR) {
            Identifier model = accessor.chisel$createFlatItemModel(item);
            accessor.chisel$getItemModelOutput().accept(item, ItemModelUtils.plainModel(model));
        }
    }

    private static void registerFlatBlockItemFromTexture(Block block, Material texture, BlockModelGenerators blockModels) {
        Item item = block.asItem();
        if (item == Items.AIR) return;
        Identifier model = ModelTemplates.FLAT_ITEM.create(
                ModelLocationUtils.getModelLocation(item),
                TextureMapping.layer0(texture),
                blockModels.modelOutput);
        ((BlockModelGeneratorsAccessor) blockModels).chisel$getItemModelOutput()
                .accept(item, ItemModelUtils.plainModel(model));
    }

    private static void registerTintedBlockItem(Block block, Identifier model, BlockModelGenerators blockModels, ItemTintSource... tints) {
        Item item = block.asItem();
        if (item != Items.AIR) {
            ((BlockModelGeneratorsAccessor) blockModels).chisel$getItemModelOutput()
                    .accept(item, ItemModelUtils.tintedModel(model, tints));
        }
    }

    private static int dyeColorFromBlockPath(Block block, String prefix) {
        String path = BuiltInRegistries.BLOCK.getKey(block).getPath();
        int slash = path.indexOf('/');
        String namePart = slash >= 0 ? path.substring(0, slash) : path;
        String colorName = namePart.startsWith(prefix) ? namePart.substring(prefix.length()) : namePart;
        return DyeColor.byName(colorName, DyeColor.WHITE).getTextColor();
    }

    private static void createPaneMultipart(Block block, Material pane, Material edge, BlockModelGenerators blockModels) {
        TextureMapping mapping = new TextureMapping()
                .put(TextureSlot.PANE, pane)
                .put(TextureSlot.EDGE, edge);
        MultiVariant post = BlockModelGeneratorsAccessor.chisel$plainVariant(
                ModelTemplates.STAINED_GLASS_PANE_POST.create(block, mapping, blockModels.modelOutput));
        MultiVariant side = BlockModelGeneratorsAccessor.chisel$plainVariant(
                ModelTemplates.STAINED_GLASS_PANE_SIDE.create(block, mapping, blockModels.modelOutput));
        MultiVariant sideAlt = BlockModelGeneratorsAccessor.chisel$plainVariant(
                ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(block, mapping, blockModels.modelOutput));
        MultiVariant noSide = BlockModelGeneratorsAccessor.chisel$plainVariant(
                ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(block, mapping, blockModels.modelOutput));
        MultiVariant noSideAlt = BlockModelGeneratorsAccessor.chisel$plainVariant(
                ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(block, mapping, blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiPartGenerator.multiPart(block)
                        .with(post)
                        .with(new ConditionBuilder().term(BlockStateProperties.NORTH, true), side)
                        .with(new ConditionBuilder().term(BlockStateProperties.EAST, true), side.with(BlockModelGenerators.Y_ROT_90))
                        .with(new ConditionBuilder().term(BlockStateProperties.SOUTH, true), sideAlt)
                        .with(new ConditionBuilder().term(BlockStateProperties.WEST, true), sideAlt.with(BlockModelGenerators.Y_ROT_90))
                        .with(new ConditionBuilder().term(BlockStateProperties.NORTH, false), noSide)
                        .with(new ConditionBuilder().term(BlockStateProperties.EAST, false), noSideAlt)
                        .with(new ConditionBuilder().term(BlockStateProperties.SOUTH, false), noSideAlt.with(BlockModelGenerators.Y_ROT_90))
                        .with(new ConditionBuilder().term(BlockStateProperties.WEST, false), noSide.with(BlockModelGenerators.Y_ROT_270))
        );
    }

    public static ChiselModelTemplate bars(String texture, String edge, String side) {
        return (block, blockModels) -> {
            Material barsTex = mat(texture);
            Material edgeTex = mat(edge);
            Material sideTex = mat(side);
            TextureMapping mapping = new TextureMapping()
                    .put(TextureSlot.BARS, barsTex)
                    .put(TextureSlot.EDGE, edgeTex);
            createBarsMultipart(block, mapping, blockModels);
            registerFlatBlockItemFromTexture(block, sideTex, blockModels);
        };
    }

    public static ChiselModelTemplate ironBars() {
        return (block, blockModels) -> {
            String basePath = "block/" + texturePath(block);
            Material barsTex = mat(basePath);
            TextureMapping mapping = new TextureMapping()
                    .put(TextureSlot.BARS, barsTex)
                    .put(TextureSlot.EDGE, barsTex);
            createBarsMultipart(block, mapping, blockModels);
            registerFlatBlockItemFromTexture(block, barsTex, blockModels);
        };
    }

    private static void createBarsMultipart(Block block, TextureMapping mapping, BlockModelGenerators blockModels) {
        MultiVariant postEnds = BlockModelGeneratorsAccessor.chisel$plainVariant(
                ModelTemplates.BARS_POST_ENDS.create(block, mapping, blockModels.modelOutput));
        MultiVariant post = BlockModelGeneratorsAccessor.chisel$plainVariant(
                ModelTemplates.BARS_POST.create(block, mapping, blockModels.modelOutput));
        MultiVariant cap = BlockModelGeneratorsAccessor.chisel$plainVariant(
                ModelTemplates.BARS_CAP.create(block, mapping, blockModels.modelOutput));
        MultiVariant capAlt = BlockModelGeneratorsAccessor.chisel$plainVariant(
                ModelTemplates.BARS_CAP_ALT.create(block, mapping, blockModels.modelOutput));
        MultiVariant side = BlockModelGeneratorsAccessor.chisel$plainVariant(
                ModelTemplates.BARS_POST_SIDE.create(block, mapping, blockModels.modelOutput));
        MultiVariant sideAlt = BlockModelGeneratorsAccessor.chisel$plainVariant(
                ModelTemplates.BARS_POST_SIDE_ALT.create(block, mapping, blockModels.modelOutput));

        blockModels.blockStateOutput.accept(
                MultiPartGenerator.multiPart(block)
                        .with(postEnds)
                        .with(new ConditionBuilder()
                                .term(BlockStateProperties.NORTH, false)
                                .term(BlockStateProperties.EAST, false)
                                .term(BlockStateProperties.SOUTH, false)
                                .term(BlockStateProperties.WEST, false), post)
                        .with(new ConditionBuilder()
                                .term(BlockStateProperties.NORTH, true)
                                .term(BlockStateProperties.EAST, false)
                                .term(BlockStateProperties.SOUTH, false)
                                .term(BlockStateProperties.WEST, false), cap)
                        .with(new ConditionBuilder()
                                .term(BlockStateProperties.NORTH, false)
                                .term(BlockStateProperties.EAST, true)
                                .term(BlockStateProperties.SOUTH, false)
                                .term(BlockStateProperties.WEST, false), cap.with(BlockModelGenerators.Y_ROT_90))
                        .with(new ConditionBuilder()
                                .term(BlockStateProperties.NORTH, false)
                                .term(BlockStateProperties.EAST, false)
                                .term(BlockStateProperties.SOUTH, true)
                                .term(BlockStateProperties.WEST, false), capAlt)
                        .with(new ConditionBuilder()
                                .term(BlockStateProperties.NORTH, false)
                                .term(BlockStateProperties.EAST, false)
                                .term(BlockStateProperties.SOUTH, false)
                                .term(BlockStateProperties.WEST, true), capAlt.with(BlockModelGenerators.Y_ROT_90))
                        .with(new ConditionBuilder().term(BlockStateProperties.NORTH, true), side)
                        .with(new ConditionBuilder().term(BlockStateProperties.EAST, true), side.with(BlockModelGenerators.Y_ROT_90))
                        .with(new ConditionBuilder().term(BlockStateProperties.SOUTH, true), sideAlt)
                        .with(new ConditionBuilder().term(BlockStateProperties.WEST, true), sideAlt.with(BlockModelGenerators.Y_ROT_90))
        );
    }

    public static ChiselModelTemplate woolCtm() {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureSlot allSlot = TextureSlot.ALL;
            TextureSlot ctmSlot = slot("connected_tex");
            TextureMapping mapping = new TextureMapping()
                    .put(allSlot, mat(texPath))
                    .put(ctmSlot, mat(texPath + "-ctm"));
            Identifier model = parentModel(block, Chisel.id("block/cube_ctm"), mapping, blockModels, allSlot, ctmSlot);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate carpetCtm() {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block).replace("carpet/", "wool/");
            TextureSlot allSlot = TextureSlot.ALL;
            TextureSlot ctmSlot = slot("all_ctm");
            TextureMapping mapping = new TextureMapping()
                    .put(allSlot, mat(texPath))
                    .put(ctmSlot, mat(texPath + "-ctm"));
            Identifier model = parentModel(block, Chisel.id("block/carpet_ctm"), mapping, blockModels, allSlot, ctmSlot);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate mysteriousCube() {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureSlot core = slot("core");
            TextureSlot side = slot("side");
            TextureSlot top = slot("top");
            TextureSlot bottom = slot("bottom");
            TextureMapping mapping = new TextureMapping()
                    .put(core, mat(replaceVariant(texPath, "mysterious_cube_core")))
                    .put(side, mat(replaceVariant(texPath, "mysterious_cube_side")))
                    .put(top, mat(replaceVariant(texPath, "mysterious_cube_top")))
                    .put(bottom, mat(replaceVariant(texPath, "mysterious_cube_bottom")));
            Identifier model = parentModel(block, Chisel.id("block/futura/mysterious_cube_base"), mapping, blockModels, core, side, top, bottom);
            simpleBlockState(block, model, blockModels);
        };
    }

    public static ChiselModelTemplate ae2Controller() {
        return (block, blockModels) -> {
            String texPath = "block/" + texturePath(block);
            TextureSlot blockTex = slot("block");
            TextureSlot lights = slot("lights");
            TextureMapping mapping = new TextureMapping()
                    .put(blockTex, mat(replaceVariant(texPath, "ae2_controller")))
                    .put(lights, mat(replaceVariant(texPath, "ae2_controller_lights")));
            Identifier model = parentModel(block, Chisel.id("block/futura/ae2_controller_base"), mapping, blockModels, blockTex, lights);
            simpleBlockState(block, model, blockModels);
        };
    }
}
