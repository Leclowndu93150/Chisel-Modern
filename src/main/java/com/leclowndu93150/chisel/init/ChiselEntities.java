package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.entity.BallOMossEntity;
import com.leclowndu93150.chisel.entity.CloudInABottleEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChiselEntities {

    public static final DeferredHolder<EntityType<?>, EntityType<BallOMossEntity>> BALL_O_MOSS =
            ChiselRegistries.ENTITY_TYPES.register("ball_o_moss",
                    () -> EntityType.Builder.<BallOMossEntity>of(BallOMossEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, Chisel.id("ball_o_moss"))));

    public static final DeferredHolder<EntityType<?>, EntityType<CloudInABottleEntity>> CLOUD_IN_A_BOTTLE =
            ChiselRegistries.ENTITY_TYPES.register("cloud_in_a_bottle",
                    () -> EntityType.Builder.<CloudInABottleEntity>of(CloudInABottleEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE, Chisel.id("cloud_in_a_bottle"))));

    public static void init() {
    }
}
