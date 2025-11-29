package com.leclowndu93150.chisel.block.entity;

import com.leclowndu93150.chisel.ChiselConfig;
import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.init.ChiselBlockEntities;
import com.leclowndu93150.chisel.inventory.AutoChiselMenu;
import com.leclowndu93150.chisel.network.client.AutoChiselFXPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.leclowndu93150.chisel.network.ChiselNetwork;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.Nullable;

public class AutoChiselBlockEntity extends BlockEntity implements MenuProvider, Nameable {

    public static final int INPUT_SLOTS = 12;
    public static final int OUTPUT_SLOTS = 12;

    private static final int MAX_PROGRESS = 1024;
    private static final int BASE_PROGRESS = 16;
    private static final int SPEEDUP_PROGRESS = 64;
    private static final int POWER_PER_TICK = 20;
    private static final int MAX_ENERGY = 10000;

    private final ItemStackHandler chiselSlot = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return stack.getItem() instanceof IChiselItem;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ItemStackHandler targetSlot = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return CarvingHelper.canChisel(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ItemStackHandler inputInv = new ItemStackHandler(INPUT_SLOTS) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return CarvingHelper.canChisel(stack);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final ItemStackHandler outputInv = new ItemStackHandler(OUTPUT_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private final EnergyStorage energyStorage = new EnergyStorage(MAX_ENERGY, POWER_PER_TICK * 2, POWER_PER_TICK);

    private int progress = 0;
    private int sourceSlot = -1;
    private Component customName;

    public final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case AutoChiselMenu.ACTIVE -> sourceSlot >= 0 ? 1 : 0;
                case AutoChiselMenu.PROGRESS -> progress;
                case AutoChiselMenu.MAX_PROGRESS -> MAX_PROGRESS;
                case AutoChiselMenu.ENERGY -> energyStorage.getEnergyStored();
                case AutoChiselMenu.MAX_ENERGY -> energyStorage.getMaxEnergyStored();
                case AutoChiselMenu.ENERGY_USE -> getUsagePerTick();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 6;
        }
    };

    public AutoChiselBlockEntity(BlockPos pos, BlockState state) {
        super(ChiselBlockEntities.AUTO_CHISEL.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }

        if (energyStorage.getEnergyStored() == 0 && ChiselConfig.autoChiselNeedsPower) {
            return;
        }

        ItemStack target = targetSlot.getStackInSlot(0);
        ItemStack chisel = chiselSlot.getStackInSlot(0);
        ItemStack source = sourceSlot >= 0 ? inputInv.getStackInSlot(sourceSlot) : ItemStack.EMPTY;

        TagKey<Item> targetGroup = target.isEmpty() || chisel.isEmpty() ? null : CarvingHelper.getCarvingGroupForItem(target);

        if (chisel.isEmpty() || targetGroup == null) {
            sourceSlot = -1;
            progress = 0;
            return;
        }

        if (!source.isEmpty()) {
            TagKey<Item> sourceGroup = CarvingHelper.getCarvingGroupForItem(source);
            if (sourceGroup == null || !sourceGroup.equals(targetGroup)) {
                source = ItemStack.EMPTY;
            }
        }

        if ((sourceSlot < 0 && level.getGameTime() % 20 == 0) || sourceSlot >= 0) {
            if (source.isEmpty()) {
                sourceSlot = -1;
            }

            ItemStack result = new ItemStack(target.getItem());
            if (!source.isEmpty()) {
                result.setCount(source.getCount());
            }

            if (source.isEmpty() || canOutput(result)) {
                for (int i = 0; sourceSlot < 0 && i < inputInv.getSlots(); i++) {
                    ItemStack stack = inputInv.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        TagKey<Item> stackGroup = CarvingHelper.getCarvingGroupForItem(stack);
                        if (stackGroup != null && stackGroup.equals(targetGroup)) {
                            result.setCount(stack.getCount());
                            if (canOutput(result)) {
                                sourceSlot = i;
                                source = stack.copy();
                            }
                        }
                    }
                }
            } else {
                sourceSlot = -1;
            }
        }

