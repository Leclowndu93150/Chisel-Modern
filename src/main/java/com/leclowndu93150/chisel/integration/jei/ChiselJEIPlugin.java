package com.leclowndu93150.chisel.integration.jei;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.carving.ICarvingGroup;
import com.leclowndu93150.chisel.carving.CarvingGroup;
// import com.leclowndu93150.chisel.carving.KubeJSCarvingGroup;
// import com.leclowndu93150.chisel.compat.kubejs.KubeJSCompat;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    public void onRuntimeAvailable(IJeiRuntime runtime) {
        // if (KubeJSCompat.isLoaded()) {
        //     Map<Identifier, Set<Identifier>> kubeGroups = KubeJSCompat.getCustomGroupsForJEI();
        //
        //     if (!kubeGroups.isEmpty()) {
        //         List<ICarvingGroup> kubeJSGroups = new ArrayList<>();
        //
        //         for (Map.Entry<Identifier, Set<Identifier>> entry : kubeGroups.entrySet()) {
        //             boolean isExistingGroup = ChiselBlocks.ALL_BLOCK_TYPES.stream()
        //                     .anyMatch(bt -> {
        //                         Identifier groupId = Chisel.id("carving/" + bt.getName());
        //                         return groupId.equals(entry.getKey());
        //                     });
        //
        //             if (!isExistingGroup) {
        //                 kubeJSGroups.add(new KubeJSCarvingGroup(entry.getKey(), entry.getValue()));
        //             }
        //         }
        //
        //         if (!kubeJSGroups.isEmpty()) {
        //             runtime.getRecipeManager().addRecipes(ChiselRecipeCategory.RECIPE_TYPE, kubeJSGroups);
        //         }
        //     }
        // }
    }

    @Override
    public Identifier getPluginUid() {
        return Chisel.id("jei_plugin");
    }
}
