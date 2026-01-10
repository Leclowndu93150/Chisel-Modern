package com.leclowndu93150.chisel.network.client;

import com.leclowndu93150.chisel.api.chunkdata.ChunkData;
import com.leclowndu93150.chisel.api.chunkdata.OffsetData;
import com.leclowndu93150.chisel.network.AutoChiselFXPayload;
import com.leclowndu93150.chisel.network.ChunkDataPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Client-side handler for network payloads.
 */
public class ClientPayloadHandler {

    public static void handleAutoChiselFX(AutoChiselFXPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var level = Minecraft.getInstance().level;
            if (level != null && level.hasChunkAt(payload.pos())) {
                double x = payload.pos().getX() + 0.5;
                double y = payload.pos().getY() + 1.0;
                double z = payload.pos().getZ() + 0.5;

                for (int i = 0; i < 5; i++) {
                    double dx = (level.random.nextDouble() - 0.5) * 0.3;
                    double dy = level.random.nextDouble() * 0.2;
                    double dz = (level.random.nextDouble() - 0.5) * 0.3;
                    level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, payload.state()), x, y, z, dx, dy, dz);
                }

                level.playLocalSound(payload.pos(), SoundEvents.UI_STONECUTTER_TAKE_RESULT, SoundSource.BLOCKS, 0.5F, 1.0F, false);
            }
        });
    }

    public static void handleChunkData(ChunkDataPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.level != null && mc.level.dimension().equals(payload.dimension())) {
                ChunkPos chunkPos = new ChunkPos(payload.chunkX(), payload.chunkZ());

                if (payload.data().isEmpty()) {
                    ChunkData.removeData(mc.level, chunkPos);
                } else {
                    OffsetData offsetData = new OffsetData();
                    offsetData.readFromNBT(payload.data());
                    ChunkData.setData(mc.level, chunkPos, offsetData);
                }

                int minSection = mc.level.getMinSection();
                int maxSection = mc.level.getMaxSection();

                for (int dx = -1; dx <= 1; dx++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        for (int sectionY = minSection; sectionY <= maxSection; sectionY++) {
                            mc.levelRenderer.setSectionDirty(payload.chunkX() + dx, sectionY, payload.chunkZ() + dz);
                        }
                    }
                }
            }
        });
    }
}
