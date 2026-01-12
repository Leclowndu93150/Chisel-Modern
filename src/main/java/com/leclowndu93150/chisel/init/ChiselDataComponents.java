package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.component.ChiselData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Utility class for managing ChiselData using NBT (Forge 1.20.1 backport).
 * Mimics the data component API to minimize code changes.
 */
public class ChiselDataComponents {

    private static final String CHISEL_DATA_KEY = "ChiselData";
    private static final String MODE_KEY = "mode";
    private static final String PREVIEW_TYPE_KEY = "preview_type";
    private static final String SELECTION_SLOT_KEY = "selection_slot";
    private static final String TARGET_SLOT_KEY = "target_slot";
    private static final String ROTATE_KEY = "rotate";
    private static final String FUZZY_KEY = "fuzzy";
    private static final String TARGET_KEY = "target";

    /**
     * A fake "data component type" that provides get/set methods for ChiselData.
     * This mimics the NeoForge DataComponentType API using NBT storage.
     */
    public static class ChiselDataComponent {

        /**
         * Get ChiselData from an ItemStack's NBT.
         * Returns DEFAULT if not present.
         */
        public ChiselData get(ItemStack stack) {
            if (!stack.hasTag()) {
                return ChiselData.DEFAULT;
            }

            CompoundTag tag = stack.getTag();
            if (!tag.contains(CHISEL_DATA_KEY)) {
                return ChiselData.DEFAULT;
            }

            CompoundTag data = tag.getCompound(CHISEL_DATA_KEY);

            String mode = data.contains(MODE_KEY) ? data.getString(MODE_KEY) : "single";
            int previewType = data.contains(PREVIEW_TYPE_KEY) ? data.getInt(PREVIEW_TYPE_KEY) : 0;
            int selectionSlot = data.contains(SELECTION_SLOT_KEY) ? data.getInt(SELECTION_SLOT_KEY) : -1;
            int targetSlot = data.contains(TARGET_SLOT_KEY) ? data.getInt(TARGET_SLOT_KEY) : -1;
            boolean rotate = data.contains(ROTATE_KEY) && data.getBoolean(ROTATE_KEY);
            boolean fuzzy = data.contains(FUZZY_KEY) && data.getBoolean(FUZZY_KEY);

            ItemStack target = ItemStack.EMPTY;
            if (data.contains(TARGET_KEY)) {
                target = ItemStack.of(data.getCompound(TARGET_KEY));
            }

            return new ChiselData(mode, previewType, selectionSlot, targetSlot, rotate, fuzzy, target);
        }

        /**
         * Set ChiselData on an ItemStack's NBT.
         */
        public void set(ItemStack stack, ChiselData chiselData) {
            CompoundTag tag = stack.getOrCreateTag();
            CompoundTag data = new CompoundTag();

            data.putString(MODE_KEY, chiselData.mode());
            data.putInt(PREVIEW_TYPE_KEY, chiselData.previewType());
            data.putInt(SELECTION_SLOT_KEY, chiselData.selectionSlot());
            data.putInt(TARGET_SLOT_KEY, chiselData.targetSlot());
            data.putBoolean(ROTATE_KEY, chiselData.rotate());
            data.putBoolean(FUZZY_KEY, chiselData.fuzzy());

            if (!chiselData.target().isEmpty()) {
                CompoundTag targetTag = new CompoundTag();
                chiselData.target().save(targetTag);
                data.put(TARGET_KEY, targetTag);
            }

            tag.put(CHISEL_DATA_KEY, data);
        }
    }

    /**
     * The "registry object" that provides access to get/set methods.
     * Usage: stack.get(CHISEL_DATA.get()) becomes CHISEL_DATA.get().get(stack)
     * Usage: stack.set(CHISEL_DATA.get(), data) becomes CHISEL_DATA.get().set(stack, data)
     */
    public static class ChiselDataRegistryObject {
        private final ChiselDataComponent component = new ChiselDataComponent();

        public ChiselDataComponent get() {
            return component;
        }
    }

    public static final ChiselDataRegistryObject CHISEL_DATA = new ChiselDataRegistryObject();

    public static void init() {

    }
}
