package com.leclowndu93150.chisel.entity;

import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselEntities;
import com.leclowndu93150.chisel.init.ChiselItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.RegistryObject;

public class CloudInABottleEntity extends ThrowableItemProjectile {

    public CloudInABottleEntity(EntityType<? extends CloudInABottleEntity> entityType, Level level) {
        super(entityType, level);
    }

    public CloudInABottleEntity(Level level, LivingEntity shooter) {
        super(ChiselEntities.CLOUD_IN_A_BOTTLE.get(), shooter, level);
    }

    public CloudInABottleEntity(Level level, double x, double y, double z) {
        super(ChiselEntities.CLOUD_IN_A_BOTTLE.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ChiselItems.CLOUD_IN_A_BOTTLE.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        Level level = level();
        BlockPos hitPos = result.getBlockPos().relative(result.getDirection());

        if (!level.isClientSide) {
            generateCloud(level, level.random, hitPos.getX(), hitPos.getY(), hitPos.getZ(), 40);

            level.playSound(null, hitPos, SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);
        }

        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        discard();
    }

    private void generateCloud(Level level, RandomSource random, int gx, int gy, int gz, int numberOfBlocks) {
        int[] X = new int[9];
        int[] Y = new int[9];
        int[] Z = new int[9];

        for (int dir = 0; dir < 9; dir++) {
            X[dir] = gx;
            Y[dir] = gy;
            Z[dir] = gz;
        }

        Block cloudBlock = getCloudBlock();
        if (cloudBlock == null) {
            return;
        }

        BlockState cloudState = cloudBlock.defaultBlockState();
        int count = 0;

        while (count < numberOfBlocks) {
            for (int dir = 0; dir < 9; dir++) {
                if (count >= numberOfBlocks) {
                    break;
                }

                int dx = dir % 3 - 1;
                int dz = dir / 3 - 1;

                if (dx == 0 && dz == 0) {
                    continue;
                }

                X[dir] += random.nextInt(3) - 1 + dx;
                Z[dir] += random.nextInt(3) - 1 + dz;
                Y[dir] += random.nextInt(2) * (random.nextInt(3) - 1);

                int x = X[dir];
                int y = Y[dir];
                int z = Z[dir];

                for (int j2 = x; j2 < x + random.nextInt(4) + 1; j2++) {
                    for (int k2 = y; k2 < y + random.nextInt(1) + 2; k2++) {
                        for (int l2 = z; l2 < z + random.nextInt(4) + 1; l2++) {
                            BlockPos pos = new BlockPos(j2, k2, l2);
                            if (level.getBlockState(pos).isAir() &&
                                    Math.abs(j2 - x) + Math.abs(k2 - y) + Math.abs(l2 - z) < 4 + random.nextInt(2)) {
                                level.setBlock(pos, cloudState, 3);
                                count++;
                            }
                        }
                    }
                }
            }
        }
    }

    private Block getCloudBlock() {
        if (ChiselBlocks.CLOUD != null) {
            RegistryObject<? extends Block> cloudBlock = ChiselBlocks.CLOUD.getBlock("cloud");
            if (cloudBlock != null) {
                return cloudBlock.get();
            }
        }
        return null;
    }
}
