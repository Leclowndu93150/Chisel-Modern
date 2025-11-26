package com.leclowndu93150.chisel.network;

import com.leclowndu93150.chisel.Chisel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Handles registration of all network payloads for the Chisel mod.
 */
@EventBusSubscriber(modid = Chisel.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ChiselNetwork {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Chisel.MODID).versioned("1.0.0");

        registrar.playToServer(
                ChiselModePayload.TYPE,
                ChiselModePayload.STREAM_CODEC,
                ChiselModePayload::handle
        );

        registrar.playToServer(
                ChiselButtonPayload.TYPE,
                ChiselButtonPayload.STREAM_CODEC,
                ChiselButtonPayload::handle
        );

        registrar.playToServer(
                HitechSettingsPayload.TYPE,
                HitechSettingsPayload.STREAM_CODEC,
                HitechSettingsPayload::handle
        );

        registrar.playToClient(
                AutoChiselFXPayload.TYPE,
                AutoChiselFXPayload.STREAM_CODEC,
                AutoChiselFXPayload::handle
        );

        registrar.playToClient(
                ChunkDataPayload.TYPE,
                ChunkDataPayload.STREAM_CODEC,
                ChunkDataPayload::handle
        );
    }
}
