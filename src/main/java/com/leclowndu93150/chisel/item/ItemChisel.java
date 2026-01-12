package com.leclowndu93150.chisel.item;

import com.leclowndu93150.chisel.ChiselConfig;
import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.leclowndu93150.chisel.carving.ChiselMode;
import com.leclowndu93150.chisel.carving.ChiselModeRegistry;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import com.leclowndu93150.chisel.component.ChiselData;
import com.leclowndu93150.chisel.init.ChiselDataComponents;
import com.leclowndu93150.chisel.inventory.ChiselMenu;
import com.leclowndu93150.chisel.inventory.HitechChiselMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class ItemChisel extends Item implements IChiselItem {

    public enum ChiselType {
        IRON(false, false),
        DIAMOND(true, true),
        HITECH(true, true);

        private final boolean canLeftClick;
        private final boolean hasModes;

        ChiselType(boolean canLeftClick, boolean hasModes) {
            this.canLeftClick = canLeftClick;
            this.hasModes = hasModes;
        }

        public int getMaxDamage() {
            return switch (this) {
                case IRON -> ChiselConfig.ironChiselMaxDamage;
                case DIAMOND -> ChiselConfig.diamondChiselMaxDamage;
                case HITECH -> ChiselConfig.hitechChiselMaxDamage;
            };
        }

        public int getAttackDamage() {
            return switch (this) {
                case IRON -> ChiselConfig.ironChiselAttackDamage;
                case DIAMOND -> ChiselConfig.diamondChiselAttackDamage;
                case HITECH -> ChiselConfig.hitechChiselAttackDamage;
            };
        }

        public boolean canLeftClick() {
            return canLeftClick || ChiselConfig.ironChiselCanLeftClick;
        }

        public boolean hasModes() {
            return hasModes || ChiselConfig.ironChiselHasModes;
        }
    }

    private final ChiselType type;

    public ItemChisel(ChiselType type, Properties properties) {
        super(properties);
        this.type = type;
    }

    public ChiselType getChiselType() {
        return type;
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (ChiselConfig.allowChiselDamage) {
            return type.getMaxDamage();
        }
        return 0;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return ChiselConfig.allowChiselDamage;
    }

    @Override
    public boolean isValidRepairItem(ItemStack damagedItem, ItemStack repairMaterial) {
        return switch (type) {
            case DIAMOND, HITECH -> repairMaterial.is(Items.DIAMOND);
            case IRON -> repairMaterial.is(Items.IRON_INGOT);
        };
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("chisel.tooltip.gui").withStyle(ChatFormatting.GRAY));

        if (type.canLeftClick()) {
            tooltip.add(Component.translatable("chisel.tooltip.leftclick.1").withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("chisel.tooltip.leftclick.2").withStyle(ChatFormatting.GRAY));
        }

        if (type.hasModes()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.translatable("chisel.tooltip.modes").withStyle(ChatFormatting.GRAY));
            IChiselMode mode = getMode(stack);
            tooltip.add(Component.translatable("chisel.tooltip.selectedmode",
                    mode.getLocalizedName().copy().withStyle(ChatFormatting.GREEN)).withStyle(ChatFormatting.GRAY));

            boolean fuzzy = isFuzzyMode(stack);
            Component fuzzyStatus = fuzzy
                    ? Component.translatable("chisel.tooltip.fuzzy.enabled").withStyle(ChatFormatting.GREEN)
                    : Component.translatable("chisel.tooltip.fuzzy.disabled").withStyle(ChatFormatting.RED);
            tooltip.add(Component.translatable("chisel.tooltip.fuzzy", fuzzyStatus).withStyle(ChatFormatting.GRAY));
            tooltip.add(Component.translatable("chisel.tooltip.fuzzy.hint").withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    @Override
    public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
        return ItemAttributeModifiers.builder()
                .add(Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(Item.BASE_ATTACK_DAMAGE_ID, type.getAttackDamage(), AttributeModifier.Operation.ADD_VALUE),
                        EquipmentSlotGroup.MAINHAND)
                .build();
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (player.isCrouching() && type.hasModes()) {
            if (!level.isClientSide) {
                cycleMode(stack, player, true);
            }
            return InteractionResultHolder.success(stack);
        }

        if (!level.isClientSide && canOpenGui(level, player, hand)) {
            if (player instanceof ServerPlayer serverPlayer) {
                ChiselGuiType guiType = getGuiType(level, player, hand);
                if (guiType == ChiselGuiType.HITECH) {
                    serverPlayer.openMenu(new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return Component.translatable("container.chisel.hitech");
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player p) {
                            return new HitechChiselMenu(containerId, playerInv, hand);
                        }
                    }, buf -> buf.writeBoolean(hand == InteractionHand.MAIN_HAND));
                } else {
                    serverPlayer.openMenu(new MenuProvider() {
                        @Override
                        public Component getDisplayName() {
                            return Component.translatable("container.chisel");
                        }

                        @Override
                        public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player p) {
                            return new ChiselMenu(containerId, playerInv, hand);
                        }
                    }, buf -> buf.writeBoolean(hand == InteractionHand.MAIN_HAND));
                }
                return InteractionResultHolder.consume(stack);
            }
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean canOpenGui(Level world, Player player, InteractionHand hand) {
        return true;
    }

    @Override
    public ChiselGuiType getGuiType(Level world, Player player, InteractionHand hand) {
        return type == ChiselType.HITECH ? ChiselGuiType.HITECH : ChiselGuiType.NORMAL;
    }

    @Override
    public boolean onChisel(Level world, Player player, ItemStack chisel, Block target) {
        return ChiselConfig.allowChiselDamage;
    }

    @Override
    public boolean canChisel(Level world, Player player, ItemStack chisel, ItemStack target) {
        return !chisel.isEmpty() && CarvingHelper.canChisel(target);
    }

    @Override
    public boolean canChiselBlock(Level world, Player player, InteractionHand hand, BlockPos pos, BlockState state) {
        return type.canLeftClick() && CarvingHelper.canChisel(state);
    }

    @Override
    public boolean supportsMode(Player player, ItemStack chisel, IChiselMode mode) {
        if (type == ChiselType.HITECH) {
            return true;
        }
        if (type == ChiselType.DIAMOND || ChiselConfig.ironChiselHasModes) {
            return mode != ChiselMode.CONTIGUOUS && mode != ChiselMode.CONTIGUOUS_2D;
        }
        return mode == ChiselMode.SINGLE;
    }

    public IChiselMode getMode(ItemStack stack) {
        ChiselData data = stack.get(ChiselDataComponents.CHISEL_DATA.get());
        if (data != null) {
            IChiselMode mode = ChiselModeRegistry.INSTANCE.getModeByName(data.mode());
            if (mode != null) {
                return mode;
            }
        }
        return ChiselMode.SINGLE;
    }

    public void setMode(ItemStack stack, IChiselMode mode) {
        ChiselData data = stack.getOrDefault(ChiselDataComponents.CHISEL_DATA.get(), ChiselData.DEFAULT);
        stack.set(ChiselDataComponents.CHISEL_DATA.get(), data.withMode(mode.name()));
    }

    public int getPreviewType(ItemStack stack) {
        ChiselData data = stack.get(ChiselDataComponents.CHISEL_DATA.get());
        return data != null ? data.previewType() : 0;
    }

    public void setPreviewType(ItemStack stack, int type) {
        ChiselData data = stack.getOrDefault(ChiselDataComponents.CHISEL_DATA.get(), ChiselData.DEFAULT);
        stack.set(ChiselDataComponents.CHISEL_DATA.get(), data.withPreviewType(type));
    }

    public int getSelectionSlot(ItemStack stack) {
        ChiselData data = stack.get(ChiselDataComponents.CHISEL_DATA.get());
        return data != null ? data.selectionSlot() : -1;
    }

    public void setSelectionSlot(ItemStack stack, int slot) {
        ChiselData data = stack.getOrDefault(ChiselDataComponents.CHISEL_DATA.get(), ChiselData.DEFAULT);
        stack.set(ChiselDataComponents.CHISEL_DATA.get(), data.withSelectionSlot(slot));
    }

    public int getTargetSlot(ItemStack stack) {
        ChiselData data = stack.get(ChiselDataComponents.CHISEL_DATA.get());
        return data != null ? data.targetSlot() : -1;
    }

    public void setTargetSlot(ItemStack stack, int slot) {
        ChiselData data = stack.getOrDefault(ChiselDataComponents.CHISEL_DATA.get(), ChiselData.DEFAULT);
        stack.set(ChiselDataComponents.CHISEL_DATA.get(), data.withTargetSlot(slot));
    }

    public boolean getRotate(ItemStack stack) {
        ChiselData data = stack.get(ChiselDataComponents.CHISEL_DATA.get());
        return data != null && data.rotate();
    }

    public void setRotate(ItemStack stack, boolean rotate) {
        ChiselData data = stack.getOrDefault(ChiselDataComponents.CHISEL_DATA.get(), ChiselData.DEFAULT);
        stack.set(ChiselDataComponents.CHISEL_DATA.get(), data.withRotate(rotate));
    }

    public ItemStack getTarget(ItemStack stack) {
        ChiselData data = stack.get(ChiselDataComponents.CHISEL_DATA.get());
        return data != null ? data.target() : ItemStack.EMPTY;
    }

    public void setTarget(ItemStack stack, ItemStack target) {
        ChiselData data = stack.getOrDefault(ChiselDataComponents.CHISEL_DATA.get(), ChiselData.DEFAULT);
        stack.set(ChiselDataComponents.CHISEL_DATA.get(), data.withTarget(target));
    }

    @Override
    public boolean isFuzzyMode(ItemStack stack) {
        ChiselData data = stack.get(ChiselDataComponents.CHISEL_DATA.get());
        return data != null && data.fuzzy();
    }

    @Override
    public void setFuzzyMode(ItemStack stack, boolean fuzzy) {
        ChiselData data = stack.getOrDefault(ChiselDataComponents.CHISEL_DATA.get(), ChiselData.DEFAULT);
        stack.set(ChiselDataComponents.CHISEL_DATA.get(), data.withFuzzy(fuzzy));
    }

    public void cycleMode(ItemStack stack, Player player, boolean forward) {
        IChiselMode current = getMode(stack);
        IChiselMode next;

        if (forward) {
            next = ChiselModeRegistry.INSTANCE.getNextMode(current);
        } else {
            next = ChiselModeRegistry.INSTANCE.getPreviousMode(current);
        }

        while (next != current && !supportsMode(player, stack, next)) {
            if (forward) {
                next = ChiselModeRegistry.INSTANCE.getNextMode(next);
            } else {
                next = ChiselModeRegistry.INSTANCE.getPreviousMode(next);
            }
        }

        if (next != current) {
            setMode(stack, next);
            player.displayClientMessage(Component.translatable("chisel.message.mode_changed",
                    next.getLocalizedName()), true);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged || !ItemStack.isSameItem(oldStack, newStack);
    }

    @Override
    public void openGui(Player player, InteractionHand hand, ItemStack chisel) {
        if (player instanceof ServerPlayer serverPlayer) {
            ChiselGuiType guiType = getGuiType(player.level(), player, hand);
            if (guiType == ChiselGuiType.HITECH) {
                serverPlayer.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("container.chisel.hitech");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player p) {
                        return new HitechChiselMenu(containerId, playerInv, hand);
                    }
                }, buf -> buf.writeBoolean(hand == InteractionHand.MAIN_HAND));
            } else {
                serverPlayer.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("container.chisel");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int containerId, Inventory playerInv, Player p) {
                        return new ChiselMenu(containerId, playerInv, hand);
                    }
                }, buf -> buf.writeBoolean(hand == InteractionHand.MAIN_HAND));
            }
        }
    }
}
