package com.leclowndu93150.chisel.compat.kubejs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Handler that directly interacts with KubeJS classes.
 * This class is ONLY loaded when KubeJS is present.
 * KubeJSCompat ensures this class is never referenced unless KubeJS is installed.
 */
public class KubeJSHandler {

    public KubeJSHandler() {
        KubeJSEventHandler.register();
    }

    @Nullable
    public Boolean shouldIncludeBlock(TagKey<Block> groupTag, Block block) {
        return KubeJSEventHandler.shouldIncludeBlock(groupTag, block);
    }

    public Set<ResourceLocation> getAdditionalBlocks(TagKey<Block> groupTag) {
        return KubeJSEventHandler.getAdditionalBlocks(groupTag);
    }

    public boolean isGroupRemoved(ResourceLocation groupId) {
        return KubeJSEventHandler.isGroupRemoved(groupId);
    }

    @Nullable
    public TagKey<Block> getGroupForBlock(Block block) {
        return KubeJSEventHandler.getGroupForBlock(block);
    }

    public void clearCache() {
        KubeJSEventHandler.clearCache();
    }

    public Map<ResourceLocation, Set<ResourceLocation>> getCustomGroupsForJEI() {
        Map<ResourceLocation, CarvingGroupBuilder> groups = KubeJSEventHandler.getCustomGroups();
        Map<ResourceLocation, Set<ResourceLocation>> result = new HashMap<>();

        for (Map.Entry<ResourceLocation, CarvingGroupBuilder> entry : groups.entrySet()) {
            CarvingGroupBuilder builder = entry.getValue();
            if (!builder.getBlocksToAdd().isEmpty()) {
                result.put(entry.getKey(), builder.getBlocksToAdd());
            }
        }

        return result;
    }
}
