package com.leclowndu93150.chisel.carving;

import com.leclowndu93150.chisel.Chisel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for carving operations.
 * Handles looking up carving groups and finding variant blocks.
 */
public class CarvingHelper {

    /**
     * Gets the carving group tag for a block state.
     */
    @Nullable
    public static TagKey<Block> getCarvingGroup(BlockState state) {
        Block block = state.getBlock();
        ResourceLocation blockId = BuiltInRegistries.BLOCK.getKey(block);

        // Check all carving tags to find which group this block belongs to
        for (ResourceLocation tagId : BuiltInRegistries.BLOCK.getTagNames()
                .filter(tag -> tag.location().getNamespace().equals(Chisel.MODID) && tag.location().getPath().startsWith("carving/"))
                .map(TagKey::location)
                .toList()) {
            TagKey<Block> tag = BlockTags.create(tagId);
            if (state.is(tag)) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Gets the carving group tag for an item stack.
     */
    @Nullable
    public static TagKey<Item> getCarvingGroupForItem(ItemStack stack) {
        Item item = stack.getItem();

        // Check all carving tags to find which group this item belongs to
        for (ResourceLocation tagId : BuiltInRegistries.ITEM.getTagNames()
                .filter(tag -> tag.location().getNamespace().equals(Chisel.MODID) && tag.location().getPath().startsWith("carving/"))
                .map(TagKey::location)
                .toList()) {
            TagKey<Item> tag = ItemTags.create(tagId);
            if (stack.is(tag)) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Gets all blocks in a carving group.
     */
    public static List<Block> getBlocksInGroup(TagKey<Block> groupTag) {
        List<Block> blocks = new ArrayList<>();
        BuiltInRegistries.BLOCK.getTag(groupTag).ifPresent(tag -> {
            tag.forEach(holder -> blocks.add(holder.value()));
        });
        return blocks;
    }

    /**
     * Gets all items in a carving group.
     */
    public static List<Item> getItemsInGroup(TagKey<Item> groupTag) {
        List<Item> items = new ArrayList<>();
        BuiltInRegistries.ITEM.getTag(groupTag).ifPresent(tag -> {
            tag.forEach(holder -> items.add(holder.value()));
        });
        return items;
    }

    /**
     * Gets the next block variant in the carving group.
     */
    @Nullable
    public static Block getNextVariant(BlockState currentState) {
        TagKey<Block> group = getCarvingGroup(currentState);
        if (group == null) return null;

        List<Block> blocks = getBlocksInGroup(group);
        if (blocks.isEmpty()) return null;

        Block currentBlock = currentState.getBlock();
        int currentIndex = blocks.indexOf(currentBlock);

        if (currentIndex == -1) {
            return blocks.get(0);
        }

        return blocks.get((currentIndex + 1) % blocks.size());
    }

    /**
     * Gets the previous block variant in the carving group.
     */
    @Nullable
    public static Block getPreviousVariant(BlockState currentState) {
        TagKey<Block> group = getCarvingGroup(currentState);
        if (group == null) return null;

        List<Block> blocks = getBlocksInGroup(group);
        if (blocks.isEmpty()) return null;

        Block currentBlock = currentState.getBlock();
        int currentIndex = blocks.indexOf(currentBlock);

        if (currentIndex == -1) {
            return blocks.get(blocks.size() - 1);
        }

        return blocks.get((currentIndex - 1 + blocks.size()) % blocks.size());
    }

    /**
     * Gets a specific variant by index in the carving group.
     */
    @Nullable
    public static Block getVariantByIndex(BlockState currentState, int index) {
        TagKey<Block> group = getCarvingGroup(currentState);
        if (group == null) return null;

        List<Block> blocks = getBlocksInGroup(group);
        if (blocks.isEmpty() || index < 0 || index >= blocks.size()) return null;

        return blocks.get(index);
    }

    /**
     * Checks if two blocks are in the same carving group.
     */
    public static boolean areInSameGroup(BlockState state1, BlockState state2) {
        TagKey<Block> group1 = getCarvingGroup(state1);
        TagKey<Block> group2 = getCarvingGroup(state2);

        if (group1 == null || group2 == null) return false;
        return group1.location().equals(group2.location());
    }

    /**
     * Plays the chisel sound effect.
     */
    public static void playChiselSound(Level level, Player player) {
        level.playSound(player, player.blockPosition(), SoundEvents.UI_STONECUTTER_TAKE_RESULT,
                SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    /**
     * Checks if a block can be chiseled (is in any carving group).
     */
    public static boolean canChisel(BlockState state) {
        return getCarvingGroup(state) != null;
    }

    /**
     * Checks if an item can be chiseled (is in any carving group).
     */
    public static boolean canChisel(ItemStack stack) {
        return getCarvingGroupForItem(stack) != null;
    }

    /**
     * Gets the carving group TagKey for a block (as an item tag).
     */
    @Nullable
    public static TagKey<Item> getCarvingGroupForBlock(Block block) {
        Item item = block.asItem();
        if (item == net.minecraft.world.item.Items.AIR) {
            return null;
        }
        return getCarvingGroupForItem(new ItemStack(item));
    }
}
