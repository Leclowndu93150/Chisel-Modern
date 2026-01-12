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
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.registries.RegistryObject;

public class BallOMossEntity extends ThrowableItemProjectile {

    public BallOMossEntity(EntityType<? extends BallOMossEntity> entityType, Level level) {
        super(entityType, level);
    }

    public BallOMossEntity(Level level, LivingEntity shooter) {
        super(ChiselEntities.BALL_O_MOSS.get(), shooter, level);
    }

    public BallOMossEntity(Level level, double x, double y, double z) {
        super(ChiselEntities.BALL_O_MOSS.get(), x, y, z, level);
    }

    @Override
    protected Item getDefaultItem() {
        return ChiselItems.BALL_O_MOSS.get();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);

        Level level = level();
        BlockPos hitPos = result.getBlockPos().relative(result.getDirection());

        if (level.isClientSide) {
            level.playLocalSound(hitPos.getX(), hitPos.getY(), hitPos.getZ(),
                    SoundEvents.SLIME_SQUISH, SoundSource.NEUTRAL, 1.0F, 1.0F, false);
        } else {
            int radius = 5;
            int falloff = 3;
            RandomSource rand = level.random;

            for (int xx = -radius; xx < radius; xx++) {
                for (int yy = -radius; yy < radius; yy++) {
                    for (int zz = -radius; zz < radius; zz++) {
                        double dist = Math.abs(xx) + Math.abs(yy) + Math.abs(zz);

                        if (!(dist < falloff || rand.nextInt(radius * 3 - falloff) >= dist * 2)) {
                            continue;
                        }

                        turnToMoss(level, hitPos.offset(xx, yy, zz));
                    }
                }
            }
        }

        discard();
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result);
        discard();
    }

    private void turnToMoss(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();
        BlockState newState = null;

        // Vanilla conversions
        if (block == Blocks.COBBLESTONE) {
            newState = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
        } else if (block == Blocks.COBBLESTONE_WALL) {
            newState = Blocks.MOSSY_COBBLESTONE_WALL.defaultBlockState();
        } else if (block == Blocks.STONE_BRICKS) {
            newState = Blocks.MOSSY_STONE_BRICKS.defaultBlockState();
        } else if (block == Blocks.STONE_BRICK_WALL) {
            newState = Blocks.MOSSY_STONE_BRICK_WALL.defaultBlockState();
        } else if (block == Blocks.STONE_BRICK_STAIRS) {
            newState = Blocks.MOSSY_STONE_BRICK_STAIRS.withPropertiesOf(state);
        } else if (block == Blocks.STONE_BRICK_SLAB) {
            newState = Blocks.MOSSY_STONE_BRICK_SLAB.withPropertiesOf(state);
        } else if (block == Blocks.COBBLESTONE_STAIRS) {
            newState = Blocks.MOSSY_COBBLESTONE_STAIRS.withPropertiesOf(state);
        } else if (block == Blocks.COBBLESTONE_SLAB) {
            newState = Blocks.MOSSY_COBBLESTONE_SLAB.withPropertiesOf(state);
        }

        // Chisel cobblestone conversions
        if (newState == null && ChiselBlocks.COBBLESTONE != null) {
            for (RegistryObject<? extends Block> registryObject : ChiselBlocks.COBBLESTONE.getAllBlocks()) {
                if (registryObject.get() == block) {
                    String variantName = getVariantName(registryObject);
                    RegistryObject<? extends Block> mossyVariant = ChiselBlocks.COBBLESTONE_MOSSY.getBlock(variantName);
                    if (mossyVariant != null) {
                        newState = mossyVariant.get().defaultBlockState();
                    }
                    break;
                }
            }
        }

        // Chisel temple block conversions
        if (newState == null && ChiselBlocks.TEMPLE != null) {
            for (RegistryObject<? extends Block> registryObject : ChiselBlocks.TEMPLE.getAllBlocks()) {
                if (registryObject.get() == block) {
                    String variantName = getVariantName(registryObject);
                    RegistryObject<? extends Block> mossyVariant = ChiselBlocks.TEMPLE_MOSSY.getBlock(variantName);
                    if (mossyVariant != null) {
                        newState = mossyVariant.get().defaultBlockState();
                    }
                    break;
                }
            }
        }

        if (newState != null && newState != state) {
            level.setBlock(pos, newState, 3);
        }
    }

    private String getVariantName(RegistryObject<? extends Block> block) {
        String fullName = block.getId().getPath();
        int lastSlash = fullName.lastIndexOf('/');
        return lastSlash >= 0 ? fullName.substring(lastSlash + 1) : fullName;
    }
}
