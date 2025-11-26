package com.leclowndu93150.chisel.inventory;

import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

/**
 * Selection slot for the chisel GUI that displays available variations.
 * When clicked, crafts items from the input slot into the selected variation.
 */
public class SlotChiselSelection extends Slot {

    private final ChiselMenu container;

    public SlotChiselSelection(ChiselMenu container, InventoryChiselSelection inv, int slot, int x, int y) {
        super(inv, slot, x, y);
        this.container = container;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public boolean mayPickup(Player player) {
        return player.containerMenu.getCarried().isEmpty();
    }

    /**
     * Performs the crafting operation.
     *
     * @param container The chisel container
     * @param player The player performing the craft
     * @param target The target item type to craft into
     * @param simulate If true, only simulate the craft without modifying items
     * @return The crafted item stack, or empty if craft failed
     */
    public static ItemStack craft(ChiselMenu container, Player player, ItemStack target, boolean simulate) {
        ItemStack source = container.getInventoryChisel().getStackInSpecialSlot();
        ItemStack chisel = container.getChisel();

        if (simulate) {
            target = target.copy();
            source = source.isEmpty() ? ItemStack.EMPTY : source.copy();
            chisel = chisel.copy();
        }

        ItemStack result = ItemStack.EMPTY;
        if (!chisel.isEmpty() && !source.isEmpty()) {
            if (!(chisel.getItem() instanceof IChiselItem chiselItem)) {
                return result;
            }

            if (!chiselItem.canChisel(player.level(), player, chisel, target)) {
                return result;
            }

            EquipmentSlot equipSlot = container.getHand() == InteractionHand.MAIN_HAND ?
                    EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND;
            result = chiselItem.craftItem(chisel, source, target, player, equipSlot);

            if (!simulate) {
                container.getInventoryChisel().setStackInSpecialSlot(source.isEmpty() ? ItemStack.EMPTY : source);
                container.onChiselSlotChanged();

                Block targetBlock = Blocks.AIR;
                if (target.getItem() instanceof BlockItem blockItem) {
                    targetBlock = blockItem.getBlock();
                }
                chiselItem.onChisel(player.level(), player, chisel, targetBlock);

                if (chisel.isEmpty() || chisel.getCount() == 0) {
                    container.getInventoryPlayer().setItem(container.getChiselSlot(), ItemStack.EMPTY);
                }

                if (!source.isEmpty() && !chiselItem.canChisel(player.level(), player, chisel, target)) {
                    container.onChiselBroken();
                }

                container.getInventoryChisel().updateItems();
                container.broadcastChanges();
            }
        }

        return result;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        ItemStack chisel = container.getChisel().copy();
        ItemStack result = craft(container, player, stack, false);

        if (!result.isEmpty()) {
            // Play sound based on the block being crafted
            Block targetBlock = Blocks.AIR;
            if (stack.getItem() instanceof BlockItem blockItem) {
                targetBlock = blockItem.getBlock();
            }
            CarvingHelper.playChiselSound(player.level(), player, targetBlock);

            ClickType clickType = container.getCurrentClickType();
            if (clickType != null) {
                switch (clickType) {
                    case PICKUP, PICKUP_ALL, QUICK_CRAFT, QUICK_MOVE -> container.setCarried(result);
                    case SWAP, THROW -> stack.setCount(result.getCount());
                    default -> {}
                }
            } else {
                container.setCarried(result);
            }
        } else {
            stack.setCount(0);
        }
    }
}
