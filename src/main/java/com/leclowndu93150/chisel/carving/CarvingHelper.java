package com.leclowndu93150.chisel.carving;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.ChiselSound;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.compat.kubejs.KubeJSCompat;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

        for (ResourceLocation tagId : BuiltInRegistries.BLOCK.getTagNames()
                .filter(tag -> tag.location().getNamespace().equals(Chisel.MODID) && tag.location().getPath().startsWith("carving/"))
                .map(TagKey::location)
                .toList()) {
            TagKey<Block> tag = BlockTags.create(tagId);
            if (state.is(tag)) {
                Boolean include = KubeJSCompat.shouldIncludeBlock(tag, block);
                if (include == null || include) {
                    return tag;
                }
            }
        }

        TagKey<Block> kubeGroup = KubeJSCompat.getGroupForBlock(block);
        if (kubeGroup != null) {
            return kubeGroup;
        }

        return null;
    }

    /**
     * Gets the carving group tag for an item stack.
     */
    @Nullable
    public static TagKey<Item> getCarvingGroupForItem(ItemStack stack) {
        Item item = stack.getItem();

        for (ResourceLocation tagId : BuiltInRegistries.ITEM.getTagNames()
                .filter(tag -> tag.location().getNamespace().equals(Chisel.MODID) && tag.location().getPath().startsWith("carving/"))
                .map(TagKey::location)
                .toList()) {
            TagKey<Item> tag = ItemTags.create(tagId);
            if (stack.is(tag)) {
                Block block = Block.byItem(item);
                TagKey<Block> blockTag = BlockTags.create(tagId);
                Boolean include = KubeJSCompat.shouldIncludeBlock(blockTag, block);
                if (include == null || include) {
                    return tag;
                }
            }
        }

        Block block = Block.byItem(item);
        if (block != null) {
            TagKey<Block> kubeGroup = KubeJSCompat.getGroupForBlock(block);
            if (kubeGroup != null) {
                return TagKey.create(Registries.ITEM, kubeGroup.location());
            }
        }

        return null;
    }

    /**
     * Gets all blocks in a carving group.
     * Includes KubeJS modifications if KubeJS is loaded.
     */
    public static List<Block> getBlocksInGroup(TagKey<Block> groupTag) {
        List<Block> blocks = new ArrayList<>();

        BuiltInRegistries.BLOCK.getTag(groupTag).ifPresent(tag -> {
            tag.forEach(holder -> {
                Block block = holder.value();
                Boolean include = KubeJSCompat.shouldIncludeBlock(groupTag, block);
                if (include == null || include) {
                    blocks.add(block);
                }
            });
        });

        for (ResourceLocation blockId : KubeJSCompat.getAdditionalBlocks(groupTag)) {
            Block block = BuiltInRegistries.BLOCK.get(blockId);
            if (block != null && !blocks.contains(block)) {
                blocks.add(block);
            }
        }

        return blocks;
    }

    /**
     * Gets all items in a carving group.
     * Includes KubeJS modifications if KubeJS is loaded.
     */
    public static List<Item> getItemsInGroup(TagKey<Item> groupTag) {
        TagKey<Block> blockTag = TagKey.create(Registries.BLOCK, groupTag.location());

        List<Item> items = new ArrayList<>();
        BuiltInRegistries.ITEM.getTag(groupTag).ifPresent(tag -> {
            tag.forEach(holder -> {
                Item item = holder.value();
                Block block = Block.byItem(item);
                Boolean include = KubeJSCompat.shouldIncludeBlock(blockTag, block);
                if (include == null || include) {
                    items.add(item);
                }
            });
        });

        for (ResourceLocation blockId : KubeJSCompat.getAdditionalBlocks(blockTag)) {
            Block block = BuiltInRegistries.BLOCK.get(blockId);
            if (block != null) {
                Item item = block.asItem();
                if (item != Items.AIR && !items.contains(item)) {
                    items.add(item);
                }
            }
        }

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
     * Plays the chisel sound effect with default fallback sound.
     */
    public static void playChiselSound(Level level, Player player) {
        playChiselSound(level, player, ChiselSound.FALLBACK);
    }

    /**
     * Plays the chisel sound effect for a specific block.
     * Looks up the block type to determine which sound to play.
     */
    public static void playChiselSound(Level level, Player player, Block block) {
        ChiselSound sound = getChiselSoundForBlock(block);
        playChiselSound(level, player, sound);
    }

    /**
     * Plays a specific chisel sound.
     */
    public static void playChiselSound(Level level, Player player, ChiselSound chiselSound) {
        if (chiselSound == null) {
            chiselSound = ChiselSound.FALLBACK;
        }
        float randomValue = level.random.nextFloat();
        level.playSound(player, player.blockPosition(), chiselSound.getSound(),
                SoundSource.PLAYERS, chiselSound.getVolume(randomValue), chiselSound.getPitch(randomValue));
    }

    /**
     * Gets the appropriate chiseling sound for a block.
     * Looks up the ChiselBlockType to find the configured sound.
     */
    @Nullable
    public static ChiselSound getChiselSoundForBlock(Block block) {
        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            for (var deferredBlock : blockType.getAllBlocks()) {
                if (deferredBlock.get() == block) {
                    ChiselSound sound = blockType.getChiselSound();
                    return sound != null ? sound : ChiselSound.FALLBACK;
                }
            }
        }
        return ChiselSound.FALLBACK;
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
        if (item == Items.AIR) {
            return null;
        }
        return getCarvingGroupForItem(new ItemStack(item));
    }
}
