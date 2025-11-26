package com.leclowndu93150.chisel.inventory;

import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.item.ItemChisel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
 * Container/Menu for the standard chisel GUI..
 */
public class ChiselMenu extends AbstractContainerMenu {

    public static final int SELECTION_ROWS = 6;
    public static final int SELECTION_COLS = 10;
    public static final int SELECTION_SIZE = SELECTION_ROWS * SELECTION_COLS;

    public static final int SELECTION_LEFT = 62;
    public static final int SELECTION_TOP = 8;
    public static final int INPUT_X = 24;
    public static final int INPUT_Y = 24;
    public static final int PLAYER_INV_LEFT = 71;
    public static final int PLAYER_INV_TOP = 120;
    public static final int HOTBAR_TOP = 178;

    protected final InventoryChiselSelection inventoryChisel;
    protected final Inventory inventoryPlayer;
    protected final InteractionHand hand;
    protected final int chiselSlot;
    protected final ItemStack chisel;
    protected SlotChiselInput inputSlot;

    private ClickType currentClickType;

    public static Supplier<MenuType<ChiselMenu>> MENU_TYPE_SUPPLIER;

    public ChiselMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    public ChiselMenu(int containerId, Inventory playerInv, InteractionHand hand) {
        this(MENU_TYPE_SUPPLIER != null ? MENU_TYPE_SUPPLIER.get() : null, containerId, playerInv, hand, SELECTION_SIZE);
    }

    protected ChiselMenu(@Nullable MenuType<?> type, int containerId, Inventory playerInv, InteractionHand hand, int selectionSize) {
        super(type, containerId);
        this.inventoryPlayer = playerInv;
        this.hand = hand;
        this.chiselSlot = hand == InteractionHand.MAIN_HAND ? playerInv.selected : playerInv.getContainerSize() - 1;
        this.chisel = playerInv.getItem(chiselSlot);
        this.inventoryChisel = new InventoryChiselSelection(this, selectionSize);

        addSelectionSlots(selectionSize);
        this.inputSlot = new SlotChiselInput(this, inventoryChisel, selectionSize, INPUT_X, INPUT_Y);
        addSlot(inputSlot);
        addPlayerInventory(playerInv);

        if (!chisel.isEmpty() && chisel.getItem() instanceof ItemChisel itemChisel) {
            ItemStack storedTarget = itemChisel.getTarget(chisel);
            if (!storedTarget.isEmpty()) {
                inventoryChisel.setItem(selectionSize, storedTarget.copy());
            }
        }

        inventoryChisel.updateItems();
    }

    protected void addSelectionSlots(int size) {
        int cols = getSelectionCols();
        for (int i = 0; i < size; i++) {
            int x = getSelectionLeft() + (i % cols) * 18;
            int y = getSelectionTop() + (i / cols) * 18;
            addSlot(new SlotChiselSelection(this, inventoryChisel, i, x, y));
        }
    }

