package com.leclowndu93150.chisel.network.server;

import com.leclowndu93150.chisel.item.ItemChisel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet for synchronizing hitech chisel preview settings.
 */
public class HitechSettingsPacket {

    private final byte previewType;
    private final boolean rotate;
    private final int chiselSlot;

    public HitechSettingsPacket(byte previewType, boolean rotate, int chiselSlot) {
        this.previewType = previewType;
        this.rotate = rotate;
        this.chiselSlot = chiselSlot;
    }

    public static void encode(HitechSettingsPacket packet, FriendlyByteBuf buf) {
        buf.writeByte(packet.previewType);
        buf.writeBoolean(packet.rotate);
        buf.writeVarInt(packet.chiselSlot);
    }

    public static HitechSettingsPacket decode(FriendlyByteBuf buf) {
        return new HitechSettingsPacket(buf.readByte(), buf.readBoolean(), buf.readVarInt());
    }

    public static void handle(HitechSettingsPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getInventory().getItem(packet.chiselSlot);
                if (stack.getItem() instanceof ItemChisel itemChisel) {
                    itemChisel.setPreviewType(stack, packet.previewType);
                    itemChisel.setRotate(stack, packet.rotate);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
