package com.leclowndu93150.chisel.api.carving;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

/**
 * Represents a group of blocks that can be chiseled between each other.
 */
public interface ICarvingGroup {

    /**
     * Gets the unique identifier for this carving group.
     */
    ResourceLocation getId();

    /**
     * Gets the translation key for this group's display name.
     */
    String getTranslationKey();

    /**
     * Gets the display name component for this group.
     */
    default Component getDisplayName() {
        return Component.translatable(getTranslationKey());
    }

    /**
     * Gets the item tag for all items in this carving group.
     */
    TagKey<Item> getItemTag();

    /**
     * Gets the block tag for all blocks in this carving group.
     */
    TagKey<Block> getBlockTag();

    /**
     * Gets the sound to play when chiseling blocks in this group.
     */
    SoundEvent getSound();
}
