package com.leclowndu93150.chisel.integration.jei;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.api.carving.ICarvingGroup;
import com.leclowndu93150.chisel.carving.CarvingGroup;
import com.leclowndu93150.chisel.carving.KubeJSCarvingGroup;
import com.leclowndu93150.chisel.compat.kubejs.KubeJSCompat;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@JeiPlugin
public class ChiselJEIPlugin implements IModPlugin {

    private ChiselRecipeCategory category;
    private final Set<ResourceLocation> registeredGroupIds = new HashSet<>();

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        category = new ChiselRecipeCategory(registration.getJeiHelpers().getGuiHelper());
        registration.addRecipeCategories(category);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registeredGroupIds.clear();
        List<ICarvingGroup> groups = new ArrayList<>();

        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            groups.add(new CarvingGroup(blockType));
            registeredGroupIds.add(blockType.getCarvingGroupTag().location());
        }

        BuiltInRegistries.BLOCK.getTagNames()
                .map(TagKey::location)
                .filter(loc -> loc.getNamespace().equals(Chisel.MODID)
                        && loc.getPath().startsWith("carving/")
                        && !registeredGroupIds.contains(loc))
                .toList()
                .forEach(loc -> {
                    groups.add(new KubeJSCarvingGroup(loc, Collections.emptySet()));
                    registeredGroupIds.add(loc);
                });

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
        if (KubeJSCompat.isLoaded()) {
            Map<ResourceLocation, Set<ResourceLocation>> kubeGroups = KubeJSCompat.getCustomGroupsForJEI();

            if (!kubeGroups.isEmpty()) {
                List<ICarvingGroup> kubeJSGroups = new ArrayList<>();

                for (Map.Entry<ResourceLocation, Set<ResourceLocation>> entry : kubeGroups.entrySet()) {
                    if (!registeredGroupIds.contains(entry.getKey())) {
                        kubeJSGroups.add(new KubeJSCarvingGroup(entry.getKey(), entry.getValue()));
                        registeredGroupIds.add(entry.getKey());
                    }
                }

                if (!kubeJSGroups.isEmpty()) {
                    runtime.getRecipeManager().addRecipes(ChiselRecipeCategory.RECIPE_TYPE, kubeJSGroups);
                }
            }
        }
    }

    @Override
    public ResourceLocation getPluginUid() {
        return Chisel.id("jei_plugin");
    }
}
