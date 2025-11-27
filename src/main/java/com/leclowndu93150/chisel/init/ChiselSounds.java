package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.Chisel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Sound event registration for Chisel mod.
 * Includes metal block sounds (break, step, place, hit, fall) and chiseling action sounds.
 */
public class ChiselSounds {

    // Metal Block Sounds - for factory, laboratory, technical blocks
    public static final DeferredHolder<SoundEvent, SoundEvent> METAL_BREAK = registerSound("block.metal.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> METAL_FALL = registerSound("block.metal.fall");
    public static final DeferredHolder<SoundEvent, SoundEvent> METAL_HIT = registerSound("block.metal.hit");
    public static final DeferredHolder<SoundEvent, SoundEvent> METAL_PLACE = registerSound("block.metal.place");
    public static final DeferredHolder<SoundEvent, SoundEvent> METAL_STEP = registerSound("block.metal.step");

    // Holystone Block Sounds
    public static final DeferredHolder<SoundEvent, SoundEvent> HOLYSTONE_BREAK = registerSound("block.holystone.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> HOLYSTONE_FALL = registerSound("block.holystone.fall");
    public static final DeferredHolder<SoundEvent, SoundEvent> HOLYSTONE_HIT = registerSound("block.holystone.hit");
    public static final DeferredHolder<SoundEvent, SoundEvent> HOLYSTONE_PLACE = registerSound("block.holystone.place");
    public static final DeferredHolder<SoundEvent, SoundEvent> HOLYSTONE_STEP = registerSound("block.holystone.step");

    // Chiseling Action Sounds
    public static final DeferredHolder<SoundEvent, SoundEvent> CHISEL_FALLBACK = registerSound("chisel.fallback");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHISEL_WOOD = registerSound("chisel.wood");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHISEL_DIRT = registerSound("chisel.dirt");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSound(String name) {
        ResourceLocation id = Chisel.id(name);
        return ChiselRegistries.SOUND_EVENTS.register(name.replace(".", "_"),
            () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void init() {

    }
}
