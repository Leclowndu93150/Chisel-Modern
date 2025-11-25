package com.leclowndu93150.chisel.carving;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.api.carving.ICarvingGroup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Implementation of ICarvingGroup for chisel block types.
 */
public class CarvingGroup implements ICarvingGroup {

    private final ChiselBlockType<?> blockType;

    public CarvingGroup(ChiselBlockType<?> blockType) {
        this.blockType = blockType;
    }

    @Override
    public ResourceLocation getId() {
        return Chisel.id("carving/" + blockType.getName());
    }

    @Override
    public String getTranslationKey() {
        return blockType.getGroupName() != null
                ? "chisel.group." + blockType.getName()
                : "block.chisel." + blockType.getName();
    }

    @Override
    public TagKey<Item> getItemTag() {
        TagKey<Block> blockTag = blockType.getCarvingGroupTag();
        if (blockTag != null) {
            return TagKey.create(
                Registries.ITEM,
                blockTag.location()
            );
        }
        return blockType.getItemTags().isEmpty()
            ? TagKey.create(Registries.ITEM, getId())
            : blockType.getItemTags().get(0);
    }

    @Override
    public TagKey<Block> getBlockTag() {
        TagKey<Block> carvingTag = blockType.getCarvingGroupTag();
        if (carvingTag != null) {
            return carvingTag;
        }
        return blockType.getBlockTags().isEmpty()
            ? TagKey.create(Registries.BLOCK, getId())
            : blockType.getBlockTags().get(0);
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.STONE_BREAK;
    }

    public ChiselBlockType<?> getBlockType() {
        return blockType;
    }
}
