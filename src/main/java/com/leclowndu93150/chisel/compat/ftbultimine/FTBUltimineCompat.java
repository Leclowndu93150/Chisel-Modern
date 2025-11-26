package com.leclowndu93150.chisel.compat.ftbultimine;

import dev.ftb.mods.ftbultimine.api.blockselection.RegisterBlockSelectionHandlerEvent;

public class FTBUltimineCompat {

    public static void init() {
        RegisterBlockSelectionHandlerEvent.REGISTER.register(dispatcher -> {
            dispatcher.registerHandler(ChiselBlockSelectionHandler.INSTANCE);
        });
    }
}
