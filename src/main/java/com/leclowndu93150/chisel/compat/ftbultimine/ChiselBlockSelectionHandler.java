package com.leclowndu93150.chisel.compat.ftbultimine;

import com.leclowndu93150.chisel.ChiselConfig;
import com.leclowndu93150.chisel.api.IChiselItem;
import dev.ftb.mods.ftbultimine.api.blockselection.BlockSelectionHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public enum ChiselBlockSelectionHandler implements BlockSelectionHandler {
    INSTANCE;

    @Override
    public Result customSelectionCheck(Player player, BlockPos origPos, BlockPos pos, BlockState origState, BlockState state) {
        if (!ChiselConfig.ultimineGroupVariants) {
            return Result.PASS;
        }

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean holdingChisel = mainHand.getItem() instanceof IChiselItem || offHand.getItem() instanceof IChiselItem;

        if (!holdingChisel) {
            return Result.PASS;
        }

        if (origState.getBlock() == state.getBlock()) {
            return Result.TRUE;
        }

        return Result.PASS;
    }
}
