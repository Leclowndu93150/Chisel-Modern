package com.leclowndu93150.chisel.integration.jei;

import com.leclowndu93150.chisel.Chisel;
import net.minecraft.core.registries.BuiltInRegistries;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.carving.CarvingGroup;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeMap;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RecipesReceivedEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = Chisel.MODID, value = Dist.CLIENT)
public class CraftableBlockCache {

    private static final Map<Identifier, ItemStack> CACHE = new HashMap<>();

    @SubscribeEvent
    public static void onRecipesReceived(RecipesReceivedEvent event) {
        rebuild(event.getRecipeMap());
    }

    private static void rebuild(RecipeMap recipeMap) {
        CACHE.clear();

        Set<Identifier> craftableItems = new HashSet<>();
        for (RecipeHolder<?> holder : recipeMap.values()) {
            if (holder.value() instanceof CraftingRecipe || holder.value() instanceof SmeltingRecipe) {
                for (RecipeDisplay display : holder.value().display()) {
                    SlotDisplay resultDisplay = display.result();
                    if (resultDisplay instanceof SlotDisplay.ItemSlotDisplay itemDisplay) {
                        itemDisplay.item().unwrapKey().ifPresent(key -> craftableItems.add(key.identifier()));
                    } else if (resultDisplay instanceof SlotDisplay.ItemStackSlotDisplay stackDisplay) {
                        stackDisplay.stack().item().unwrapKey().ifPresent(key -> craftableItems.add(key.identifier()));
                    }
                }
            }
        }

        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            CarvingGroup group = new CarvingGroup(blockType);
            Identifier groupId = group.getId();
            List<Item> groupItems = CarvingHelper.getItemsInGroup(group.getItemTag());

            for (Item item : groupItems) {
                Identifier itemId = item.builtInRegistryHolder().key().identifier();
                if (craftableItems.contains(itemId)) {
                    CACHE.put(groupId, new ItemStack(item));
                    break;
                }
            }
        }
    }

    public static ItemStack get(Identifier groupId) {
        return CACHE.getOrDefault(groupId, ItemStack.EMPTY);
    }
}
