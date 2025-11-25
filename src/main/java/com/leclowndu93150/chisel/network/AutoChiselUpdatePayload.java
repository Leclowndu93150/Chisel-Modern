package com.leclowndu93150.chisel.network;

import com.leclowndu93150.chisel.Chisel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Payload for updating auto chisel source item on clients.
 */
public record AutoChiselUpdatePayload(BlockPos pos, ItemStack stack) implements CustomPacketPayload {

    public static final Type<AutoChiselUpdatePayload> TYPE = new Type<>(Chisel.id("autochisel_update"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AutoChiselUpdatePayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AutoChiselUpdatePayload decode(RegistryFriendlyByteBuf buf) {
            return new AutoChiselUpdatePayload(BlockPos.of(buf.readLong()), ItemStack.OPTIONAL_STREAM_CODEC.decode(buf));
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, AutoChiselUpdatePayload payload) {
            buf.writeLong(payload.pos.asLong());
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, payload.stack);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level != null && level.hasChunkAt(pos)) {
                // This packet can be used for more granular updates if needed
                // Currently, block entity sync handles most updates automatically
            }
        });
    }
}
