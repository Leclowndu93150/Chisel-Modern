package com.leclowndu93150.chisel.component;

import net.minecraft.world.item.ItemStack;

public record ChiselData(
        String mode,
        int previewType,
        int selectionSlot,
        int targetSlot,
        boolean rotate,
        boolean fuzzy,
        ItemStack target
) {
    public static final ChiselData DEFAULT = new ChiselData("single", 0, -1, -1, false, false, ItemStack.EMPTY);

    public ChiselData withMode(String newMode) {
        return new ChiselData(newMode, previewType, selectionSlot, targetSlot, rotate, fuzzy, target);
    }

    public ChiselData withPreviewType(int newPreviewType) {
        return new ChiselData(mode, newPreviewType, selectionSlot, targetSlot, rotate, fuzzy, target);
    }

    public ChiselData withSelectionSlot(int newSelectionSlot) {
        return new ChiselData(mode, previewType, newSelectionSlot, targetSlot, rotate, fuzzy, target);
    }

    public ChiselData withTargetSlot(int newTargetSlot) {
        return new ChiselData(mode, previewType, selectionSlot, newTargetSlot, rotate, fuzzy, target);
    }

    public ChiselData withRotate(boolean newRotate) {
        return new ChiselData(mode, previewType, selectionSlot, targetSlot, newRotate, fuzzy, target);
    }

    public ChiselData withFuzzy(boolean newFuzzy) {
        return new ChiselData(mode, previewType, selectionSlot, targetSlot, rotate, newFuzzy, target);
    }

    public ChiselData withTarget(ItemStack newTarget) {
        return new ChiselData(mode, previewType, selectionSlot, targetSlot, rotate, fuzzy, newTarget.copy());
    }
}
