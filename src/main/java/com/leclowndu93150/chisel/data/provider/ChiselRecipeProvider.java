package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

/**
 * Data provider for generating recipe JSON files.
 * Recipes ported from Chisel 1.18.2.
 */
public class ChiselRecipeProvider extends RecipeProvider {

    public ChiselRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        buildToolRecipes(output);
        buildAutoChiselRecipe(output);
        buildFactoryRecipes(output);
        buildLaboratoryRecipes(output);
        buildAntiblockRecipes(output);
        buildBrownstoneRecipes(output);
        buildVoidstoneRecipes(output);
        buildCharcoalRecipes(output);
        buildHexplatingRecipes(output);
        buildFuturaRecipes(output);
        buildValentinesRecipes(output);
        buildCustomStoneRecipes(output);
    }

    private void buildToolRecipes(RecipeOutput output) {
        // Iron Chisel: stick + iron ingot
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ChiselItems.IRON_CHISEL.get())
                .pattern(" I")
                .pattern("S ")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('S', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(output);

        // Diamond Chisel: stick + diamond
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ChiselItems.DIAMOND_CHISEL.get())
                .pattern(" D")
                .pattern("S ")
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('S', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .save(output);

        // Hitech Chisel: diamond chisel + gold + redstone (shapeless)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ChiselItems.HITECH_CHISEL.get())
                .requires(ChiselItems.DIAMOND_CHISEL.get())
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_diamond_chisel", has(ChiselItems.DIAMOND_CHISEL.get()))
                .save(output);
    }

    private void buildAutoChiselRecipe(RecipeOutput output) {
        // Auto Chisel: glass + redstone + iron
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ChiselItems.AUTO_CHISEL.get())
                .pattern("GGG")
                .pattern("GRG")
                .pattern("III")
                .define('G', Tags.Items.GLASS_BLOCKS)
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('I', Tags.Items.INGOTS_IRON)
                .unlockedBy("has_redstone", has(Tags.Items.DUSTS_REDSTONE))
                .save(output);
    }

    private void buildFactoryRecipes(RecipeOutput output) {
        // Factory circuit: stone + iron in alternating pattern = 32
        if (ChiselBlocks.FACTORY.getBlock("circuit") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.FACTORY.getBlock("circuit").get(), 32)
                    .pattern("SXS")
                    .pattern("X X")
                    .pattern("SXS")
                    .define('S', Tags.Items.STONES)
                    .define('X', Tags.Items.INGOTS_IRON)
                    .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                    .save(output, Chisel.id("factory/circuit"));
        }

        // Factory dots, rust variants, etc. share similar recipes
        // Most factory blocks use the circuit as base
    }

    private void buildLaboratoryRecipes(RecipeOutput output) {
        // Laboratory large steel: stone surrounding quartz = 8
        if (ChiselBlocks.LABORATORY.getBlock("largesteel") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.LABORATORY.getBlock("largesteel").get(), 8)
                    .pattern("***")
                    .pattern("*X*")
                    .pattern("***")
                    .define('*', Blocks.STONE)
                    .define('X', Items.QUARTZ)
                    .unlockedBy("has_quartz", has(Items.QUARTZ))
                    .save(output, Chisel.id("laboratory/largesteel"));
        }
    }

    private void buildAntiblockRecipes(RecipeOutput output) {
        // Antiblock white: glowstone + 4 stone = 8
        var block = ChiselBlocks.ANTIBLOCK.getBlock("white");
        if (block != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block.get(), 8)
                    .pattern(" S ")
                    .pattern("SGS")
                    .pattern(" S ")
                    .define('S', Tags.Items.STONES)
                    .define('G', Blocks.GLOWSTONE)
                    .unlockedBy("has_glowstone", has(Blocks.GLOWSTONE))
                    .save(output, Chisel.id("antiblock/white"));
        }
    }

    private void buildBrownstoneRecipes(RecipeOutput output) {
        // Brownstone: sandstone + clay ball = 4
        if (ChiselBlocks.BROWNSTONE.getBlock("default") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.BROWNSTONE.getBlock("default").get(), 4)
                    .pattern(" S ")
                    .pattern("SCS")
                    .pattern(" S ")
                    .define('S', Tags.Items.SANDSTONE_BLOCKS)
                    .define('C', Items.CLAY_BALL)
                    .unlockedBy("has_clay", has(Items.CLAY_BALL))
                    .save(output, Chisel.id("brownstone/default"));
        }
    }

    private void buildVoidstoneRecipes(RecipeOutput output) {
        // Voidstone bevel: obsidian + ender eye = 16
        if (ChiselBlocks.VOIDSTONE.getBlock("bevel") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.VOIDSTONE.getBlock("bevel").get(), 16)
                    .pattern(" E ")
                    .pattern("OOO")
                    .pattern(" E ")
                    .define('E', Items.ENDER_EYE)
                    .define('O', Tags.Items.OBSIDIANS)
                    .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                    .save(output, Chisel.id("voidstone/bevel_from_obsidian"));

            // Alternative: purpur + ender eye = 48
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.VOIDSTONE.getBlock("bevel").get(), 48)
                    .pattern(" P ")
                    .pattern("PEP")
                    .pattern(" P ")
                    .define('E', Items.ENDER_EYE)
                    .define('P', Blocks.PURPUR_BLOCK)
                    .unlockedBy("has_purpur", has(Blocks.PURPUR_BLOCK))
                    .save(output, Chisel.id("voidstone/bevel_from_purpur"));
        }
    }

    private void buildCharcoalRecipes(RecipeOutput output) {
        // Charcoal block raw: 9 charcoal = 1 block
        if (ChiselBlocks.CHARCOAL.getBlock("raw") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.CHARCOAL.getBlock("raw").get())
                    .pattern("CCC")
                    .pattern("CCC")
                    .pattern("CCC")
                    .define('C', Items.CHARCOAL)
                    .unlockedBy("has_charcoal", has(Items.CHARCOAL))
                    .save(output, Chisel.id("charcoal/raw"));

            // Reverse: block -> 9 charcoal
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.CHARCOAL, 9)
                    .requires(ChiselBlocks.CHARCOAL.getBlock("raw").get())
                    .unlockedBy("has_charcoal_block", has(ChiselBlocks.CHARCOAL.getBlock("raw").get()))
                    .save(output, Chisel.id("charcoal/raw_uncraft"));
        }
    }

    private void buildHexplatingRecipes(RecipeOutput output) {
        // Hex plating for each color
        for (DyeColor color : DyeColor.values()) {
            var hexType = ChiselBlocks.HEX_PLATING.get(color);
            if (hexType != null) {
                var block = hexType.getBlock("hexbase");
                if (block != null) {
                    // Hex plating: stone + dye + iron nugget = 8
                    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, block.get(), 8)
                            .pattern("SSS")
                            .pattern("SDS")
                            .pattern("NNN")
                            .define('S', Tags.Items.STONES)
                            .define('D', color.getTag())
                            .define('N', Tags.Items.NUGGETS_IRON)
                            .unlockedBy("has_dye", has(color.getTag()))
                            .save(output, Chisel.id("hexplating/" + color.getSerializedName() + "_hexbase"));
                }
            }
        }
    }

    private void buildFuturaRecipes(RecipeOutput output) {
        // Futura controller: glowstone + iron + redstone
        if (ChiselBlocks.FUTURA.getBlock("controller") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.FUTURA.getBlock("controller").get(), 8)
                    .pattern("IGI")
                    .pattern("GRG")
                    .pattern("IGI")
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('G', Blocks.GLOWSTONE)
                    .define('R', Tags.Items.DUSTS_REDSTONE)
                    .unlockedBy("has_glowstone", has(Blocks.GLOWSTONE))
                    .save(output, Chisel.id("futura/controller"));
        }
    }

    private void buildValentinesRecipes(RecipeOutput output) {
        // Valentines companion cube: wool + redstone block
        if (ChiselBlocks.VALENTINES.getBlock("companion") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.VALENTINES.getBlock("companion").get(), 8)
                    .pattern("WWW")
                    .pattern("WRW")
                    .pattern("WWW")
                    .define('W', Blocks.PINK_WOOL)
                    .define('R', Blocks.REDSTONE_BLOCK)
                    .unlockedBy("has_redstone_block", has(Blocks.REDSTONE_BLOCK))
                    .save(output, Chisel.id("valentines/companion"));
        }
    }

    private void buildCustomStoneRecipes(RecipeOutput output) {
        // Marble: stone + bone meal
        if (ChiselBlocks.MARBLE.getBlock("raw") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.MARBLE.getBlock("raw").get(), 4)
                    .pattern("SB")
                    .pattern("BS")
                    .define('S', Blocks.STONE)
                    .define('B', Items.BONE_MEAL)
                    .unlockedBy("has_bone_meal", has(Items.BONE_MEAL))
                    .save(output, Chisel.id("marble/raw"));
        }

        // Limestone: stone + gravel
        if (ChiselBlocks.LIMESTONE.getBlock("raw") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.LIMESTONE.getBlock("raw").get(), 4)
                    .pattern("SG")
                    .pattern("GS")
                    .define('S', Blocks.STONE)
                    .define('G', Blocks.GRAVEL)
                    .unlockedBy("has_gravel", has(Blocks.GRAVEL))
                    .save(output, Chisel.id("limestone/raw"));
        }
    }
}
