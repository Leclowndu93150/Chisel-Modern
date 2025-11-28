package com.leclowndu93150.chisel.block;

import com.leclowndu93150.chisel.api.block.VariationData;
import com.leclowndu93150.chisel.init.ChiselParticles;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Holystone block that emits star particles and glows.
 */
public class BlockHolystone extends BlockCarvable {

    public BlockHolystone(Properties properties, VariationData variation, String blockType) {
        super(properties, variation, blockType);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(16) == 0) {
            double x = pos.getX() + random.nextDouble();
            double y = pos.getY() + 1.0 + random.nextDouble() * 0.5;
            double z = pos.getZ() + random.nextDouble();

            double xSpeed = (random.nextDouble() - 0.5) * 0.02;
            double ySpeed = 0.01 + random.nextDouble() * 0.02;
            double zSpeed = (random.nextDouble() - 0.5) * 0.02;

            level.addParticle(ChiselParticles.HOLYSTONE_STAR.get(), x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("chisel.tooltip.holystone.glow").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
