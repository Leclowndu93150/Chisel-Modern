package com.leclowndu93150.chisel.inventory;

import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import net.minecraft.core.NonNullList;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Inventory for the chisel selection grid.
 * The "special slot" (last slot) holds the target item being chiseled.
 * Selection slots (0 to size-1) display available variations.
 */
public class InventoryChiselSelection implements Container {

    public final int size;
    public int activeVariations = 0;
    @Nullable
    ChiselMenu container;
    NonNullList<ItemStack> inventory;

    public InventoryChiselSelection(ChiselMenu container, int size) {
        this.size = size;
        this.container = container;
        this.inventory = NonNullList.withSize(size + 1, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return size + 1;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : inventory) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= inventory.size()) {
            return ItemStack.EMPTY;
        }
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack stack = inventory.get(slot);
        if (!stack.isEmpty()) {
            if (stack.getCount() <= amount) {
                setItem(slot, ItemStack.EMPTY);
                return stack;
            } else {
                ItemStack split = stack.split(amount);
                if (stack.getCount() == 0) {
                    setItem(slot, ItemStack.EMPTY);
                }
                return split;
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = getItem(slot);
        inventory.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < inventory.size()) {
            inventory.set(slot, stack);
        }
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setChanged() {
    }

    @Override
    public boolean stillValid(Player player) {
        if (container == null) return false;
        ItemStack held = player.getInventory().getItem(container.getChiselSlot());
        return !held.isEmpty() && held.getItem() instanceof IChiselItem chiselItem &&
               chiselItem.canOpenGui(player.level(), player, container.getHand());
    }

    @Override
    public void clearContent() {
        inventory.clear();
        for (int i = 0; i < size + 1; i++) {
            inventory.add(ItemStack.EMPTY);
        }
    }

    /**
     * Gets the item in the special slot (input/target slot).
     */
    public ItemStack getStackInSpecialSlot() {
        return inventory.get(size);
    }

    /**
     * Sets the item in the special slot (input/target slot).
     */
    public void setStackInSpecialSlot(ItemStack stack) {
        setItem(size, stack);
    }

    /**
     * Clears only the selection slots, not the special slot.
     */
    public void clearItems() {
        activeVariations = 0;
        for (int i = 0; i < size; i++) {
            setItem(i, ItemStack.EMPTY);
        }
    }

    /**
     * Updates the selection slots based on the current target item.
     * Populates with available variations for chiseling.
     */
    public void updateItems() {
        ItemStack target = getStackInSpecialSlot();
        clearItems();

        if (target.isEmpty()) {
            return;
        }

        TagKey<Item> group = CarvingHelper.getCarvingGroupForItem(target);
        if (group == null) {
            return;
        }

        List<Item> variations = CarvingHelper.getItemsInGroup(group);
        activeVariations = 0;

        for (Item item : variations) {
            if (activeVariations >= size) {
                break;
            }
            setItem(activeVariations, new ItemStack(item));
            activeVariations++;
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        if (slot != size) {
            return false;
        }
        return CarvingHelper.canChisel(stack);
    }
}
