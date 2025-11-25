package com.leclowndu93150.chisel.inventory;

import com.leclowndu93150.chisel.carving.CarvingHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

/**
 * Container/Menu for the standard chisel GUI.
 * Based on the original Chisel mod's ChiselContainer.
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

    private final Container inputContainer;
    private final InventoryChiselSelection selectionInventory;
    private final Player player;
    private final InteractionHand hand;
    private final ItemStack chiselStack;

    private SlotChiselInput inputSlot;
    public static Supplier<MenuType<ChiselMenu>> MENU_TYPE_SUPPLIER;

    public ChiselMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv, buf.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    public ChiselMenu(int containerId, Inventory playerInv, InteractionHand hand) {
        this(MENU_TYPE_SUPPLIER != null ? MENU_TYPE_SUPPLIER.get() : null, containerId, playerInv, hand, SELECTION_SIZE);
    }

    protected ChiselMenu(@Nullable MenuType<?> type, int containerId, Inventory playerInv, InteractionHand hand, int selectionSize) {
        super(type, containerId);
        this.player = playerInv.player;
        this.hand = hand;
        this.chiselStack = player.getItemInHand(hand);
        this.inputContainer = new SimpleContainer(1) {
            @Override
            public void setChanged() {
                super.setChanged();
                slotsChanged(this);
            }
        };
        this.selectionInventory = new InventoryChiselSelection(selectionSize);

        addSelectionSlots(selectionSize);
        this.inputSlot = new SlotChiselInput(inputContainer, 0, INPUT_X, INPUT_Y, this::onInputChanged);
        addSlot(inputSlot);
        addPlayerInventory(playerInv);
    }

    protected void addSelectionSlots(int size) {
        int cols = getSelectionCols();
        for (int i = 0; i < size; i++) {
            int x = getSelectionLeft() + (i % cols) * 18;
            int y = getSelectionTop() + (i / cols) * 18;
            addSlot(new SlotChiselSelection(this, selectionInventory, i, x, y, () -> chiselStack));
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

    public void onInputChanged(ItemStack stack) {
        selectionInventory.clearSelection();
        if (stack.isEmpty()) return;

        TagKey<Item> group = CarvingHelper.getCarvingGroupForItem(stack);
        if (group == null) return;

        List<Item> variations = CarvingHelper.getItemsInGroup(group);
        int slot = 0;
        for (Item item : variations) {
            if (slot >= selectionInventory.getContainerSize() - 1) break;
            if (item != stack.getItem()) {
                selectionInventory.setItem(slot, new ItemStack(item));
                slot++;
            }
        }
        selectionInventory.setActiveVariations(slot);
    }

    @Override
    public void slotsChanged(Container container) {
        super.slotsChanged(container);
        if (container == inputContainer) {
            onInputChanged(inputContainer.getItem(0));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            int selectionEnd = getSelectionSize();
            int inputSlotIndex = selectionEnd;
            int playerInvStart = inputSlotIndex + 1;
            int playerInvEnd = playerInvStart + 36;

            if (slotIndex < selectionEnd) {
                // From selection slot - craft and move to player inventory
                if (!moveItemStackTo(slotStack, playerInvStart, playerInvEnd, false)) {
                    return ItemStack.EMPTY;
                }
                slot.onTake(player, slotStack);
            } else if (slotIndex == inputSlotIndex) {
                // From input slot - move to player inventory
                if (!moveItemStackTo(slotStack, playerInvStart, playerInvEnd, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // From player inventory - try to move to input slot
                if (CarvingHelper.canChisel(slotStack)) {
                    if (!moveItemStackTo(slotStack, inputSlotIndex, inputSlotIndex + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return result;
    }

    @Override
    public boolean stillValid(Player player) {
        return player.getItemInHand(hand).getItem() == chiselStack.getItem();
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        // Return input items to player
        if (!player.level().isClientSide) {
            ItemStack input = inputContainer.removeItemNoUpdate(0);
            if (!input.isEmpty()) {
                if (!player.getInventory().add(input)) {
                    player.drop(input, false);
                }
            }
        }
    }

    public SlotChiselInput getInputSlot() {
        return inputSlot;
    }

    public InventoryChiselSelection getSelectionInventory() {
        return selectionInventory;
    }

    public int getSelectionSize() {
        return SELECTION_SIZE;
    }

    public ItemStack getChiselStack() {
        return chiselStack;
    }

    public InteractionHand getHand() {
        return hand;
    }
}
