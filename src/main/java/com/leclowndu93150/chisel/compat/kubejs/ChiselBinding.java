package com.leclowndu93150.chisel.compat.kubejs;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.ChiselSound;
import dev.latvian.mods.kubejs.typings.Info;

public class ChiselBinding {

    @Info("The Chisel mod ID")
    public static final String MOD_ID = Chisel.MODID;

    @Info("Chiseling sound for wood materials")
    public static final ChiselSound SOUND_WOOD = ChiselSound.WOOD;

    @Info("Chiseling sound for dirt materials")
    public static final ChiselSound SOUND_DIRT = ChiselSound.DIRT;

    @Info("Default chiseling sound (fallback)")
    public static final ChiselSound SOUND_FALLBACK = ChiselSound.FALLBACK;

    @Info("Stonecutter chiseling sound")
    public static final ChiselSound SOUND_STONECUTTER = ChiselSound.STONECUTTER;

    @Info("Create a chisel resource location")
    public static String id(String path) {
        return Chisel.MODID + ":" + path;
    }

    @Info("Create a carving group tag ID")
    public static String carvingTag(String groupName) {
        return Chisel.MODID + ":carving/" + groupName;
    }
}
