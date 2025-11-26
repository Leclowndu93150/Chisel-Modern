package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

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
        buildWaterstoneRecipes(output);
        buildLavastoneRecipes(output);
        buildIceRecipes(output);
        buildMiscellaneousRecipes(output);
        buildGlassRecipes(output);
        buildBookshelfRecipes(output);
    }

    private void buildToolRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ChiselItems.IRON_CHISEL.get())
                .pattern(" I")
                .pattern("S ")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('S', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(output);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ChiselItems.DIAMOND_CHISEL.get())
                .pattern(" D")
                .pattern("S ")
                .define('D', Tags.Items.GEMS_DIAMOND)
                .define('S', Tags.Items.RODS_WOODEN)
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .save(output);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ChiselItems.HITECH_CHISEL.get())
                .requires(ChiselItems.DIAMOND_CHISEL.get())
                .requires(Tags.Items.INGOTS_GOLD)
                .requires(Tags.Items.DUSTS_REDSTONE)
                .unlockedBy("has_diamond_chisel", has(ChiselItems.DIAMOND_CHISEL.get()))
                .save(output);
    }

    private void buildAutoChiselRecipe(RecipeOutput output) {
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
        if (ChiselBlocks.FACTORY.getBlock("dots") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.FACTORY.getBlock("dots").get(), 32)
                    .pattern("SXS")
                    .pattern("X X")
                    .pattern("SXS")
                    .define('S', Tags.Items.STONES)
                    .define('X', Tags.Items.INGOTS_IRON)
                    .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                    .save(output, Chisel.id("factory/dots"));
        }
    }

    private void buildLaboratoryRecipes(RecipeOutput output) {
        if (ChiselBlocks.LABORATORY.getBlock("largesteel") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.LABORATORY.getBlock("largesteel").get(), 8)
                    .pattern("SSS")
                    .pattern("SQS")
                    .pattern("SSS")
                    .define('S', Blocks.STONE)
                    .define('Q', Items.QUARTZ)
                    .unlockedBy("has_quartz", has(Items.QUARTZ))
                    .save(output, Chisel.id("laboratory/largesteel"));
        }
    }

    private void buildAntiblockRecipes(RecipeOutput output) {
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
        if (ChiselBlocks.VOIDSTONE.getBlock("bevel") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.VOIDSTONE.getBlock("bevel").get(), 16)
                    .pattern(" E ")
                    .pattern("OOO")
                    .pattern(" E ")
                    .define('E', Items.ENDER_EYE)
                    .define('O', Tags.Items.OBSIDIANS)
                    .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                    .save(output, Chisel.id("voidstone/bevel_from_obsidian"));

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
        if (ChiselBlocks.CHARCOAL.getBlock("raw") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.CHARCOAL.getBlock("raw").get())
                    .pattern("CCC")
                    .pattern("CCC")
                    .pattern("CCC")
                    .define('C', Items.CHARCOAL)
                    .unlockedBy("has_charcoal", has(Items.CHARCOAL))
                    .save(output, Chisel.id("charcoal/raw"));

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.CHARCOAL, 9)
                    .requires(ChiselBlocks.CHARCOAL.getBlock("raw").get())
                    .unlockedBy("has_charcoal_block", has(ChiselBlocks.CHARCOAL.getBlock("raw").get()))
                    .save(output, Chisel.id("charcoal/raw_uncraft"));
        }
    }

    private void buildHexplatingRecipes(RecipeOutput output) {
        for (DyeColor color : DyeColor.values()) {
            var hexType = ChiselBlocks.HEX_PLATING.get(color);
            if (hexType != null) {
                var block = hexType.getBlock("hexbase");
                if (block != null) {
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
        if (ChiselBlocks.MARBLE.getBlock("raw") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.MARBLE.getBlock("raw").get(), 4)
                    .pattern("SB")
                    .pattern("BS")
                    .define('S', Blocks.STONE)
                    .define('B', Items.BONE_MEAL)
                    .unlockedBy("has_bone_meal", has(Items.BONE_MEAL))
                    .save(output, Chisel.id("marble/raw"));
        }

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

    private void buildWaterstoneRecipes(RecipeOutput output) {
        if (ChiselBlocks.WATERSTONE.getBlock("cracked") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.WATERSTONE.getBlock("cracked").get(), 8)
                    .pattern("SSS")
                    .pattern("SWS")
                    .pattern("SSS")
                    .define('S', Tags.Items.STONES)
                    .define('W', Items.WATER_BUCKET)
                    .unlockedBy("has_water_bucket", has(Items.WATER_BUCKET))
                    .save(output, Chisel.id("waterstone/cracked"));
        }
    }

    private void buildLavastoneRecipes(RecipeOutput output) {
        if (ChiselBlocks.LAVASTONE.getBlock("cracked") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.LAVASTONE.getBlock("cracked").get(), 8)
                    .pattern("SSS")
                    .pattern("SLS")
                    .pattern("SSS")
                    .define('S', Tags.Items.STONES)
                    .define('L', Items.LAVA_BUCKET)
                    .unlockedBy("has_lava_bucket", has(Items.LAVA_BUCKET))
                    .save(output, Chisel.id("lavastone/cracked"));
        }
    }

    private void buildIceRecipes(RecipeOutput output) {
        if (ChiselBlocks.ICE.getBlock("cube") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.ICE.getBlock("cube").get(), 8)
                    .pattern("III")
                    .pattern("I I")
                    .pattern("III")
                    .define('I', Items.PACKED_ICE)
                    .unlockedBy("has_packed_ice", has(Items.PACKED_ICE))
                    .save(output, Chisel.id("ice/cube"));
        }

        if (ChiselBlocks.ICE_PILLAR.getBlock("plain") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.ICE_PILLAR.getBlock("plain").get(), 8)
                    .pattern("III")
                    .pattern("I I")
                    .pattern("III")
                    .define('I', Items.ICE)
                    .unlockedBy("has_ice", has(Items.ICE))
                    .save(output, Chisel.id("ice_pillar/plain"));
        }
    }

    private void buildMiscellaneousRecipes(RecipeOutput output) {
        if (ChiselBlocks.CLOUD.getBlock("cloud") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.CLOUD.getBlock("cloud").get(), 8)
                    .pattern("WWW")
                    .pattern("WPW")
                    .pattern("WWW")
                    .define('W', Items.WHITE_WOOL)
                    .define('P', Items.PHANTOM_MEMBRANE)
                    .unlockedBy("has_phantom_membrane", has(Items.PHANTOM_MEMBRANE))
                    .save(output, Chisel.id("cloud/cloud"));
        }

        if (ChiselBlocks.FUTURA.getBlock("screen_metallic") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.FUTURA.getBlock("screen_metallic").get(), 8)
                    .pattern("IGI")
                    .pattern("GRG")
                    .pattern("IGI")
                    .define('I', Tags.Items.INGOTS_IRON)
                    .define('G', Blocks.GLOWSTONE)
                    .define('R', Tags.Items.DUSTS_REDSTONE)
                    .unlockedBy("has_glowstone", has(Blocks.GLOWSTONE))
                    .save(output, Chisel.id("futura/screen_metallic"));
        }

        if (ChiselBlocks.TECHNICAL.getBlock("scaffold") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.TECHNICAL.getBlock("scaffold").get(), 16)
                    .pattern("I I")
                    .pattern("III")
                    .pattern("I I")
                    .define('I', Tags.Items.INGOTS_IRON)
                    .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                    .save(output, Chisel.id("technical/scaffold"));
        }

        if (ChiselBlocks.TYRIAN.getBlock("tyrian") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.TYRIAN.getBlock("tyrian").get(), 8)
                    .pattern("SPS")
                    .pattern("PEP")
                    .pattern("SPS")
                    .define('S', Tags.Items.STONES)
                    .define('P', Blocks.PURPUR_BLOCK)
                    .define('E', Items.ENDER_EYE)
                    .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                    .save(output, Chisel.id("tyrian/tyrian"));
        }

        if (ChiselBlocks.VOIDSTONE_ENERGISED.getBlock("raw") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.VOIDSTONE_ENERGISED.getBlock("raw").get(), 8)
                    .pattern("OOO")
                    .pattern("OGO")
                    .pattern("OOO")
                    .define('O', Tags.Items.OBSIDIANS)
                    .define('G', Blocks.GLOWSTONE)
                    .unlockedBy("has_obsidian", has(Tags.Items.OBSIDIANS))
                    .save(output, Chisel.id("energized_voidstone/raw"));
        }

        if (ChiselBlocks.VOIDSTONE_RUNIC.getBlock("raw") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.VOIDSTONE_RUNIC.getBlock("raw").get(), 8)
                    .pattern("OOO")
                    .pattern("OEO")
                    .pattern("OOO")
                    .define('O', Tags.Items.OBSIDIANS)
                    .define('E', Items.ENDER_EYE)
                    .unlockedBy("has_ender_eye", has(Items.ENDER_EYE))
                    .save(output, Chisel.id("runic_voidstone/raw"));
        }

        if (ChiselBlocks.MARBLE_PILLAR.getBlock("pillar") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.MARBLE_PILLAR.getBlock("pillar").get(), 4)
                    .pattern("SB")
                    .pattern("BS")
                    .define('S', Blocks.STONE)
                    .define('B', Items.BONE_MEAL)
                    .unlockedBy("has_bone_meal", has(Items.BONE_MEAL))
                    .save(output, Chisel.id("marble_pillar/pillar"));
        }

        if (ChiselBlocks.PAPER.getBlock("box") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.PAPER.getBlock("box").get())
                    .pattern("PPP")
                    .pattern("PPP")
                    .pattern("PPP")
                    .define('P', Items.PAPER)
                    .unlockedBy("has_paper", has(Items.PAPER))
                    .save(output, Chisel.id("paper/box"));
        }

        if (ChiselBlocks.SANDSTONE_SCRIBBLES.getBlock("scribbles") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.SANDSTONE_SCRIBBLES.getBlock("scribbles").get(), 4)
                    .pattern("SS")
                    .pattern("SS")
                    .define('S', Tags.Items.SANDSTONE_BLOCKS)
                    .unlockedBy("has_sandstone", has(Tags.Items.SANDSTONE_BLOCKS))
                    .save(output, Chisel.id("sandstone_scribbles/scribbles"));
        }

        if (ChiselBlocks.RED_SANDSTONE_SCRIBBLES.getBlock("scribbles") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.RED_SANDSTONE_SCRIBBLES.getBlock("scribbles").get(), 4)
                    .pattern("SS")
                    .pattern("SS")
                    .define('S', Blocks.RED_SANDSTONE)
                    .unlockedBy("has_red_sandstone", has(Blocks.RED_SANDSTONE))
                    .save(output, Chisel.id("red_sandstone_scribbles/scribbles"));
        }

        if (ChiselBlocks.TEMPLE.getBlock("bricks") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.TEMPLE.getBlock("bricks").get(), 4)
                    .pattern("SC")
                    .pattern("CS")
                    .define('S', Blocks.STONE_BRICKS)
                    .define('C', Blocks.COBBLESTONE)
                    .unlockedBy("has_stone_bricks", has(Blocks.STONE_BRICKS))
                    .save(output, Chisel.id("temple/bricks"));
        }

        if (ChiselBlocks.TEMPLE_MOSSY.getBlock("bricks") != null) {
            ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ChiselBlocks.TEMPLE_MOSSY.getBlock("bricks").get(), 4)
                    .pattern("SC")
                    .pattern("CS")
                    .define('S', Blocks.MOSSY_STONE_BRICKS)
                    .define('C', Blocks.MOSSY_COBBLESTONE)
                    .unlockedBy("has_mossy_stone_bricks", has(Blocks.MOSSY_STONE_BRICKS))
                    .save(output, Chisel.id("templemossy/bricks"));
        }
    }

    private void buildGlassRecipes(RecipeOutput output) {
        for (DyeColor color : DyeColor.values()) {
            var dyedGlassType = ChiselBlocks.GLASS_DYED.get(color);
            if (dyedGlassType != null) {
                var baseBlock = dyedGlassType.getBlock("bubble");
                if (baseBlock != null) {
                    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, baseBlock.get(), 8)
                            .pattern("GGG")
                            .pattern("GDG")
                            .pattern("GGG")
                            .define('G', Tags.Items.GLASS_BLOCKS_COLORLESS)
                            .define('D', color.getTag())
                            .unlockedBy("has_glass", has(Tags.Items.GLASS_BLOCKS_COLORLESS))
                            .save(output, Chisel.id("glassdyed/" + color.getSerializedName() + "_bubble"));
                }
            }
        }
    }

    private void buildBookshelfRecipes(RecipeOutput output) {
        String[] woodTypes = {"spruce", "birch", "jungle", "acacia", "dark_oak", "mangrove", "cherry", "bamboo", "crimson", "warped"};

        for (String woodType : woodTypes) {
            var bookshelfType = ChiselBlocks.BOOKSHELVES.get(woodType);
            if (bookshelfType != null) {
                var baseBlock = bookshelfType.getBlock("rainbow");
                if (baseBlock != null) {
                    Item slab = getSlabItem(woodType);
                    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, baseBlock.get())
                            .pattern("SSS")
                            .pattern("BBB")
                            .pattern("SSS")
                            .define('S', slab)
                            .define('B', Items.BOOK)
                            .unlockedBy("has_book", has(Items.BOOK))
                            .save(output, Chisel.id("bookshelf/" + woodType + "_rainbow"));
                }
            }
        }
    }

    private Item getSlabItem(String woodType) {
        return switch (woodType) {
            case "oak" -> Items.OAK_SLAB;
            case "spruce" -> Items.SPRUCE_SLAB;
            case "birch" -> Items.BIRCH_SLAB;
            case "jungle" -> Items.JUNGLE_SLAB;
            case "acacia" -> Items.ACACIA_SLAB;
            case "dark_oak" -> Items.DARK_OAK_SLAB;
            case "mangrove" -> Items.MANGROVE_SLAB;
            case "cherry" -> Items.CHERRY_SLAB;
            case "bamboo" -> Items.BAMBOO_SLAB;
            case "crimson" -> Items.CRIMSON_SLAB;
            case "warped" -> Items.WARPED_SLAB;
            default -> Items.OAK_SLAB;
        };
    }
}