    protected void addPlayerInventory(Inventory playerInv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, getPlayerInvLeft() + col * 18, getPlayerInvTop() + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, getPlayerInvLeft() + col * 18, getHotbarTop()));
        }
    }

    protected int getSelectionLeft() { return SELECTION_LEFT; }
    protected int getSelectionTop() { return SELECTION_TOP; }
    protected int getSelectionCols() { return SELECTION_COLS; }
    protected int getPlayerInvLeft() { return PLAYER_INV_LEFT; }
    protected int getPlayerInvTop() { return PLAYER_INV_TOP; }
    protected int getHotbarTop() { return HOTBAR_TOP; }

    @Override
    public void clicked(int slotId, int dragType, ClickType clickTypeIn, Player player) {
        if (clickTypeIn != ClickType.QUICK_CRAFT && slotId >= 0) {
            int clickedPlayerSlot = slotId - inventoryChisel.getContainerSize() - 27;

            if (clickedPlayerSlot == chiselSlot || (clickTypeIn == ClickType.SWAP && dragType == chiselSlot)) {
                return;
            }
        }

        this.currentClickType = clickTypeIn;
        super.clicked(slotId, dragType, clickTypeIn, player);
    }

    public ClickType getCurrentClickType() {
        return currentClickType;
    }

    @Override
    public void removed(Player player) {
        inventoryChisel.clearItems();
        super.removed(player);
    }

    @Override
    public boolean stillValid(Player player) {
        ItemStack held = inventoryPlayer.getItem(chiselSlot);
        return !held.isEmpty() && held.getItem() instanceof IChiselItem chiselItem &&
               chiselItem.canOpenGui(player.level(), player, hand);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIdx) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIdx);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();

            int selectionSize = getSelectionSize();
            int inputSlotIndex = selectionSize;
            int playerInvStart = inputSlotIndex + 1;
            int playerInvEnd = playerInvStart + 36;

            if (slotIdx > selectionSize) {
                if (CarvingHelper.canChisel(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, inputSlotIndex, inputSlotIndex + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            } else {
                if (slotIdx < selectionSize && !slotStack.isEmpty()) {
                    SlotChiselSelection selectSlot = (SlotChiselSelection) slot;
                    ItemStack check = SlotChiselSelection.craft(this, player, slotStack, true);
                    if (check.isEmpty()) {
                        return ItemStack.EMPTY;
                    }
                    if (!this.moveItemStackTo(check, playerInvStart, playerInvEnd, true)) {
                        return ItemStack.EMPTY;
                    }
                    ItemStack result = SlotChiselSelection.craft(this, player, slotStack, false);
                    if (!result.isEmpty()) {
                        // Play sound based on the block being crafted
                        Block targetBlock = Blocks.AIR;
                        if (slotStack.getItem() instanceof BlockItem blockItem) {
                            targetBlock = blockItem.getBlock();
                        }
                        CarvingHelper.playChiselSound(player.level(), player, targetBlock);
                    }
                    inventoryChisel.setStackInSpecialSlot(inventoryChisel.getStackInSpecialSlot());
                } else if (!this.moveItemStackTo(slotStack, playerInvStart, playerInvEnd, true)) {
                    return ItemStack.EMPTY;
                }
            }

            boolean clearSlot = slotIdx >= selectionSize || inventoryChisel.getStackInSpecialSlot().isEmpty();

            slot.onQuickCraft(slotStack, itemstack);

            if (slotStack.isEmpty()) {
                if (clearSlot) {
                    slot.set(ItemStack.EMPTY);
                }
            } else {
                slot.setChanged();
            }

            inventoryChisel.updateItems();

            if (slotStack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            if (slotIdx >= selectionSize) {
                slot.onTake(player, slotStack);
            }
            if (slotStack.isEmpty()) {
                if (clearSlot) {
                    slot.set(ItemStack.EMPTY);
                }
                return ItemStack.EMPTY;
            } else {
                if (!clearSlot) {
                    slot.set(itemstack);
                }
                return slotStack;
            }
        }
        return itemstack;
    }

    public void onChiselSlotChanged() {
        if (chisel.getItem() instanceof ItemChisel itemChisel) {
            itemChisel.setTarget(chisel, inventoryChisel.getStackInSpecialSlot());
        }
    }

    public void onChiselBroken() {
        if (!inventoryPlayer.player.level().isClientSide) {
            inventoryPlayer.player.drop(inventoryChisel.getStackInSpecialSlot(), false);
            inventoryChisel.setStackInSpecialSlot(ItemStack.EMPTY);
        }
    }

    public SlotChiselInput getInputSlot() {
        return inputSlot;
    }

    public InventoryChiselSelection getInventoryChisel() {
        return inventoryChisel;
    }

    public Inventory getInventoryPlayer() {
        return inventoryPlayer;
    }

    public int getSelectionSize() {
        return SELECTION_SIZE;
    }

    public ItemStack getChisel() {
        return chisel;
    }

    public int getChiselSlot() {
        return chiselSlot;
    }

    public InteractionHand getHand() {
        return hand;
    }
}
