package com.leclowndu93150.chisel.network.server;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.item.ItemChisel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Payload for toggling fuzzy mode from client to server.
 */
public record ChiselFuzzyPayload(int slot) implements CustomPacketPayload {

    public static final Type<ChiselFuzzyPayload> TYPE = new Type<>(Chisel.id("chisel_fuzzy"));

    public static final StreamCodec<FriendlyByteBuf, ChiselFuzzyPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ChiselFuzzyPayload::slot,
            ChiselFuzzyPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack stack = player.getInventory().getItem(slot);
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
    }
}
