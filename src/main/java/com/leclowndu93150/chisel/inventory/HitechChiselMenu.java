package com.leclowndu93150.chisel.inventory;

import com.google.common.collect.ImmutableList;
import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.item.ItemChisel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Container/Menu for the hitech (iChisel) GUI.
 * Unlike the standard chisel, this doesn't have a visible input slot.
 * Instead, players click on items in their inventory to select them,
 * then click on variants to set the target, then click the "Chisel" button.
 */
public class HitechChiselMenu extends ChiselMenu {

    public static final int HITECH_SELECTION_ROWS = 7;
    public static final int HITECH_SELECTION_COLS = 9;
    public static final int HITECH_SELECTION_SIZE = HITECH_SELECTION_ROWS * HITECH_SELECTION_COLS;

    public static final int HITECH_SELECTION_LEFT = 88;
    public static final int HITECH_SELECTION_TOP = 8;
    public static final int HITECH_INPUT_X = -1000;
    public static final int HITECH_INPUT_Y = 0;
    public static final int HITECH_PLAYER_INV_LEFT = 88;
    public static final int HITECH_PLAYER_INV_TOP = 138;
    public static final int HITECH_HOTBAR_TOP = 196;

    @Nullable
    private Slot selection;
    @Nullable
    private Slot target;
    private List<Slot> selectionDuplicates = ImmutableList.of();
    @Nullable
    private TagKey<Item> currentGroup;

    public static Supplier<MenuType<HitechChiselMenu>> MENU_TYPE_SUPPLIER;

    public HitechChiselMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, buf != null && buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    public HitechChiselMenu(int containerId, Inventory playerInv, InteractionHand hand) {
        super(MENU_TYPE_SUPPLIER != null ? MENU_TYPE_SUPPLIER.get() : null, containerId, playerInv, hand, HITECH_SELECTION_SIZE);

        if (!chisel.isEmpty() && chisel.getItem() instanceof ItemChisel itemChisel) {
            int selectionSlot = itemChisel.getSelectionSlot(chisel);
            if (selectionSlot >= inventoryChisel.getContainerSize()) {
                int playerSlotIndex = selectionSlot - inventoryChisel.getContainerSize();
                if (playerSlotIndex >= 0 && playerSlotIndex < 36) {
                    setSelection(getSlot(selectionSlot));
                }
            }

            int targetSlot = itemChisel.getTargetSlot(chisel);
            if (targetSlot >= 0 && targetSlot < inventoryChisel.size) {
                setTarget(getSlot(targetSlot));
            }
        }
    }

    @Override
    protected int getSelectionLeft() { return HITECH_SELECTION_LEFT; }

    @Override
    protected int getSelectionTop() { return HITECH_SELECTION_TOP; }

    @Override
    protected int getSelectionCols() { return HITECH_SELECTION_COLS; }

    @Override
    protected int getPlayerInvLeft() { return HITECH_PLAYER_INV_LEFT; }

    @Override
    protected int getPlayerInvTop() { return HITECH_PLAYER_INV_TOP; }

    @Override
    protected int getHotbarTop() { return HITECH_HOTBAR_TOP; }

    @Override
    public int getSelectionSize() {
        return HITECH_SELECTION_SIZE;
    }

