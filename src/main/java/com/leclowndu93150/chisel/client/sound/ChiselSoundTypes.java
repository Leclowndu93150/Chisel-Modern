package com.leclowndu93150.chisel.client.sound;

import com.leclowndu93150.chisel.init.ChiselSounds;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.Lazy;

import java.util.function.Supplier;

/**
 * Custom sound types for Chisel blocks.
 * These provide custom walk, break, place, hit, and fall sounds.
 *
 * Uses lazy initialization to properly handle deferred sound event registration.
 */
public class ChiselSoundTypes {

    /**
     * Custom metal sound type for factory, laboratory, and similar industrial blocks.
     * Uses custom metal clanking sounds for all interactions.
     *
     * Lazy initialized to ensure sound events are registered before access.
     */
    private static final Lazy<SoundType> METAL_LAZY = Lazy.of(() -> new SoundType(
        1.0F,  // volume
        1.0F,  // pitch
        ChiselSounds.METAL_BREAK.get(),  // break sound
        ChiselSounds.METAL_STEP.get(),   // step/walk sound
        ChiselSounds.METAL_PLACE.get(),  // place sound
        ChiselSounds.METAL_HIT.get(),    // hit sound
        ChiselSounds.METAL_FALL.get()    // fall sound
    ));

    /**
     * Supplier for the metal sound type. Use this in ChiselBlockType.sound() calls.
     */
    public static final Supplier<SoundType> METAL_SUPPLIER = METAL_LAZY::get;

    /**
     * Custom holystone sound type for holystone blocks.
     * Uses ethereal, chime-like sounds.
     */
    private static final Lazy<SoundType> HOLYSTONE_LAZY = Lazy.of(() -> new SoundType(
        1.0F,  // volume
        1.0F,  // pitch
        ChiselSounds.HOLYSTONE_BREAK.get(),
        ChiselSounds.HOLYSTONE_STEP.get(),
        ChiselSounds.HOLYSTONE_PLACE.get(),
        ChiselSounds.HOLYSTONE_HIT.get(),
        ChiselSounds.HOLYSTONE_FALL.get()
    ));

    /**
     * Supplier for the holystone sound type. Use this in ChiselBlockType.sound() calls.
     */
    public static final Supplier<SoundType> HOLYSTONE_SUPPLIER = HOLYSTONE_LAZY::get;
}
