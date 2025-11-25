package com.leclowndu93150.chisel.network;

import com.leclowndu93150.chisel.Chisel;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.handling.IPayloadContext;

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

    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            Level level = Minecraft.getInstance().level;
            if (level != null && level.hasChunkAt(pos)) {
                // Spawn block breaking particles
                double x = pos.getX() + 0.5;
                double y = pos.getY() + 1.0;
                double z = pos.getZ() + 0.5;

                for (int i = 0; i < 5; i++) {
                    double dx = (level.random.nextDouble() - 0.5) * 0.3;
                    double dy = level.random.nextDouble() * 0.2;
                    double dz = (level.random.nextDouble() - 0.5) * 0.3;
                    level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, state), x, y, z, dx, dy, dz);
                }

                // Play chisel sound
                level.playLocalSound(pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 0.5F, 1.0F, false);
            }
        });
    }
}