        if (sourceSlot >= 0) {
            source = inputInv.getStackInSlot(sourceSlot);

            if (!ItemStack.isSameItem(source, target)) {
                if (progress < MAX_PROGRESS) {
                    if (!ChiselConfig.autoChiselNeedsPower) {
                        progress = Math.min(MAX_PROGRESS, progress + BASE_PROGRESS);
                    }

                    int toUse = Math.min(MAX_PROGRESS - progress, getPowerProgressPerTick());
                    int powerToUse = getUsagePerTick();

                    if (toUse > 0 && powerToUse > 0) {
                        if (ChiselConfig.autoChiselPowered) {
                            int used = energyStorage.extractEnergy(powerToUse, false);
                            progress += (int) (toUse * ((float) used / powerToUse));
                        } else {
                            progress += toUse;
                        }
                    }
                } else {
                    ItemStack result = new ItemStack(target.getItem(), source.getCount());

                    inputInv.setStackInSlot(sourceSlot, ItemStack.EMPTY);

                    if (ChiselConfig.allowChiselDamage && chisel.isDamageableItem()) {
                        chisel.setDamageValue(chisel.getDamageValue() + 1);
                        if (chisel.getDamageValue() >= chisel.getMaxDamage()) {
                            chiselSlot.setStackInSlot(0, ItemStack.EMPTY);
                        }
                    }

                    mergeOutput(result);

                    if (level instanceof ServerLevel serverLevel) {
                        BlockState resultState = result.getItem() instanceof BlockItem blockItem
                                ? blockItem.getBlock().defaultBlockState()
                                : Blocks.STONE.defaultBlockState();
                        // unchecked cast
                        ChiselNetwork.sendToAllTrackingChunk((LevelChunk) serverLevel.getChunk(worldPosition),
                                new AutoChiselFXPacket(worldPosition, chisel.copy(), resultState));
                    }

                    sourceSlot = (sourceSlot + 1) % inputInv.getSlots();
                    progress = 0;
                }
            } else {
                inputInv.setStackInSlot(sourceSlot, ItemStack.EMPTY);
                mergeOutput(source);
            }
        } else {
            progress = 0;
        }
    }

    private float getSpeedFactor() {
        return ChiselConfig.autoChiselPowered ? (float) energyStorage.getEnergyStored() / energyStorage.getMaxEnergyStored() : 1;
    }

    private int getPowerProgressPerTick() {
        int maxPerTick = ChiselConfig.autoChiselNeedsPower ? (BASE_PROGRESS + SPEEDUP_PROGRESS) : SPEEDUP_PROGRESS;
        return (int) Math.ceil(getSpeedFactor() * maxPerTick);
    }

    private int getUsagePerTick() {
        return (int) Math.ceil(getSpeedFactor() * POWER_PER_TICK);
    }

    private boolean canOutput(ItemStack stack) {
        ItemStack remaining = stack.copy();
        for (int i = 0; i < outputInv.getSlots(); i++) {
            remaining = outputInv.insertItem(i, remaining, true);
            if (remaining.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void mergeOutput(ItemStack stack) {
        for (int i = 0; i < outputInv.getSlots() && !stack.isEmpty(); i++) {
            stack = outputInv.insertItem(i, stack, false);
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        for (int i = 0; i < inputInv.getSlots(); i++) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), inputInv.getStackInSlot(i));
        }
        for (int i = 0; i < outputInv.getSlots(); i++) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), outputInv.getStackInSlot(i));
        }
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), chiselSlot.getStackInSlot(0));
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), targetSlot.getStackInSlot(0));
    }

    public ItemStackHandler getChiselSlot() {
        return chiselSlot;
    }

    public ItemStackHandler getTargetSlot() {
        return targetSlot;
    }

    public ItemStackHandler getInputInv() {
        return inputInv;
    }

    public ItemStackHandler getOutputInv() {
        return outputInv;
    }

    public EnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("chisel", chiselSlot.serializeNBT());
        tag.put("target", targetSlot.serializeNBT());
        tag.put("input", inputInv.serializeNBT());
        tag.put("output", outputInv.serializeNBT());
        tag.putInt("energy", energyStorage.getEnergyStored());
        tag.putInt("progress", progress);
        tag.putInt("source", sourceSlot);
        if (customName != null) {
            tag.putString("customName", Component.Serializer.toJson(customName));
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        chiselSlot.deserializeNBT(tag.getCompound("chisel"));
        targetSlot.deserializeNBT(tag.getCompound("target"));
        inputInv.deserializeNBT(tag.getCompound("input"));
        outputInv.deserializeNBT(tag.getCompound("output"));
        energyStorage.receiveEnergy(tag.getInt("energy") - energyStorage.getEnergyStored(), false);
        progress = tag.getInt("progress");
        sourceSlot = tag.getInt("source");
        if (tag.contains("customName")) {
            customName = Component.Serializer.fromJson(tag.getString("customName"));
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public Component getDisplayName() {
        return hasCustomName() ? getCustomName() : getName();
    }

    @Override
    public Component getName() {
        return customName != null ? customName : getBlockState().getBlock().getName();
    }

    @Override
    public boolean hasCustomName() {
        return customName != null;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return customName;
    }

    public void setCustomName(Component name) {
        this.customName = name;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player player) {
        return new AutoChiselMenu(containerId, playerInv, this, dataAccess, ContainerLevelAccess.create(level, worldPosition));
    }
    
    private final LazyOptional<IItemHandler> itemHandlerTop = LazyOptional.of(() -> inputInv);
    private final LazyOptional<IItemHandler> itemHandlerBottom = LazyOptional.of(() -> outputInv);
    private final LazyOptional<IItemHandler> itemHandlerSides = LazyOptional.of(() ->
        new CombinedInvWrapper(inputInv, outputInv));
    private final LazyOptional<IItemHandler> itemHandlerAll = LazyOptional.of(() ->
        new CombinedInvWrapper(inputInv, outputInv, chiselSlot, targetSlot));
    private final LazyOptional<net.minecraftforge.energy.IEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            if (side == Direction.DOWN) {
                return itemHandlerBottom.cast();
            } else if (side == Direction.UP) {
                return itemHandlerTop.cast();
            } else if (side == null) {
                return itemHandlerAll.cast();
            } else {
                return itemHandlerSides.cast();
            }
        } else if (cap == ForgeCapabilities.ENERGY) {
            return energyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerTop.invalidate();
        itemHandlerBottom.invalidate();
        itemHandlerSides.invalidate();
        itemHandlerAll.invalidate();
        energyHandler.invalidate();
    }
}
