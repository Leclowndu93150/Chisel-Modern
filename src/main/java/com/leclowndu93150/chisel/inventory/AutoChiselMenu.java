package com.leclowndu93150.chisel.inventory;

import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.block.entity.AutoChiselBlockEntity;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class AutoChiselMenu extends AbstractContainerMenu {

    public static final int ACTIVE = 0;
    public static final int PROGRESS = 1;
    public static final int MAX_PROGRESS = 2;
    public static final int ENERGY = 3;
    public static final int MAX_ENERGY = 4;
    public static final int ENERGY_USE = 5;

    public final Inventory playerInventory;
    public final int chiselSlot;
    public final int targetSlot;

    private final int beginInputSlots;
    private final int endInputSlots;
    private final int beginOutputSlots;
    private final int endOutputSlots;
    private final int beginPlayerSlots;
    private final int endPlayerSlots;

    private final ContainerData data;
    private final ContainerLevelAccess access;

    public AutoChiselMenu(int containerId, Inventory playerInv, FriendlyByteBuf buf) {
        this(containerId, playerInv,
                new ItemStackHandler(1),
                new ItemStackHandler(1),
                new ItemStackHandler(AutoChiselBlockEntity.INPUT_SLOTS),
                new ItemStackHandler(AutoChiselBlockEntity.OUTPUT_SLOTS),
                new SimpleContainerData(6),
                ContainerLevelAccess.NULL);
    }

    public AutoChiselMenu(int containerId, Inventory playerInv, AutoChiselBlockEntity blockEntity,
                          ContainerData data, ContainerLevelAccess access) {
        this(containerId, playerInv,
                blockEntity.getChiselSlot(),
                blockEntity.getTargetSlot(),
                blockEntity.getInputInv(),
                blockEntity.getOutputInv(),
                data, access);
    }

    public AutoChiselMenu(int containerId, Inventory playerInv,
                          IItemHandler chisel, IItemHandler target,
                          IItemHandler input, IItemHandler output,
                          ContainerData data, ContainerLevelAccess access) {
        super(ChiselMenus.AUTO_CHISEL_MENU.get(), containerId);
        this.playerInventory = playerInv;
        this.data = data;
        this.access = access;

        int yStart = 19;

        chiselSlot = 0;
        addSlot(new SlotItemHandler(chisel, 0, 80, yStart + 9) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof IChiselItem;
            }
        });

        targetSlot = 1;
        addSlot(new SlotItemHandler(target, 0, 80, 54 + yStart - 9) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return CarvingHelper.canChisel(stack);
            }
        });

        beginInputSlots = slots.size();
        for (int i = 0; i < input.getSlots(); i++) {
            int x = 8 + 18 * (i % 3);
            int y = yStart + 18 * (i / 3);
            addSlot(new SlotItemHandler(input, i, x, y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return CarvingHelper.canChisel(stack);
                }
            });
        }
        endInputSlots = beginOutputSlots = slots.size();

        for (int i = 0; i < output.getSlots(); i++) {
            int x = 8 + 108 + 18 * (i % 3);
            int y = yStart + 18 * (i / 3);
            addSlot(new SlotItemHandler(output, i, x, y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }
            });
        }

        beginPlayerSlots = endOutputSlots = slots.size();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 118 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInv, col, 8 + col * 18, 176));
        }

        endPlayerSlots = slots.size();

        addDataSlots(data);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ChiselBlocks.AUTO_CHISEL.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            if (index >= beginOutputSlots && index < endOutputSlots) {
                if (!this.moveItemStackTo(slotStack, beginPlayerSlots, endPlayerSlots, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(slotStack, result);
            } else if (index >= beginPlayerSlots) {
                if (CarvingHelper.canChisel(slotStack)) {
                    if (!this.moveItemStackTo(slotStack, targetSlot, targetSlot + 1, false)) {
                        if (!this.moveItemStackTo(slotStack, beginInputSlots, endInputSlots, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else if (slotStack.getItem() instanceof IChiselItem) {
                    if (!this.moveItemStackTo(slotStack, chiselSlot, chiselSlot + 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= beginPlayerSlots && index < endPlayerSlots - 9) {
                    if (!this.moveItemStackTo(slotStack, endPlayerSlots - 9, endPlayerSlots, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= endPlayerSlots - 9 && index < endPlayerSlots) {
                    if (!this.moveItemStackTo(slotStack, beginPlayerSlots, endPlayerSlots - 9, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(slotStack, beginPlayerSlots, endPlayerSlots, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
        }

        return result;
    }

    public boolean isActive() {
        return data.get(ACTIVE) > 0;
    }

    public int getProgressScaled(int barLength) {
        int progress = data.get(PROGRESS);
        int maxProgress = data.get(MAX_PROGRESS);
        return maxProgress > 0 ? (int) (((float) progress / maxProgress) * barLength) : 0;
    }

    public boolean hasEnergy() {
        return getEnergy() > 0;
    }

    public int getEnergy() {
        return data.get(ENERGY);
    }

    public int getMaxEnergy() {
        return data.get(MAX_ENERGY);
    }

    public int getEnergyScaled(int barLength) {
        int energy = data.get(ENERGY);
        int maxEnergy = data.get(MAX_ENERGY);
        return maxEnergy > 0 ? (int) (((float) energy / maxEnergy) * barLength) : 0;
    }

    public int getUsagePerTick() {
        return data.get(ENERGY_USE);
    }
}
