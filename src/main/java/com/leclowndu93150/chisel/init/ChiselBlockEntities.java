package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.block.entity.AutoChiselBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registration class for Chisel block entities.
 */
public class ChiselBlockEntities {

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoChiselBlockEntity>> AUTO_CHISEL =
            ChiselRegistries.BLOCK_ENTITY_TYPES.register("auto_chisel", () ->
                    BlockEntityType.Builder.of(AutoChiselBlockEntity::new, ChiselBlocks.AUTO_CHISEL.get())
                            .build(null)
            );

    public static void init() {
    }
}
