package com.leclowndu93150.chisel.mixin;

import net.minecraft.client.renderer.chunk.RenderChunkRegion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RenderChunkRegion.class)
public interface RenderChunkRegionAccessor {

    @Accessor("level")
    Level chisel$getLevel();
}
