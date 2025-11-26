package com.leclowndu93150.chisel.network;

import com.leclowndu93150.chisel.Chisel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Payload for spawning auto chisel completion effects on clients.
 */
public record AutoChiselFXPayload(BlockPos pos, ItemStack chisel, BlockState state) implements CustomPacketPayload {

    public static final Type<AutoChiselFXPayload> TYPE = new Type<>(Chisel.id("autochisel_fx"));

    public static final StreamCodec<RegistryFriendlyByteBuf, AutoChiselFXPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public AutoChiselFXPayload decode(RegistryFriendlyByteBuf buf) {
            return new AutoChiselFXPayload(
                    BlockPos.of(buf.readLong()),
                    ItemStack.OPTIONAL_STREAM_CODEC.decode(buf),
                    Block.stateById(buf.readVarInt())
            );
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, AutoChiselFXPayload payload) {
            buf.writeLong(payload.pos.asLong());
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, payload.chisel);
            buf.writeVarInt(Block.getId(payload.state));
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