    /**
     * Sets the target slot (in the variant grid).
     */
    public void setTarget(@Nullable Slot slot) {
        this.target = slot;
        // Save to chisel data
        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            itemChisel.setTargetSlot(chisel, slot != null ? slot.index : -1);
        }
    }

    /**
     * Sets the selection slot (in player inventory).
     * This updates the variant grid to show available chiseling options.
     */
    public void setSelection(@Nullable Slot slot) {
        this.selection = slot;

        if (slot == null || !slot.hasItem()) {
            currentGroup = null;
            selectionDuplicates = ImmutableList.of();
            setTarget(null);
            inventoryChisel.setStackInSpecialSlot(ItemStack.EMPTY);
        } else {
            ImmutableList.Builder<Slot> builder = ImmutableList.builder();
            int playerInvStart = inventoryChisel.getContainerSize();
            for (int i = playerInvStart; i < slots.size(); i++) {
                Slot s = getSlot(i);
                if (slot != s && ItemStack.isSameItem(slot.getItem(), s.getItem())) {
                    builder.add(s);
                }
            }
            selectionDuplicates = builder.build();

            TagKey<Item> group = CarvingHelper.getCarvingGroupForItem(slot.getItem());
            if (currentGroup != null && group != currentGroup) {
                setTarget(null);
            }
            currentGroup = group;

            inventoryChisel.setStackInSpecialSlot(slot.getItem().copy());
        }

        inventoryChisel.updateItems();

        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            itemChisel.setSelectionSlot(chisel, slot != null ? slot.index : -1);
        }
    }

    @Nullable
    public Slot getSelection() {
        return selection;
    }

    @Nullable
    public Slot getTarget() {
        return target;
    }

    public List<Slot> getSelectionDuplicates() {
        return selectionDuplicates;
    }

    @Nullable
    public ItemStack getSelectionStack() {
        return selection != null ? selection.getItem() : ItemStack.EMPTY;
    }

    public ItemStack getTargetItem() {
        return target != null ? target.getItem() : ItemStack.EMPTY;
    }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (slotId >= 0 && slotId < slots.size()) {
            Slot slot = getSlot(slotId);

            if (slotId < inventoryChisel.size) {
                setTarget(slot);
            } else if (slotId > inventoryChisel.size) {
                if (dragType == 1) {
                    ItemStack toFind = slot.getItem();
                    if (!toFind.isEmpty()) {
                        for (int i = 0; i < inventoryChisel.size; i++) {
                            if (ItemStack.isSameItem(toFind, inventoryChisel.getItem(i))) {
                                setTarget(getSlot(i));
                                break;
                            }
                        }
                    }
                } else {
                    if (slot.hasItem() && CarvingHelper.canChisel(slot.getItem())) {
                        setSelection(slot);
                    }
                }
            }
        }
    }

    @Override
    public void removed(Player player) {
        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            itemChisel.setTarget(chisel, getTargetItem());
        }
        inventoryChisel.setStackInSpecialSlot(ItemStack.EMPTY);
        super.removed(player);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIdx) {
        return ItemStack.EMPTY;
    }

    /**
     * Performs the chisel operation on the selected slots.
     * @param slots Array of player inventory slot indices to chisel
     */
    public void chiselSlots(int[] slots) {
        if (target == null || !target.hasItem()) return;
        if (selection == null || !selection.hasItem()) return;
        if (!(chisel.getItem() instanceof IChiselItem chiselItem)) return;

        ItemStack targetStack = target.getItem();
        boolean chiseledAny = false;
        Block targetBlock = targetStack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock() : Blocks.AIR;

        for (int slotIndex : slots) {
            Slot slot = getSlot(slotIndex);
            if (slot == null || !slot.hasItem()) continue;

            ItemStack source = slot.getItem();
            if (!CarvingHelper.canChisel(source)) continue;

            TagKey<Item> sourceGroup = CarvingHelper.getCarvingGroupForItem(source);
            TagKey<Item> targetGroup = CarvingHelper.getCarvingGroupForItem(targetStack);
            if (sourceGroup == null || !sourceGroup.equals(targetGroup)) continue;

            ItemStack converted = targetStack.copy();
            converted.setCount(source.getCount());

            slot.set(converted);
            chiseledAny = true;

            chiselItem.onChisel(inventoryPlayer.player.level(), inventoryPlayer.player, chisel, targetBlock);

            if (chisel.isEmpty()) {
                inventoryPlayer.setItem(chiselSlot, ItemStack.EMPTY);
                break;
            }
        }

        if (selection != null && !selection.hasItem()) {
            if (!selectionDuplicates.isEmpty()) {
                setSelection(selectionDuplicates.get(0));
            } else {
                setSelection(null);
            }
        } else if (selection != null) {
            setSelection(selection);
        }

        broadcastChanges();
    }
}
