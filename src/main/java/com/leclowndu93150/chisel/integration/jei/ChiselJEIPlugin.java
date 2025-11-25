package com.leclowndu93150.chisel.integration.jei;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.carving.ICarvingGroup;
import com.leclowndu93150.chisel.carving.CarvingGroup;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@JeiPlugin
public class ChiselJEIPlugin implements IModPlugin {

    private ChiselRecipeCategory category;

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        category = new ChiselRecipeCategory(registration.getJeiHelpers().getGuiHelper());
        registration.addRecipeCategories(category);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<ICarvingGroup> groups = ChiselBlocks.ALL_BLOCK_TYPES.stream()
                .map(blockType -> (ICarvingGroup) new CarvingGroup(blockType))
                .toList();

        registration.addRecipes(ChiselRecipeCategory.RECIPE_TYPE, groups);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ChiselItems.IRON_CHISEL.get()), ChiselRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ChiselItems.DIAMOND_CHISEL.get()), ChiselRecipeCategory.RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(ChiselItems.HITECH_CHISEL.get()), ChiselRecipeCategory.RECIPE_TYPE);
    }

    @Override
    public ResourceLocation getPluginUid() {
        return Chisel.id("jei_plugin");
    }
}
