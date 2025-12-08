package com.leclowndu93150.chisel.integration.jei;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.carving.CarvingGroup;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mod.EventBusSubscriber(modid = Chisel.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CraftableBlockCache {

    private static final Map<ResourceLocation, ItemStack> CACHE = new HashMap<>();

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        rebuild(event.getRecipeManager());
    }

    private static void rebuild(RecipeManager recipeManager) {
        CACHE.clear();

        Set<ResourceLocation> craftableItems = new HashSet<>();

        // Collect craftable items from crafting recipes
        for (Recipe<?> recipe : recipeManager.getAllRecipesFor(RecipeType.CRAFTING)) {
            ItemStack result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
            if (!result.isEmpty()) {
                ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(result.getItem());
                if (itemId != null) {
                    craftableItems.add(itemId);
                }
            }
        }

        // Collect craftable items from smelting recipes
        for (Recipe<?> recipe : recipeManager.getAllRecipesFor(RecipeType.SMELTING)) {
            ItemStack result = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());
            if (!result.isEmpty()) {
                ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(result.getItem());
                if (itemId != null) {
                    craftableItems.add(itemId);
                }
            }
        }

        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            CarvingGroup group = new CarvingGroup(blockType);
            ResourceLocation groupId = group.getId();
            List<Item> groupItems = CarvingHelper.getItemsInGroup(group.getItemTag());

            for (Item item : groupItems) {
                ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
                if (itemId != null && craftableItems.contains(itemId)) {
                    CACHE.put(groupId, new ItemStack(item));
                    break;
                }
            }
        }
    }

    public static ItemStack get(ResourceLocation groupId) {
        return CACHE.getOrDefault(groupId, ItemStack.EMPTY);
    }
}
