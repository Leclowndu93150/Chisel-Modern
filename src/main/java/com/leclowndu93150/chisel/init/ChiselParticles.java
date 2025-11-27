package com.leclowndu93150.chisel.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Particle type registration for Chisel mod.
 */
public class ChiselParticles {

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> HOLYSTONE_STAR =
        ChiselRegistries.PARTICLE_TYPES.register("holystone_star", () -> new SimpleParticleType(false));

    public static void init() {

    }
}
