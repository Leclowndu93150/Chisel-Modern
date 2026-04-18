package com.leclowndu93150.chisel.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.neoforge.FTBUltimineEvent;
import net.neoforged.neoforge.common.NeoForge;

public class FTBUltimineCompat {

    public static void init() {
        NeoForge.EVENT_BUS.addListener(FTBUltimineEvent.RegisterBlockSelectionHandler.class, event ->
                event.getEventData().consumer().accept(ChiselBlockSelectionHandler.INSTANCE));
    }
}
