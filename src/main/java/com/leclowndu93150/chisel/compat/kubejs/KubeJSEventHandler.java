package com.leclowndu93150.chisel.compat.kubejs;

import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class KubeJSEventHandler {

    private static boolean registered = false;
    private static ModifyCarvingGroupsEvent lastEvent = null;

    public static void register() {
        if (registered) return;
        registered = true;
        MinecraftForge.EVENT_BUS.addListener(KubeJSEventHandler::onTagsUpdated);
    }

    private static void onTagsUpdated(TagsUpdatedEvent event) {
        if (event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.SERVER_DATA_LOAD) {
            fireModifyGroupsEvent();
        }
    }

    /**
     * Fires the KubeJS event and stores modifications for later processing.
     */
    public static void fireModifyGroupsEvent() {
        ModifyCarvingGroupsEvent kubeEvent = new ModifyCarvingGroupsEvent();
        ChiselKubeJSEvents.MODIFY_GROUPS.post(ScriptType.SERVER, kubeEvent);
        lastEvent = kubeEvent;
    }

    /**
     * Check if a block should be in a carving group based on KubeJS modifications.
     * Called during carving group resolution.
     *
     * @param groupTag The carving group tag
     * @param block The block to check
     * @return true if the block should be included, false if excluded, null if no modification
     */
    public static Boolean shouldIncludeBlock(TagKey<Block> groupTag, Block block) {
        if (lastEvent == null) {
            return null;
        }

        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);
        ResourceLocation groupId = groupTag.location();

        if (lastEvent.getBlocksRemovedFromAll().contains(blockId)) {
            return false;
        }

        if (lastEvent.getRemovedGroups().contains(groupId)) {
            return false;
        }

        CarvingGroupBuilder builder = lastEvent.getGroups().get(groupId);
        if (builder != null) {
            if (builder.shouldClearExisting() && !builder.getBlocksToAdd().contains(blockId)) {
                return false;
            }
            if (builder.getBlocksToRemove().contains(blockId)) {
                return false;
            }
            if (builder.getBlocksToAdd().contains(blockId)) {
                return true;
            }
        }

        return null;
    }

    /**
     * Get additional blocks to add to a carving group from KubeJS.
     * Works for both existing tags being modified and new virtual groups.
     *
     * @param groupTag The carving group tag
     * @return Set of block IDs to add, or empty set if none
     */
    public static Set<ResourceLocation> getAdditionalBlocks(TagKey<Block> groupTag) {
        if (lastEvent == null) {
            return Collections.emptySet();
        }

        ResourceLocation groupId = groupTag.location();
        CarvingGroupBuilder builder = lastEvent.getGroups().get(groupId);

        if (builder != null) {
            return builder.getBlocksToAdd();
        }

        for (Map.Entry<ResourceLocation, CarvingGroupBuilder> entry : lastEvent.getGroups().entrySet()) {
            if (entry.getKey().equals(groupId) ||
                (entry.getKey().getNamespace().equals("chisel") &&
                 groupId.getPath().equals(entry.getKey().getPath()))) {
                return entry.getValue().getBlocksToAdd();
            }
        }

        return Collections.emptySet();
    }

    /**
     * Get all custom groups created via KubeJS.
     *
     * @return Map of group ID to builder
     */
    public static Map<ResourceLocation, CarvingGroupBuilder> getCustomGroups() {
        if (lastEvent == null) {
            return Collections.emptyMap();
        }
        return lastEvent.getGroups();
    }

    /**
     * Check if a group was removed via KubeJS.
     */
    public static boolean isGroupRemoved(ResourceLocation groupId) {
        return lastEvent != null && lastEvent.getRemovedGroups().contains(groupId);
    }

    /**
     * Get the KubeJS custom group that contains a block.
     * This is for new groups created entirely via KubeJS (not modifying existing tags).
     */
    @Nullable
    public static TagKey<Block> getGroupForBlock(Block block) {
        if (lastEvent == null) {
            return null;
        }

        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(block);

        for (Map.Entry<ResourceLocation, CarvingGroupBuilder> entry : lastEvent.getGroups().entrySet()) {
            CarvingGroupBuilder builder = entry.getValue();
            if (builder.getBlocksToAdd().contains(blockId)) {
                return TagKey.create(Registries.BLOCK, entry.getKey());
            }
        }

        return null;
    }

    /**
     * Clear cached event data (called on server stop).
     */
    public static void clearCache() {
        lastEvent = null;
    }
}
