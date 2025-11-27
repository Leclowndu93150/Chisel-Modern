package com.leclowndu93150.chisel.client.sound;

import com.leclowndu93150.chisel.init.ChiselSounds;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;

import java.util.function.Supplier;

/**
 * Custom sound types for Chisel blocks.
 * These provide custom walk, break, place, hit, and fall sounds.
 *
 * Uses DeferredSoundType to properly handle deferred sound event registration.
 */
public class ChiselSoundTypes {

    /**
     * Custom metal sound type for factory, laboratory, and similar industrial blocks.
     * Uses custom metal clanking sounds for all interactions.
     *
     * Uses DeferredSoundType which accepts Suppliers, allowing the sounds to be
     * resolved after registration is complete.
     */
    public static final SoundType METAL = new DeferredSoundType(
        1.0F,  // volume
        1.0F,  // pitch
        ChiselSounds.METAL_BREAK,  // break sound
        ChiselSounds.METAL_STEP,   // step/walk sound
        ChiselSounds.METAL_PLACE,  // place sound
        ChiselSounds.METAL_HIT,    // hit sound
        ChiselSounds.METAL_FALL    // fall sound
    );

    /**
     * Supplier for the metal sound type. Use this in ChiselBlockType.sound() calls.
     */
    public static final Supplier<SoundType> METAL_SUPPLIER = () -> METAL;

    /**
     * Custom holystone sound type for holystone blocks.
     * Uses ethereal, chime-like sounds.
     */
    public static final SoundType HOLYSTONE = new DeferredSoundType(
        1.0F,  // volume
        1.0F,  // pitch
        ChiselSounds.HOLYSTONE_BREAK,
        ChiselSounds.HOLYSTONE_STEP,
        ChiselSounds.HOLYSTONE_PLACE,
        ChiselSounds.HOLYSTONE_HIT,
        ChiselSounds.HOLYSTONE_FALL
    );

    /**
     * Supplier for the holystone sound type. Use this in ChiselBlockType.sound() calls.
     */
    public static final Supplier<SoundType> HOLYSTONE_SUPPLIER = () -> HOLYSTONE;
}
