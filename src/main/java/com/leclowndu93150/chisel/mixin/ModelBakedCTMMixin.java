package com.leclowndu93150.chisel.mixin;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.llamalad7.mixinextras.sugar.Local;
import team.chisel.ctm.api.texture.ICTMTexture;
import team.chisel.ctm.api.texture.ITextureContext;
import team.chisel.ctm.client.model.ModelBakedCTM;

import java.util.Collections;
import java.util.List;

/**
 * Mixin to fix carpet CTM rendering issues.
 * When wool textures have CTM metadata, carpet side faces render incorrectly
 * because CTM subdivides quads into 4 parts which breaks for 1-pixel tall faces.
 * This mixin skips CTM processing for carpet side faces (NORTH/SOUTH/EAST/WEST).
 */
@Mixin(value = ModelBakedCTM.class, remap = false)
public abstract class ModelBakedCTMMixin {

    @Redirect(
            method = "createModel",
            at = @At(
                    value = "INVOKE",
                    target = "Lteam/chisel/ctm/api/texture/ICTMTexture;transformQuad(Lnet/minecraft/client/renderer/block/model/BakedQuad;Lteam/chisel/ctm/api/texture/ITextureContext;I)Ljava/util/List;"
            )
    )
    private List<BakedQuad> chisel$skipCarpetSides(
            ICTMTexture<?> texture,
            BakedQuad quad,
            ITextureContext ctx,
            int quadGoal,
            @Local(argsOnly = true) BlockState state
    ) {
        Direction facing = quad.getDirection();
        if (state != null && state.getBlock() instanceof CarpetBlock && facing != null && facing.getAxis().isHorizontal()) {
            return Collections.singletonList(quad);
        }
        return texture.transformQuad(quad, ctx, quadGoal);
    }
}
