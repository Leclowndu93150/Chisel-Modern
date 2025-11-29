package com.leclowndu93150.chisel.network;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.network.client.AutoChiselFXPacket;
import com.leclowndu93150.chisel.network.client.ChunkDataPacket;
import com.leclowndu93150.chisel.network.server.ChiselButtonPacket;
import com.leclowndu93150.chisel.network.server.ChiselModePacket;
import com.leclowndu93150.chisel.network.server.HitechSettingsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

/**
 * Handles registration of all network packets (Forge 1.20.1).
 */
public class ChiselNetwork {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Chisel.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    private static int id() {
        return packetId++;
    }

    public static void register() {
        // Server-bound packets
        CHANNEL.messageBuilder(ChiselModePacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(ChiselModePacket::encode)
                .decoder(ChiselModePacket::decode)
                .consumerMainThread(ChiselModePacket::handle)
                .add();

        CHANNEL.messageBuilder(ChiselButtonPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(ChiselButtonPacket::encode)
                .decoder(ChiselButtonPacket::decode)
                .consumerMainThread(ChiselButtonPacket::handle)
                .add();

        CHANNEL.messageBuilder(HitechSettingsPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .encoder(HitechSettingsPacket::encode)
                .decoder(HitechSettingsPacket::decode)
                .consumerMainThread(HitechSettingsPacket::handle)
                .add();

        // Client-bound packets
        CHANNEL.messageBuilder(AutoChiselFXPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(AutoChiselFXPacket::encode)
                .decoder(AutoChiselFXPacket::decode)
                .consumerMainThread(AutoChiselFXPacket::handle)
                .add();

        CHANNEL.messageBuilder(ChunkDataPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ChunkDataPacket::encode)
                .decoder(ChunkDataPacket::decode)
                .consumerMainThread(ChunkDataPacket::handle)
                .add();
    }

    public static void sendToServer(Object message) {
        CHANNEL.sendToServer(message);
    }

    public static void sendToPlayer(ServerPlayer player, Object message) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendToAllTrackingChunk(net.minecraft.world.level.chunk.LevelChunk chunk, Object message) {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), message);
    }
}
