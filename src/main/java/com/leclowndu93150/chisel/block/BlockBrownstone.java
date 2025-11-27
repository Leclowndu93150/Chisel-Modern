package com.leclowndu93150.chisel.block;

import com.leclowndu93150.chisel.ChiselConfig;
import com.leclowndu93150.chisel.api.block.VariationData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.List;

/**
 * Brownstone block that gives a speed boost when walked on.
 */
public class BlockBrownstone extends BlockCarvable {

    public BlockBrownstone(Properties properties, VariationData variation, String blockType) {
        super(properties, variation, blockType);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof LivingEntity livingEntity && livingEntity.onGround()) {
            Vec3 motion = entity.getDeltaMovement();
            double mult = ChiselConfig.concreteVelocityMult;
            if (Math.abs(motion.x) > 0.001 || Math.abs(motion.z) > 0.001) {
                entity.setDeltaMovement(motion.x * mult, motion.y, motion.z * mult);
            }
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("chisel.tooltip.brownstone.speed").withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, context, tooltip, flag);
    }
}
