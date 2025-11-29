package com.leclowndu93150.chisel.compat.kubejs;

import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.script.BindingsEvent;

public class ChiselKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void registerEvents() {
        ChiselKubeJSEvents.GROUP.register();
    }

    @Override
    public void registerBindings(BindingsEvent event) {
        event.add("Chisel", ChiselBinding.class);
    }
}
