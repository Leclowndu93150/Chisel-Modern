package com.leclowndu93150.chisel.mixin.compat.ftbultimine;

import com.leclowndu93150.chisel.ChiselConfig;
import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.carving.CarvingHelper;
import dev.ftb.mods.ftbultimine.shape.ShapeContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin to add Chisel carving group support to FTB Ultimine's block selection.
 * When the player is holding a chisel, blocks in the same carving group are selected together.
 */
@Mixin(value = ShapeContext.class, remap = false)
public abstract class ShapeContextMixin {

    @Shadow
    public abstract ServerPlayer player();

    @Shadow
    public abstract BlockState original();

    @Inject(method = "check(Lnet/minecraft/world/level/block/state/BlockState;)Z",
            at = @At("RETURN"), cancellable = true)
    private void chisel$checkCarvingGroup(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValue() && ChiselConfig.ultimineGroupVariants) {
            ServerPlayer player = this.player();
            ItemStack mainHand = player.getMainHandItem();
            ItemStack offHand = player.getOffhandItem();
            boolean holdingChisel = mainHand.getItem() instanceof IChiselItem
                    || offHand.getItem() instanceof IChiselItem;

            if (holdingChisel) {
                if (CarvingHelper.areInSameGroup(this.original(), state)) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
