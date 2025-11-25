package com.leclowndu93150.chisel.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Virtual inventory for displaying available chisel variations.
 * This inventory holds all possible variations for the currently selected block.
 */
public class InventoryChiselSelection implements Container {

    private final int size;
    private final List<ItemStack> stacks;
    private int activeVariations = 0;

    public InventoryChiselSelection(int size) {
        this.size = size + 1; // +1 for the target slot
        this.stacks = new ArrayList<>(this.size);
        for (int i = 0; i < this.size; i++) {
            stacks.add(ItemStack.EMPTY);
        }
    }

    @Override
    public int getContainerSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : stacks) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        if (slot < 0 || slot >= size) {
            return ItemStack.EMPTY;
        }
        return stacks.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        if (slot < 0 || slot >= size) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = stacks.get(slot);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (amount >= stack.getCount()) {
            stacks.set(slot, ItemStack.EMPTY);
            return stack;
        }
        ItemStack result = stack.copy();
        result.setCount(amount);
        stack.shrink(amount);
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        if (slot < 0 || slot >= size) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = stacks.get(slot);
        stacks.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (slot >= 0 && slot < size) {
            stacks.set(slot, stack);
        }
    }

    @Override
    public void setChanged() {
        // No-op for virtual inventory
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        for (int i = 0; i < size; i++) {
            stacks.set(i, ItemStack.EMPTY);
        }
        activeVariations = 0;
    }

    /**
     * Gets the target slot index (last slot in the inventory).
     */
    public int getTargetSlot() {
        return size - 1;
    }

    /**
     * Gets the target item stack.
     */
    public ItemStack getTarget() {
        return getItem(getTargetSlot());
    }

    /**
     * Sets the target item stack.
     */
    public void setTarget(ItemStack stack) {
        setItem(getTargetSlot(), stack);
    }

    /**
     * Gets the number of active variations currently displayed.
     */
    public int getActiveVariations() {
        return activeVariations;
    }

    /**
     * Sets the number of active variations.
     */
    public void setActiveVariations(int count) {
        this.activeVariations = count;
    }

    /**
     * Clears only the selection slots (not the target).
     */
    public void clearSelection() {
        for (int i = 0; i < size - 1; i++) {
            stacks.set(i, ItemStack.EMPTY);
        }
        activeVariations = 0;
    }
}
