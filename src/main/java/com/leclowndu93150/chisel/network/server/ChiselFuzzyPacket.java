package com.leclowndu93150.chisel.network.server;

import com.leclowndu93150.chisel.api.IChiselItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet for toggling fuzzy mode from client to server.
 */
public class ChiselFuzzyPacket {

    private final int slot;

    public ChiselFuzzyPacket(int slot) {
        this.slot = slot;
    }

    public static void encode(ChiselFuzzyPacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.slot);
    }

    public static ChiselFuzzyPacket decode(FriendlyByteBuf buf) {
        return new ChiselFuzzyPacket(buf.readVarInt());
    }

    public static void handle(ChiselFuzzyPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getInventory().getItem(packet.slot);
                if (stack.getItem() instanceof IChiselItem chiselItem) {
                    boolean newState = chiselItem.toggleFuzzyMode(stack);
                    if (newState) {
                        player.displayClientMessage(Component.translatable("chisel.message.fuzzy_enabled"), true);
                    } else {
                        player.displayClientMessage(Component.translatable("chisel.message.fuzzy_disabled"), true);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
