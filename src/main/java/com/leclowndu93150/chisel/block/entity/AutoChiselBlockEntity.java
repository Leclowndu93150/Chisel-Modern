package com.leclowndu93150.chisel.block.entity;

import com.leclowndu93150.chisel.ChiselConfig;
import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.init.ChiselBlockEntities;
import com.leclowndu93150.chisel.inventory.AutoChiselMenu;
import com.leclowndu93150.chisel.network.AutoChiselFXPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.transfer.CombinedResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.energy.SimpleEnergyHandler;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public class AutoChiselBlockEntity extends BlockEntity implements MenuProvider, Nameable {

    public static final int INPUT_SLOTS = 12;
    public static final int OUTPUT_SLOTS = 12;

    private static final int MAX_PROGRESS = 1024;
    private static final int BASE_PROGRESS = 16;
    private static final int SPEEDUP_PROGRESS = 64;
    private static final int POWER_PER_TICK = 20;
    private static final int MAX_ENERGY = 10000;

    private final ItemStacksResourceHandler chiselSlot = new ItemStacksResourceHandler(1) {
        @Override
        public boolean isValid(int index, ItemResource resource) {
            return resource.toStack().getItem() instanceof IChiselItem;
        }

        @Override
        protected void onContentsChanged(int index, ItemStack oldContents) {
            setChanged();
        }
    };

    private final ItemStacksResourceHandler targetSlot = new ItemStacksResourceHandler(1) {
        @Override
        public boolean isValid(int index, ItemResource resource) {
            return CarvingHelper.canChisel(resource.toStack());
        }

        @Override
        protected void onContentsChanged(int index, ItemStack oldContents) {
            setChanged();
        }
    };

    private final ItemStacksResourceHandler inputInv = new ItemStacksResourceHandler(INPUT_SLOTS) {
        @Override
        public boolean isValid(int index, ItemResource resource) {
            return CarvingHelper.canChisel(resource.toStack());
        }

        @Override
        protected void onContentsChanged(int index, ItemStack oldContents) {
            setChanged();
        }
    };

    private final ItemStacksResourceHandler outputInv = new ItemStacksResourceHandler(OUTPUT_SLOTS) {
        @Override
        protected void onContentsChanged(int index, ItemStack oldContents) {
            setChanged();
        }
    };

    private final SimpleEnergyHandler energyStorage = new SimpleEnergyHandler(MAX_ENERGY, POWER_PER_TICK * 2, POWER_PER_TICK) {
        @Override
        protected void onEnergyChanged(int previousAmount) {
            setChanged();
        }
    };

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
                case AutoChiselMenu.ENERGY -> energyStorage.getAmountAsInt();
                case AutoChiselMenu.MAX_ENERGY -> energyStorage.getCapacityAsInt();
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

    private ItemStack getStack(ItemStacksResourceHandler handler, int index) {
        ItemResource resource = handler.getResource(index);
        int amount = handler.getAmountAsInt(index);
        return resource.isEmpty() ? ItemStack.EMPTY : resource.toStack(amount);
    }

    private void setStack(ItemStacksResourceHandler handler, int index, ItemStack stack) {
        handler.set(index, ItemResource.of(stack), stack.getCount());
    }

    public void tick() {
        if (level == null || level.isClientSide()) {
            return;
        }

        if (energyStorage.getAmountAsInt() == 0 && ChiselConfig.autoChiselNeedsPower) {
            return;
        }

        ItemStack target = getStack(targetSlot, 0);
        ItemStack chisel = getStack(chiselSlot, 0);
        ItemStack source = sourceSlot >= 0 ? getStack(inputInv, sourceSlot) : ItemStack.EMPTY;

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
                for (int i = 0; sourceSlot < 0 && i < inputInv.size(); i++) {
                    ItemStack stack = getStack(inputInv, i);
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
            source = getStack(inputInv, sourceSlot);

            if (!ItemStack.isSameItem(source, target)) {
                if (progress < MAX_PROGRESS) {
                    if (!ChiselConfig.autoChiselNeedsPower) {
                        progress = Math.min(MAX_PROGRESS, progress + BASE_PROGRESS);
                    }

                    int toUse = Math.min(MAX_PROGRESS - progress, getPowerProgressPerTick());
                    int powerToUse = getUsagePerTick();

                    if (toUse > 0 && powerToUse > 0) {
                        if (ChiselConfig.autoChiselPowered) {
                            try (var tx = Transaction.openRoot()) {
                                int used = energyStorage.extract(powerToUse, tx);
                                progress += (int) (toUse * ((float) used / powerToUse));
                                tx.commit();
                            }
                        } else {
                            progress += toUse;
                        }
                    }
                } else {
                    ItemStack result = new ItemStack(target.getItem(), source.getCount());

                    setStack(inputInv, sourceSlot, ItemStack.EMPTY);

                    if (ChiselConfig.allowChiselDamage && chisel.isDamageableItem()) {
                        chisel.setDamageValue(chisel.getDamageValue() + 1);
                        if (chisel.getDamageValue() >= chisel.getMaxDamage()) {
                            setStack(chiselSlot, 0, ItemStack.EMPTY);
                        }
                    }

                    mergeOutput(result);

                    if (level instanceof ServerLevel serverLevel) {
                        BlockState resultState = result.getItem() instanceof BlockItem blockItem
                                ? blockItem.getBlock().defaultBlockState()
                                : Blocks.STONE.defaultBlockState();
                        PacketDistributor.sendToPlayersTrackingChunk(serverLevel, serverLevel.getChunk(worldPosition).getPos(),
                                new AutoChiselFXPayload(worldPosition, chisel.copy(), resultState));
                    }

                    sourceSlot = (sourceSlot + 1) % inputInv.size();
                    progress = 0;
                }
            } else {
                setStack(inputInv, sourceSlot, ItemStack.EMPTY);
                mergeOutput(source);
            }
        } else {
            progress = 0;
        }
    }

    private float getSpeedFactor() {
        return ChiselConfig.autoChiselPowered ? (float) energyStorage.getAmountAsInt() / energyStorage.getCapacityAsInt() : 1;
    }

    private int getPowerProgressPerTick() {
        int maxPerTick = ChiselConfig.autoChiselNeedsPower ? (BASE_PROGRESS + SPEEDUP_PROGRESS) : SPEEDUP_PROGRESS;
        return (int) Math.ceil(getSpeedFactor() * maxPerTick);
    }

    private int getUsagePerTick() {
        return (int) Math.ceil(getSpeedFactor() * POWER_PER_TICK);
    }

    private boolean canOutput(ItemStack stack) {
        try (var tx = Transaction.openRoot()) {
            int remaining = stack.getCount();
            ItemResource resource = ItemResource.of(stack);
            remaining -= outputInv.insert(resource, remaining, tx);
            return remaining <= 0;
        }
    }

    private void mergeOutput(ItemStack stack) {
        if (stack.isEmpty()) return;
        try (var tx = Transaction.openRoot()) {
            outputInv.insert(ItemResource.of(stack), stack.getCount(), tx);
            tx.commit();
        }
    }

    public void dropContents(Level level, BlockPos pos) {
        for (int i = 0; i < inputInv.size(); i++) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), getStack(inputInv, i));
        }
        for (int i = 0; i < outputInv.size(); i++) {
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), getStack(outputInv, i));
        }
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), getStack(chiselSlot, 0));
        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), getStack(targetSlot, 0));
    }

    public ItemStacksResourceHandler getChiselSlot() {
        return chiselSlot;
    }

    public ItemStacksResourceHandler getTargetSlot() {
        return targetSlot;
    }

    public ItemStacksResourceHandler getInputInv() {
        return inputInv;
    }

    public ItemStacksResourceHandler getOutputInv() {
        return outputInv;
    }

    public SimpleEnergyHandler getEnergyStorage() {
        return energyStorage;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        chiselSlot.serialize(output.child("chisel"));
        targetSlot.serialize(output.child("target"));
        inputInv.serialize(output.child("input"));
        outputInv.serialize(output.child("output"));
        energyStorage.serialize(output.child("energy_data"));
        output.putInt("progress", progress);
        output.putInt("source", sourceSlot);
        if (customName != null) {
            output.store("customName", ComponentSerialization.CODEC, customName);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("chisel").ifPresent(chiselSlot::deserialize);
        input.child("target").ifPresent(targetSlot::deserialize);
        input.child("input").ifPresent(inputInv::deserialize);
        input.child("output").ifPresent(outputInv::deserialize);
        input.child("energy_data").ifPresent(energyStorage::deserialize);
        progress = input.getIntOr("progress", 0);
        sourceSlot = input.getIntOr("source", -1);
        input.read("customName", ComponentSerialization.CODEC).ifPresent(name -> customName = name);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
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

    public ResourceHandler<ItemResource> getCombinedItemHandler(@Nullable Direction side) {
        if (side == Direction.DOWN) {
            return outputInv;
        } else if (side == Direction.UP) {
            return inputInv;
        } else if (side == null) {
            return new CombinedResourceHandler<>(inputInv, outputInv, chiselSlot, targetSlot);
        } else {
            return new CombinedResourceHandler<>(inputInv, outputInv);
        }
    }
}
