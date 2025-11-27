package com.leclowndu93150.chisel.mixin;

import com.leclowndu93150.chisel.Chisel;
import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Mixin to log failed Chisel block lookups during world loading.
 * Only logs blocks with the "chisel" namespace that couldn't be resolved
 * (no alias or alias target doesn't exist).
 */
@Mixin(MappedRegistry.class)
public class MappedRegistryMixin<T> {

    @Unique
    private static final Logger LOGGER = LogUtils.getLogger();

    @Unique
    private static final Set<String> chisel$loggedMissingBlocks = new HashSet<>();

    @Inject(method = "getHolder(Lnet/minecraft/resources/ResourceLocation;)Ljava/util/Optional;",
            at = @At("RETURN"))
    private void chisel$logMissingChiselBlocks(ResourceLocation location, CallbackInfoReturnable<Optional<Holder.Reference<T>>> cir) {
        if (location != null && location.getNamespace().equals(Chisel.MODID)) {
            Optional<Holder.Reference<T>> result = cir.getReturnValue();
            if (result.isEmpty()) {
                String key = location.toString();
                if (chisel$loggedMissingBlocks.add(key)) {
                    LOGGER.warn("[Chisel Compat] Missing block with no valid alias: {} - This block will be removed from the world", location);
                }
            }
        }
    }
}
