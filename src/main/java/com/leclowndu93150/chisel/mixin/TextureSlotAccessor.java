package com.leclowndu93150.chisel.mixin;

import net.minecraft.client.data.models.model.TextureSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TextureSlot.class)
public interface TextureSlotAccessor {

    @Invoker("create")
    static TextureSlot chisel$create(String id) {
        throw new AssertionError();
    }

    @Invoker("create")
    static TextureSlot chisel$createWithParent(String id, TextureSlot parent) {
        throw new AssertionError();
    }
}
