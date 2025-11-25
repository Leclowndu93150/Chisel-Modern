package com.leclowndu93150.chisel.inventory;

import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Selection slot for the chisel GUI that displays available variations.
 * These slots are read-only and handle the crafting logic when clicked.
 */
public class SlotChiselSelection extends Slot {

    private final ChiselMenu menu;
    private final Supplier<ItemStack> chiselSupplier;

    public SlotChiselSelection(ChiselMenu menu, InventoryChiselSelection inventory, int slot, int x, int y, Supplier<ItemStack> chiselSupplier) {
        super(inventory, slot, x, y);
        this.menu = menu;
        this.chiselSupplier = chiselSupplier;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        // Selection slots are read-only - no direct placement
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return hasItem() && !menu.getInputSlot().getItem().isEmpty();
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        ItemStack chisel = chiselSupplier.get();
        ItemStack input = menu.getInputSlot().getItem();

        if (chisel.isEmpty() || input.isEmpty()) {
            return;
        }

        // Calculate how many we can craft
        int craftCount = Math.min(input.getCount(), stack.getMaxStackSize());

        // Handle chisel damage
        if (chisel.getItem() instanceof IChiselItem chiselItem) {
            if (chisel.isDamageableItem()) {
                int damageLeft = chisel.getMaxDamage() - chisel.getDamageValue();
                craftCount = Math.min(craftCount, damageLeft);
                chisel.hurtAndBreak(craftCount, player, EquipmentSlot.MAINHAND);
            }
        }

        // Update counts
        input.shrink(craftCount);
        stack.setCount(craftCount);

        // Play sound
        player.level().playSound(player, player.blockPosition(), SoundEvents.UI_STONECUTTER_TAKE_RESULT,
                SoundSource.BLOCKS, 1.0F, 1.0F);

        // Update the menu
        menu.onInputChanged(input);

        super.onTake(player, stack);
    }

    @Override
    public ItemStack getItem() {
        ItemStack stack = super.getItem();
        if (!stack.isEmpty() && !menu.getInputSlot().getItem().isEmpty()) {
            // Match the count to the input
            ItemStack copy = stack.copy();
            copy.setCount(Math.min(menu.getInputSlot().getItem().getCount(), stack.getMaxStackSize()));
            return copy;
        }
        return stack;
    }
}
