package com.leclowndu93150.chisel.network.server;

import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.leclowndu93150.chisel.carving.ChiselMode;
import com.leclowndu93150.chisel.carving.ChiselModeRegistry;
import com.leclowndu93150.chisel.item.ItemChisel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet for synchronizing chisel mode from client to server.
 */
public class ChiselModePacket {

    private final int slot;
    private final String modeName;

    public ChiselModePacket(int slot, String modeName) {
        this.slot = slot;
        this.modeName = modeName;
    }

    public ChiselModePacket(int slot, IChiselMode mode) {
        this(slot, mode.name());
    }

    public static void encode(ChiselModePacket packet, FriendlyByteBuf buf) {
        buf.writeVarInt(packet.slot);
        buf.writeUtf(packet.modeName);
    }

    public static ChiselModePacket decode(FriendlyByteBuf buf) {
        return new ChiselModePacket(buf.readVarInt(), buf.readUtf());
    }

    public static void handle(ChiselModePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getInventory().getItem(packet.slot);
                IChiselMode mode = ChiselModeRegistry.INSTANCE.getModeByName(packet.modeName);
                if (mode == null) {
                    mode = ChiselMode.SINGLE;
                }
                if (stack.getItem() instanceof IChiselItem chiselItem && chiselItem.supportsMode(player, stack, mode)) {
                    if (stack.getItem() instanceof ItemChisel itemChisel) {
                        itemChisel.setMode(stack, mode);
                    }
                }
            }
        });
        context.setPacketHandled(true);
    }
}
