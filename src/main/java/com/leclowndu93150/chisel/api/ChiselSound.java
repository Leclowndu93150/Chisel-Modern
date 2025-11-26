package com.leclowndu93150.chisel.api;

import com.leclowndu93150.chisel.init.ChiselSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

/**
 * Enum representing different chiseling sound categories.
 * Each category maps to a set of sound files that play when chiseling blocks of that material type.
 */
public enum ChiselSound {
    /**
     * Default/fallback chiseling sound for stone-like materials.
     * Used for most blocks that don't have a specific sound.
     */
    FALLBACK(ChiselSounds.CHISEL_FALLBACK),

    /**
     * Chiseling sound for wood-based materials.
     * Plays a wood carving/scraping sound.
     */
    WOOD(ChiselSounds.CHISEL_WOOD),

    /**
     * Chiseling sound for dirt/earth materials.
     * Plays a crunchy earth sound.
     */
    DIRT(ChiselSounds.CHISEL_DIRT),

    /**
     * Uses vanilla stonecutter sound.
     * Good for harder stone materials.
     */
    STONECUTTER(() -> SoundEvents.UI_STONECUTTER_TAKE_RESULT);

    private final Supplier<SoundEvent> soundSupplier;

    ChiselSound(Supplier<SoundEvent> soundSupplier) {
        this.soundSupplier = soundSupplier;
    }

    /**
     * Gets the sound event for this chisel sound category.
     * @return The SoundEvent to play when chiseling
     */
    public SoundEvent getSound() {
        return soundSupplier.get();
    }

    /**
     * Gets the volume for this chisel sound (randomized).
     * @param random A random value between 0 and 1
     * @return Volume between 0.3 and 1.0
     */
    public float getVolume(float random) {
        return 0.3f + 0.7f * random;
    }

    /**
     * Gets the pitch for this chisel sound (randomized).
     * @param random A random value between 0 and 1
     * @return Pitch between 0.6 and 1.0
     */
    public float getPitch(float random) {
        return 0.6f + 0.4f * random;
    }
}
