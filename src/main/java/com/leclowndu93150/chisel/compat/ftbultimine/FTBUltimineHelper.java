package com.leclowndu93150.chisel.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.FTBUltimine;
import dev.ftb.mods.ftbultimine.FTBUltiminePlayerData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Optional;

/**
 * Helper for accessing FTB Ultimine's current block selection.
 * Used to apply chisel operations to all blocks selected by FTB Ultimine.
 */
public class FTBUltimineHelper {

    /**
     * Gets the current block selection from FTB Ultimine for the given player.
     *
     * @param player The player to get the selection for
     * @return Optional containing the block positions if ultimine is active, empty otherwise
     */
    public static Optional<Collection<BlockPos>> getBlockSelection(Player player) {
        if (FTBUltimine.instance == null) {
            return Optional.empty();
        }

        FTBUltiminePlayerData data = FTBUltimine.instance.getOrCreatePlayerData(player);

        if (data.isPressed() && data.hasCachedPositions()) {
            return Optional.of(data.cachedPositions());
        }

        return Optional.empty();
    }
}
