package com.leclowndu93150.chisel.compat.kubejs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
// import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Safe wrapper for KubeJS compatibility.
 * All methods return safe defaults when KubeJS is not loaded.
 *
 * IMPORTANT: This class must NOT directly reference any KubeJS classes.
 * All KubeJS interaction goes through KubeJSHandler which is only loaded when KubeJS is present.
 *
 * TEMPORARILY DISABLED FOR 1.20.1 BACKPORT - KubeJS not in dependencies
 */
public class KubeJSCompat {

    // Temporarily disabled for 1.20.1 backport
    private static final boolean KUBEJS_LOADED = false; // ModList.get().isLoaded("kubejs");
    // private static KubeJSHandler handler;

    // private static KubeJSHandler getHandler() {
    //     if (handler == null && KUBEJS_LOADED) {
    //         handler = new KubeJSHandler();
    //     }
    //     return handler;
    // }

    /**
     * Check if a block should be included in a carving group.
     * @return true if should include, false if should exclude, null if no modification
     */
    @Nullable
    public static Boolean shouldIncludeBlock(TagKey<Block> groupTag, Block block) {
        // Temporarily disabled for 1.20.1 backport
        // KubeJSHandler h = getHandler();
        // return h != null ? h.shouldIncludeBlock(groupTag, block) : null;
        return null;
    }

    /**
     * Get additional blocks to add to a carving group.
     */
    public static Set<ResourceLocation> getAdditionalBlocks(TagKey<Block> groupTag) {
        // Temporarily disabled for 1.20.1 backport
        // KubeJSHandler h = getHandler();
        // return h != null ? h.getAdditionalBlocks(groupTag) : Collections.emptySet();
        return Collections.emptySet();
    }

    /**
     * Check if a group was removed via KubeJS.
     */
    public static boolean isGroupRemoved(ResourceLocation groupId) {
        // Temporarily disabled for 1.20.1 backport
        // KubeJSHandler h = getHandler();
        // return h != null && h.isGroupRemoved(groupId);
        return false;
    }

    /**
     * Get the KubeJS custom group that contains a block (for new groups created via KubeJS).
     */
    @Nullable
    public static TagKey<Block> getGroupForBlock(Block block) {
        // Temporarily disabled for 1.20.1 backport
        // KubeJSHandler h = getHandler();
        // return h != null ? h.getGroupForBlock(block) : null;
        return null;
    }

    /**
     * Clear cached data.
     */
    public static void clearCache() {
        // Temporarily disabled for 1.20.1 backport
        // KubeJSHandler h = getHandler();
        // if (h != null) {
        //     h.clearCache();
        // }
    }

    /**
     * Get all custom groups created via KubeJS for JEI integration.
     * Returns a map of group ID to the set of block IDs in that group.
     */
    public static Map<ResourceLocation, Set<ResourceLocation>> getCustomGroupsForJEI() {
        // Temporarily disabled for 1.20.1 backport
        // KubeJSHandler h = getHandler();
        // return h != null ? h.getCustomGroupsForJEI() : Collections.emptyMap();
        return Collections.emptyMap();
    }

    /**
     * Check if KubeJS is loaded.
     */
    public static boolean isLoaded() {
        return KUBEJS_LOADED;
    }
}
