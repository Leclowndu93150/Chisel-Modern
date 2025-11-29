package com.leclowndu93150.chisel.network.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Packet for spawning auto chisel completion effects on clients.
 */
public class AutoChiselFXPacket {

    private final BlockPos pos;
    private final ItemStack chisel;
    private final BlockState state;

    public AutoChiselFXPacket(BlockPos pos, ItemStack chisel, BlockState state) {
        this.pos = pos;
        this.chisel = chisel;
        this.state = state;
    }

    public static void encode(AutoChiselFXPacket packet, FriendlyByteBuf buf) {
        buf.writeLong(packet.pos.asLong());
        buf.writeItem(packet.chisel);
        buf.writeVarInt(Block.getId(packet.state));
    }

    public static AutoChiselFXPacket decode(FriendlyByteBuf buf) {
        return new AutoChiselFXPacket(
                BlockPos.of(buf.readLong()),
                buf.readItem(),
                Block.stateById(buf.readVarInt())
        );
    }

    @OnlyIn(Dist.CLIENT)
    public static void handle(AutoChiselFXPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            if (level != null && level.hasChunkAt(packet.pos)) {
                double x = packet.pos.getX() + 0.5;
                double y = packet.pos.getY() + 1.0;
                double z = packet.pos.getZ() + 0.5;

                for (int i = 0; i < 5; i++) {
                    double dx = (level.random.nextDouble() - 0.5) * 0.3;
                    double dy = level.random.nextDouble() * 0.2;
                    double dz = (level.random.nextDouble() - 0.5) * 0.3;
                    level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, packet.state), x, y, z, dx, dy, dz);
                }

                level.playLocalSound(packet.pos, SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 0.5F, 1.0F, false);
            }
        });
        context.setPacketHandled(true);
    }
}
