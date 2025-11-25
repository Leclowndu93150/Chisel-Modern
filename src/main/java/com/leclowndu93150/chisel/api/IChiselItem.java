package com.leclowndu93150.chisel.api;

import com.leclowndu93150.chisel.api.carving.IChiselMode;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.entity.EquipmentSlot;

import javax.annotation.Nullable;

/**
 * Interface for items that can be used as chisels.
 */
public interface IChiselItem {

    /**
     * Checks whether the chisel can have its GUI opened.
     *
     * @param world  The world
     * @param player The player holding the chisel
     * @param hand   The hand which the chisel is in
     * @return True if the GUI should open
     */
    boolean canOpenGui(Level world, Player player, InteractionHand hand);

    /**
     * Gets the type of GUI to open (normal or hitech).
     */
    ChiselGuiType getGuiType(Level world, Player player, InteractionHand hand);

    /**
     * Called when an item is chiseled using this chisel.
     *
     * @param world  The world
     * @param player The player performing the chiseling
     * @param chisel The chisel ItemStack
     * @param target The target block being chiseled to
     * @return True if the chisel should take damage
     */
    boolean onChisel(Level world, Player player, ItemStack chisel, Block target);

    /**
     * Checks if an item can be chiseled with this chisel.
     */
    boolean canChisel(Level world, Player player, ItemStack chisel, ItemStack target);

    /**
     * Checks if this chisel can chisel a block in the world.
     */
    boolean canChiselBlock(Level world, Player player, InteractionHand hand, BlockPos pos, BlockState state);

    /**
     * Checks if this chisel supports a given chisel mode.
     */
    boolean supportsMode(Player player, ItemStack chisel, IChiselMode mode);

    /**
     * Gets an override sound for chiseling, or null for default.
     */
    @Nullable
    default SoundEvent getOverrideSound(Level world, Player player, ItemStack chisel, Block target) {
        return null;
    }

    /**
     * Crafts items using this chisel.
     *
     * @param chisel  The chisel ItemStack
     * @param source  The source ItemStack being chiseled
     * @param target  The target ItemStack type
     * @param player  The player doing the chiseling
     * @param slot    The equipment slot the chisel is in
     * @return The result of the craft
     */
    default ItemStack craftItem(ItemStack chisel, ItemStack source, ItemStack target, Player player, EquipmentSlot slot) {
        if (chisel.isEmpty()) return ItemStack.EMPTY;

        int toCraft = Math.min(source.getCount(), target.getMaxStackSize());

        if (chisel.isDamageableItem()) {
            int damageLeft = chisel.getMaxDamage() - chisel.getDamageValue() + 1;
            toCraft = Math.min(toCraft, damageLeft);
            chisel.hurtAndBreak(toCraft, player, slot);
        }

        ItemStack res = target.copy();
        source.shrink(toCraft);
        res.setCount(toCraft);
        return res;
    }

    /**
     * Enum for chisel GUI types.
     */
    enum ChiselGuiType {
        NORMAL,
        HITECH
    }
}
