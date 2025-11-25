package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.component.ChiselData;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registration class for Chisel data components.
 */
public class ChiselDataComponents {

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ChiselData>> CHISEL_DATA =
            ChiselRegistries.DATA_COMPONENT_TYPES.register("chisel_data", () ->
                    DataComponentType.<ChiselData>builder()
                            .persistent(ChiselData.CODEC)
                            .networkSynchronized(ChiselData.STREAM_CODEC)
                            .build()
            );

    public static void init() {
    }
}
