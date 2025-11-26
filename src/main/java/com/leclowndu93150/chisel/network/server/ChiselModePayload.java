package com.leclowndu93150.chisel.network.server;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.IChiselItem;
import com.leclowndu93150.chisel.api.carving.IChiselMode;
import com.leclowndu93150.chisel.carving.ChiselMode;
import com.leclowndu93150.chisel.carving.ChiselModeRegistry;
import com.leclowndu93150.chisel.item.ItemChisel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Payload for synchronizing chisel mode from client to server.
 */
public record ChiselModePayload(int slot, String modeName) implements CustomPacketPayload {

    public static final Type<ChiselModePayload> TYPE = new Type<>(Chisel.id("chisel_mode"));

    public static final StreamCodec<FriendlyByteBuf, ChiselModePayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, ChiselModePayload::slot,
            ByteBufCodecs.STRING_UTF8, ChiselModePayload::modeName,
            ChiselModePayload::new
    );

    public ChiselModePayload(int slot, IChiselMode mode) {
        this(slot, mode.name());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack stack = player.getInventory().getItem(slot);
                IChiselMode mode = ChiselModeRegistry.INSTANCE.getModeByName(modeName);
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
    }
}
