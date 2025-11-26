package com.leclowndu93150.chisel.compat.kubejs;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

/**
 * KubeJS event group for Chisel mod.
 *
 * Usage in server scripts:
 * <pre>
 * ChiselEvents.modifyGroups(event => {
 *     // Create or modify a carving group
 *     event.create('my_custom_group')
 *         .add('minecraft:stone')
 *         .add('minecraft:andesite')
 *         .add('minecraft:diorite');
 *
 *     // Modify existing group
 *     event.get('chisel:andesite')
 *         .add('modid:custom_andesite')
 *         .remove('minecraft:polished_andesite');
 *
 *     // Remove a block from all groups
 *     event.removeFromAll('minecraft:cobblestone');
 *
 *     // Delete an entire group
 *     event.remove('chisel:some_group');
 * });
 * </pre>
 */
public interface ChiselKubeJSEvents {

    EventGroup GROUP = EventGroup.of("ChiselEvents");

    /**
     * Server event that fires during datapack reload.
     * Use this to modify, create, or remove carving groups.
     */
    EventHandler MODIFY_GROUPS = GROUP.server("modifyGroups", () -> ModifyCarvingGroupsEvent.class);
}
