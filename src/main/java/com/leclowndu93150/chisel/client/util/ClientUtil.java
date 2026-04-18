package com.leclowndu93150.chisel.client.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;

public class ClientUtil {

    private static final RenderPipeline OFFSET_OVERLAY_PIPELINE = RenderPipeline.builder()
            .withLocation(com.leclowndu93150.chisel.Chisel.id("pipeline/offset_overlay"))
            .withVertexShader("core/position_color")
            .withFragmentShader("core/position_color")
            .withVertexFormat(DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.TRIANGLES)
            .withCull(false)
            .build();

    public static final RenderType OFFSET_OVERLAY = RenderType.create(
            "chisel_offset_overlay",
            RenderSetup.builder(OFFSET_OVERLAY_PIPELINE)
                    .createRenderSetup()
    );
}
