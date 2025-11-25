package com.leclowndu93150.chisel.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

/**
 * Input slot for the chisel GUI where players place blocks to be chiseled.
 */
public class SlotChiselInput extends Slot {

    private final Consumer<ItemStack> onChanged;

    public SlotChiselInput(Container container, int slot, int x, int y, Consumer<ItemStack> onChanged) {
        super(container, slot, x, y);
        this.onChanged = onChanged;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        onChanged.accept(getItem());
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}
