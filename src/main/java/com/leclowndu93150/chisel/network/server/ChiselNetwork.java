package com.leclowndu93150.chisel.network.server;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.network.AutoChiselFXPayload;
import com.leclowndu93150.chisel.network.ChunkDataPayload;
import com.leclowndu93150.chisel.network.client.ClientPayloadHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/**
 * Handles registration of all network payloads.
 */
@EventBusSubscriber(modid = Chisel.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ChiselNetwork {

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Chisel.MODID).versioned("1.0.1");

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

        registrar.playToServer(
                ChiselFuzzyPayload.TYPE,
                ChiselFuzzyPayload.STREAM_CODEC,
                ChiselFuzzyPayload::handle
        );

        registrar.playToClient(
                AutoChiselFXPayload.TYPE,
                AutoChiselFXPayload.STREAM_CODEC,
                FMLEnvironment.dist.isClient() ? ClientHandlers.autoChiselFX() : (p, c) -> {}
        );

        registrar.playToClient(
                ChunkDataPayload.TYPE,
                ChunkDataPayload.STREAM_CODEC,
                FMLEnvironment.dist.isClient() ? ClientHandlers.chunkData() : (p, c) -> {}
        );
    }

    /**
     * Holder class to defer loading of ClientPayloadHandler until needed.
     */
    private static class ClientHandlers {
        static IPayloadHandler<AutoChiselFXPayload> autoChiselFX() {
            return ClientPayloadHandler::handleAutoChiselFX;
        }

        static IPayloadHandler<ChunkDataPayload> chunkData() {
            return ClientPayloadHandler::handleChunkData;
        }
    }
}
