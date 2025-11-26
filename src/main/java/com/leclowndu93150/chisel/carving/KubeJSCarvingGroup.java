package com.leclowndu93150.chisel.carving;

import com.leclowndu93150.chisel.api.carving.ICarvingGroup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.Set;

/**
 * Implementation of ICarvingGroup for virtual groups created via KubeJS.
 * These groups don't have actual datapack tags but are defined entirely in scripts.
 */
public class KubeJSCarvingGroup implements ICarvingGroup {

    private final ResourceLocation groupId;
    private final Set<ResourceLocation> blocks;

    public KubeJSCarvingGroup(ResourceLocation groupId, Set<ResourceLocation> blocks) {
        this.groupId = groupId;
        this.blocks = blocks;
    }

    @Override
    public ResourceLocation getId() {
        return groupId;
    }

    @Override
    public String getTranslationKey() {
        String path = groupId.getPath();
        if (path.startsWith("carving/")) {
            path = path.substring("carving/".length());
        }
        return "chisel.group." + path;
    }

    @Override
    public TagKey<Item> getItemTag() {
        return TagKey.create(Registries.ITEM, groupId);
    }

    @Override
    public TagKey<Block> getBlockTag() {
        return TagKey.create(Registries.BLOCK, groupId);
    }

    @Override
    public SoundEvent getSound() {
        return SoundEvents.STONE_BREAK;
    }

    /**
     * Gets the blocks defined in this virtual group.
     */
    public Set<ResourceLocation> getBlocks() {
        return blocks;
    }
}
