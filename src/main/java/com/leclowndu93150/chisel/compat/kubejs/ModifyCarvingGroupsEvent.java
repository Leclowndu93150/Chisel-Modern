package com.leclowndu93150.chisel.compat.kubejs;

import dev.latvian.mods.kubejs.event.KubeEvent;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

/**
 * KubeJS event for modifying Chisel carving groups.
 * Fires during server/datapack reload.
 */
public class ModifyCarvingGroupsEvent implements KubeEvent {

    private final Map<ResourceLocation, CarvingGroupBuilder> groups = new LinkedHashMap<>();
    private final Set<ResourceLocation> removedGroups = new HashSet<>();
    private final Set<ResourceLocation> blocksRemovedFromAll = new HashSet<>();

    @Info("Get or create a carving group by ID. Use 'chisel:groupname' format or just 'groupname' for chisel namespace.")
    public CarvingGroupBuilder get(String groupId) {
        ResourceLocation id = parseId(groupId);
        return groups.computeIfAbsent(id, CarvingGroupBuilder::new);
    }

    @Info("Alias for get(), creates a new carving group or modifies existing one")
    public CarvingGroupBuilder create(String groupId) {
        return get(groupId);
    }

    @Info("Remove an entire carving group")
    public ModifyCarvingGroupsEvent remove(String groupId) {
        ResourceLocation id = parseId(groupId);
        removedGroups.add(id);
        groups.remove(id);
        return this;
    }

    @Info("Remove a block from ALL carving groups")
    public ModifyCarvingGroupsEvent removeFromAll(String blockId) {
        ResourceLocation id = parseBlockId(blockId);
        blocksRemovedFromAll.add(id);
        for (CarvingGroupBuilder builder : groups.values()) {
            builder.remove(blockId);
        }
        return this;
    }

    @Info("Check if a group exists or is being created")
    public boolean hasGroup(String groupId) {
        return groups.containsKey(parseId(groupId));
    }

    @Info("Get all group IDs being modified")
    public String[] getGroupIds() {
        return groups.keySet().stream()
                .map(ResourceLocation::toString)
                .toArray(String[]::new);
    }

    public Map<ResourceLocation, CarvingGroupBuilder> getGroups() {
        return Collections.unmodifiableMap(groups);
    }

    public Set<ResourceLocation> getRemovedGroups() {
        return Collections.unmodifiableSet(removedGroups);
    }

    public Set<ResourceLocation> getBlocksRemovedFromAll() {
        return Collections.unmodifiableSet(blocksRemovedFromAll);
    }

    private ResourceLocation parseId(String id) {
        if (id.contains(":")) {
            return ResourceLocation.parse(id);
        }
        return ResourceLocation.fromNamespaceAndPath("chisel", id);
    }

    private ResourceLocation parseBlockId(String id) {
        if (id.contains(":")) {
            return ResourceLocation.parse(id);
        }
        return ResourceLocation.fromNamespaceAndPath("minecraft", id);
    }
}
