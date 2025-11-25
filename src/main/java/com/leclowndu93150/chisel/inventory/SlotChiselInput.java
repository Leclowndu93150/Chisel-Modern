package com.leclowndu93150.chisel.inventory;

import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Input slot for the chisel GUI where players place blocks to be chiseled.
 * This is the "special slot" in the InventoryChiselSelection.
 */
public class SlotChiselInput extends Slot {

    private final ChiselMenu container;

    public SlotChiselInput(ChiselMenu container, InventoryChiselSelection inv, int slot, int x, int y) {
        super(inv, slot, x, y);
        this.container = container;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        container.onChiselSlotChanged();
        container.getInventoryChisel().updateItems();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }
}
