package com.leclowndu93150.chisel.inventory;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

/**
 * Container/Menu for the hitech (iChisel) GUI.
 * Based on the original Chisel mod's ContainerChiselHitech.
 */
public class HitechChiselMenu extends ChiselMenu {

    // HiTech chisel has 9x7 selection grid = 63 slots
    public static final int HITECH_SELECTION_ROWS = 7;
    public static final int HITECH_SELECTION_COLS = 9;
    public static final int HITECH_SELECTION_SIZE = HITECH_SELECTION_ROWS * HITECH_SELECTION_COLS;

    // GUI positions - exact same as original
    public static final int HITECH_SELECTION_LEFT = 88;
    public static final int HITECH_SELECTION_TOP = 8;
    public static final int HITECH_INPUT_X = -1000; // Hidden off-screen
    public static final int HITECH_INPUT_Y = 0;
    public static final int HITECH_PLAYER_INV_LEFT = 88;
    public static final int HITECH_PLAYER_INV_TOP = 138;
    public static final int HITECH_HOTBAR_TOP = 196;

    // Selection tracking
    private int selectedSlot = -1;
    private int targetSlot = -1;

    // Supplier for menu type - set by ChiselMenus to avoid circular dependency
    public static Supplier<MenuType<HitechChiselMenu>> MENU_TYPE_SUPPLIER;

    // Client constructor
    public HitechChiselMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    // Server constructor
    public HitechChiselMenu(int containerId, Inventory playerInv, InteractionHand hand) {
        super(MENU_TYPE_SUPPLIER != null ? MENU_TYPE_SUPPLIER.get() : null, containerId, playerInv, hand, HITECH_SELECTION_SIZE);
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

    public int getSelectedSlot() {
        return selectedSlot;
    }

    public void setSelectedSlot(int slot) {
        this.selectedSlot = slot;
    }

    public int getTargetSlot() {
        return targetSlot;
    }

    public void setTargetSlot(int slot) {
        this.targetSlot = slot;
    }

    /**
     * Gets the item in the target slot for the "chisel all" functionality.
     */
    public ItemStack getTargetItem() {
        if (targetSlot >= 0 && targetSlot < getSelectionSize()) {
            return getSlot(targetSlot).getItem();
        }
        return ItemStack.EMPTY;
    }
}
