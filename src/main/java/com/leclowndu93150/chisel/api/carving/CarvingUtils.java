package com.leclowndu93150.chisel.api.carving;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.carving.ChiselModeRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Utility class for carving operations.
 */
public class CarvingUtils {

    @Nullable
    private static IModeRegistry modeRegistry;

    /**
     * Gets the mode registry instance.
     */
    public static IModeRegistry getModeRegistry() {
        if (modeRegistry == null) {
            modeRegistry = ChiselModeRegistry.INSTANCE;
        }
        return modeRegistry;
    }

    /**
     * Compares two ItemStacks for equality including NBT.
     */
    public static boolean stacksEqual(ItemStack stack1, ItemStack stack2) {
        return ItemStack.matches(stack1, stack2);
    }

    /**
     * Computes a hash code for an ItemStack including NBT.
     */
    public static int hashStack(ItemStack stack) {
        return Objects.hash(stack.getItem(), stack.getComponentsPatch());
    }

    /**
     * Creates a carving group from a block tag.
     */
    public static ICarvingGroup createGroup(ResourceLocation id, String displayName) {
        return createGroup(id, displayName, SoundEvents.UI_STONECUTTER_TAKE_RESULT);
    }

    /**
     * Creates a carving group from a block tag with a custom sound.
     */
    public static ICarvingGroup createGroup(ResourceLocation id, String displayName, SoundEvent sound) {
        TagKey<Block> blockTag = BlockTags.create(id);
        TagKey<Item> itemTag = ItemTags.create(id);

        return new ICarvingGroup() {
            @Override
            public ResourceLocation getId() {
                return id;
            }

            @Override
            public String getTranslationKey() {
                return "chisel.group." + id.getPath().replace("/", ".");
            }

            @Override
            public Component getDisplayName() {
                return Component.literal(displayName);
            }

            @Override
            public TagKey<Item> getItemTag() {
                return itemTag;
            }

            @Override
            public TagKey<Block> getBlockTag() {
                return blockTag;
            }

            @Override
            public SoundEvent getSound() {
                return sound;
            }
        };
    }

    /**
     * Creates a carving group for a chisel block type.
     */
    public static ICarvingGroup createGroupForBlockType(String blockTypeName, String displayName) {
        return createGroup(Chisel.id("carving/" + blockTypeName.replace("/", "_")), displayName);
    }
}
