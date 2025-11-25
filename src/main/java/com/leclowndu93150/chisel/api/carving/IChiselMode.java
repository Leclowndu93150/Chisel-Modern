package com.leclowndu93150.chisel.api.carving;

import com.leclowndu93150.chisel.Chisel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

import java.util.Locale;

/**
 * Interface for chisel modes that determine how blocks are selected for chiseling.
 */
public interface IChiselMode {

    /**
     * Retrieve all valid positions that can be chiseled from where the player is targeting.
     * Must consider state equality, if necessary.
     *
     * @param player The player.
     * @param pos    The position of the targeted block.
     * @param side   The side of the block being targeted.
     * @return All valid positions to be chiseled.
     */
    Iterable<? extends BlockPos> getCandidates(Player player, BlockPos pos, Direction side);

    /**
     * Gets the bounding box for rendering the selection preview.
     */
    AABB getBounds(Direction side);

    /**
     * Gets the name of this mode. Implemented implicitly by enums.
     */
    String name();

    /**
     * Gets the ordinal of this mode for sprite positioning.
     */
    int ordinal();

    default String getUnlocName() {
        return "chisel.mode." + name().toLowerCase(Locale.ROOT);
    }

    default String getUnlocDescription() {
        return getUnlocName() + ".desc";
    }

    default Component getLocalizedName() {
        return Component.translatable(getUnlocName());
    }

    default Component getLocalizedDescription() {
        return Component.translatable(getUnlocDescription());
    }

    default long[] getCacheState(BlockPos origin, Direction side) {
        return new long[]{origin.asLong(), side.ordinal()};
    }

    ResourceLocation SPRITES = Chisel.id("textures/gui/mode_icons.png");

    default ResourceLocation getSpriteSheet() {
        return SPRITES;
    }

    /**
     * Gets the position of this mode's sprite in the sprite sheet.
     */
    default int[] getSpritePos() {
        return new int[]{(ordinal() % 10) * 24, (ordinal() / 10) * 24};
    }
}
