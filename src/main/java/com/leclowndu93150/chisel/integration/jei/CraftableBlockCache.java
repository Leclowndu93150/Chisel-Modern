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
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesUpdatedEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = Chisel.MODID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class CraftableBlockCache {

    private static final Map<ResourceLocation, ItemStack> CACHE = new HashMap<>();

    @SubscribeEvent
    public static void onRecipesUpdated(RecipesUpdatedEvent event) {
        rebuild(event.getRecipeManager());
    }

    private static void rebuild(RecipeManager recipeManager) {
        CACHE.clear();

        Set<ResourceLocation> craftableItems = new HashSet<>();
        for (RecipeHolder<?> holder : recipeManager.getRecipes()) {
            if (holder.value() instanceof CraftingRecipe || holder.value() instanceof SmeltingRecipe) {
                ItemStack result = holder.value().getResultItem(Minecraft.getInstance().level.registryAccess());
                if (!result.isEmpty()) {
                    result.getItemHolder().unwrapKey().ifPresent(key -> craftableItems.add(key.location()));
                }
            }
        }

        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            CarvingGroup group = new CarvingGroup(blockType);
            ResourceLocation groupId = group.getId();
            List<Item> groupItems = CarvingHelper.getItemsInGroup(group.getItemTag());

            for (Item item : groupItems) {
                ResourceLocation itemId = item.builtInRegistryHolder().key().location();
                if (craftableItems.contains(itemId)) {
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
