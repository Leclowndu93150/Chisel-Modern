package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.block.entity.AutoChiselBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ChiselBlockEntities {

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<AutoChiselBlockEntity>> AUTO_CHISEL =
            ChiselRegistries.BLOCK_ENTITY_TYPES.register("auto_chisel", () ->
                    new BlockEntityType<>(AutoChiselBlockEntity::new, ChiselBlocks.AUTO_CHISEL.get())
            );

    public static void init() {
    }
}
