package com.leclowndu93150.chisel.compat.kubejs;

// TEMPORARILY DISABLED FOR 1.20.1 BACKPORT - KubeJS not in dependencies

/*

import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.ReturnsSelf;
import net.minecraft.resources.ResourceLocation;

import java.util.*;


@ReturnsSelf
public class CarvingGroupBuilder {

    private final ResourceLocation groupId;
    private final Set<ResourceLocation> blocksToAdd = new LinkedHashSet<>();
    private final Set<ResourceLocation> blocksToRemove = new HashSet<>();
    private boolean clearExisting = false;
    private boolean replace = false;

    public CarvingGroupBuilder(ResourceLocation groupId) {
        this.groupId = groupId;
    }

    @Info("Add a block to this carving group")
    public CarvingGroupBuilder add(String blockId) {
        ResourceLocation id = parseBlockId(blockId);
        blocksToAdd.add(id);
        blocksToRemove.remove(id);
        return this;
    }

    @Info("Add multiple blocks to this carving group. Pass an array like ['minecraft:stone', 'minecraft:andesite']")
    public CarvingGroupBuilder addAll(Object[] blockIds) {
        for (Object blockId : blockIds) {
            add(blockId.toString());
        }
        return this;
    }

    @Info("Remove a block from this carving group")
    public CarvingGroupBuilder remove(String blockId) {
        ResourceLocation id = parseBlockId(blockId);
        blocksToRemove.add(id);
        blocksToAdd.remove(id);
        return this;
    }

    @Info("Remove multiple blocks from this carving group. Pass an array like ['minecraft:stone', 'minecraft:andesite']")
    public CarvingGroupBuilder removeAll(Object[] blockIds) {
        for (Object blockId : blockIds) {
            remove(blockId.toString());
        }
        return this;
    }

    @Info("Clear all existing blocks from this group before adding new ones")
    public CarvingGroupBuilder clear() {
        this.clearExisting = true;
        return this;
    }

    @Info("Replace the entire group with the specified blocks (equivalent to clear().addAll(...))")
    public CarvingGroupBuilder replace(Object[] blockIds) {
        this.replace = true;
        this.clearExisting = true;
        blocksToAdd.clear();
        return addAll(blockIds);
    }

    public ResourceLocation getGroupId() {
        return groupId;
    }

    public Set<ResourceLocation> getBlocksToAdd() {
        return Collections.unmodifiableSet(blocksToAdd);
    }

    public Set<ResourceLocation> getBlocksToRemove() {
        return Collections.unmodifiableSet(blocksToRemove);
    }

    public boolean shouldClearExisting() {
        return clearExisting;
    }

    public boolean isReplace() {
        return replace;
    }

    private ResourceLocation parseBlockId(String id) {
        if (id.contains(":")) {
            return ResourceLocation.parse(id);
        }
        return ResourceLocation.fromNamespaceAndPath("minecraft", id);
    }
}
*/
