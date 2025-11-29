package com.leclowndu93150.chisel.component;

import net.minecraft.world.item.ItemStack;

public record ChiselData(
        String mode,
        int previewType,
        int selectionSlot,
        int targetSlot,
        boolean rotate,
        ItemStack target
) {
    public static final ChiselData DEFAULT = new ChiselData("single", 0, -1, -1, false, ItemStack.EMPTY);

//    public static final Codec<ChiselData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
//            Codec.STRING.fieldOf("mode").forGetter(ChiselData::mode),
//            Codec.INT.fieldOf("preview_type").forGetter(ChiselData::previewType),
//            Codec.INT.fieldOf("selection_slot").forGetter(ChiselData::selectionSlot),
//            Codec.INT.fieldOf("target_slot").forGetter(ChiselData::targetSlot),
//            Codec.BOOL.fieldOf("rotate").forGetter(ChiselData::rotate),
//            ItemStack.OPTIONAL_CODEC.fieldOf("target").forGetter(ChiselData::target)
//    ).apply(instance, ChiselData::new));

//    /**
//     * Encodes this ChiselData to a FriendlyByteBuf for network transmission.
//     */
//    public void toNetwork(FriendlyByteBuf buf) {
//        buf.writeUtf(mode);
//        buf.writeVarInt(previewType);
//        buf.writeVarInt(selectionSlot);
//        buf.writeVarInt(targetSlot);
//        buf.writeBoolean(rotate);
//        buf.writeItem(target);
//    }
//
//    /**
//     * Decodes a ChiselData from a FriendlyByteBuf.
//     */
//    public static ChiselData fromNetwork(FriendlyByteBuf buf) {
//        String mode = buf.readUtf();
//        int previewType = buf.readVarInt();
//        int selectionSlot = buf.readVarInt();
//        int targetSlot = buf.readVarInt();
//        boolean rotate = buf.readBoolean();
//        ItemStack target = buf.readItem();
//        return new ChiselData(mode, previewType, selectionSlot, targetSlot, rotate, target);
//    }

    public ChiselData withMode(String newMode) {
        return new ChiselData(newMode, previewType, selectionSlot, targetSlot, rotate, target);
    }

    public ChiselData withPreviewType(int newPreviewType) {
        return new ChiselData(mode, newPreviewType, selectionSlot, targetSlot, rotate, target);
    }

    public ChiselData withSelectionSlot(int newSelectionSlot) {
        return new ChiselData(mode, previewType, newSelectionSlot, targetSlot, rotate, target);
    }

    public ChiselData withTargetSlot(int newTargetSlot) {
        return new ChiselData(mode, previewType, selectionSlot, newTargetSlot, rotate, target);
    }

    public ChiselData withRotate(boolean newRotate) {
        return new ChiselData(mode, previewType, selectionSlot, targetSlot, newRotate, target);
    }

    public ChiselData withTarget(ItemStack newTarget) {
        return new ChiselData(mode, previewType, selectionSlot, targetSlot, rotate, newTarget.copy());
    }
}
