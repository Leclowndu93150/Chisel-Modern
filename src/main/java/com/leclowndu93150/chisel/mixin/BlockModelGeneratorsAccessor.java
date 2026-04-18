package com.leclowndu93150.chisel.mixin;

import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.data.models.ItemModelOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockModelGenerators.class)
public interface BlockModelGeneratorsAccessor {

    @Invoker("plainModel")
    static Variant chisel$plainModel(Identifier model) {
        throw new AssertionError();
    }

    @Invoker("plainVariant")
    static MultiVariant chisel$plainVariant(Identifier model) {
        throw new AssertionError();
    }

    @Invoker("createSimpleBlock")
    static MultiVariantGenerator chisel$createSimpleBlock(Block block, MultiVariant variant) {
        throw new AssertionError();
    }

    @Accessor("itemModelOutput")
    ItemModelOutput chisel$getItemModelOutput();

    @Invoker("createFlatItemModel")
    Identifier chisel$createFlatItemModel(Item item);
}
