package com.leclowndu93150.chisel.network;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.client.gui.HitechChiselScreen;
import com.leclowndu93150.chisel.item.ItemChisel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Payload for synchronizing hitech chisel preview settings.
 */
public record HitechSettingsPayload(byte previewType, boolean rotate, int chiselSlot) implements CustomPacketPayload {

    public static final Type<HitechSettingsPayload> TYPE = new Type<>(Chisel.id("hitech_settings"));

    public static final StreamCodec<FriendlyByteBuf, HitechSettingsPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BYTE, HitechSettingsPayload::previewType,
            ByteBufCodecs.BOOL, HitechSettingsPayload::rotate,
            ByteBufCodecs.VAR_INT, HitechSettingsPayload::chiselSlot,
            HitechSettingsPayload::new
    );

    public HitechSettingsPayload(HitechChiselScreen.PreviewMode mode, boolean rotate, int slot) {
        this((byte) mode.ordinal(), rotate, slot);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack stack = player.getInventory().getItem(chiselSlot);
                if (stack.getItem() instanceof ItemChisel itemChisel) {
                    itemChisel.setPreviewType(stack, previewType);
                    itemChisel.setRotate(stack, rotate);
                }
            }
        });
    }
}
