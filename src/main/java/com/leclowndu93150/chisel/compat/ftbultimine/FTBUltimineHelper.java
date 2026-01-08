package com.leclowndu93150.chisel.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.FTBUltimineAPI;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

import java.util.Collection;
import java.util.Optional;

public class FTBUltimineHelper {

    public static Optional<Collection<BlockPos>> getBlockSelection(Player player) {
        try {
            var api = FTBUltimineAPI.api();
            if (api == null) {
                return Optional.empty();
            }
            return api.currentBlockSelection(player);
        } catch (Exception e) {
            // FTB Ultimine API may not be fully initialized
            return Optional.empty();
        }
    }
}
