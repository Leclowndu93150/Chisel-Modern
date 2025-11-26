package com.leclowndu93150.chisel.client.render.type;

import com.leclowndu93150.chisel.client.render.ctx.BlockRenderContextAlterR;
import com.leclowndu93150.chisel.client.render.texture.ChiselTextureAlterR;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import team.chisel.ctm.api.texture.ICTMTexture;
import team.chisel.ctm.api.texture.ITextureContext;
import team.chisel.ctm.api.texture.ITextureType;
import team.chisel.ctm.api.texture.TextureType;
import team.chisel.ctm.api.util.TextureInfo;
import team.chisel.ctm.client.texture.ctx.TextureContextPosition;

@TextureType("AR")
public class BlockRenderTypeAlterR implements ITextureType {
    @Override
    public ChiselTextureAlterR makeTexture(TextureInfo info) {
        return new ChiselTextureAlterR(this, info);
    }

    @Override
    public ITextureContext getBlockRenderContext(BlockState blockState, BlockAndTintGetter blockAndTintGetter, BlockPos blockPos, ICTMTexture<?> ictmTexture) {
        return new BlockRenderContextAlterR(blockPos, (ChiselTextureAlterR) ictmTexture);
    }

    @Override
    public ITextureContext getContextFromData(long data) {
        return new TextureContextPosition(BlockPos.of(data));
    }

    @Override
    public int getQuadsPerSide() {
        return 1;
    }

    @Override
    public int requiredTextures() {
        return 1;
    }
}
