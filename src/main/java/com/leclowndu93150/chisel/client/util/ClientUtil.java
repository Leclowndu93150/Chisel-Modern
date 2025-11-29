package com.leclowndu93150.chisel.client.util;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class ClientUtil {

    public static final RenderType OFFSET_OVERLAY = RenderType.create(
            "chisel_offset_overlay",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.TRIANGLES,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setTransparencyState(new RenderStateShard.TransparencyStateShard(
                            "translucent_transparency",
                            () -> {
                                RenderSystem.enableBlend();
                                RenderSystem.blendFuncSeparate(
                                        GlStateManager.SourceFactor.SRC_ALPHA,
                                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                                        GlStateManager.SourceFactor.ONE,
                                        GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
                                );
                            },
                            () -> {
                                RenderSystem.disableBlend();
                                RenderSystem.defaultBlendFunc();
                            }
                    ))
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setDepthTestState(new RenderStateShard.DepthTestStateShard("lequal", 515))
                    .createCompositeState(false)
    );
}
