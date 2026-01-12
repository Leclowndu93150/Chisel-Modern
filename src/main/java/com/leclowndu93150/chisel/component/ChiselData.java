package com.leclowndu93150.chisel.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
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

    public static final Codec<ChiselData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("mode").forGetter(ChiselData::mode),
            Codec.INT.fieldOf("preview_type").forGetter(ChiselData::previewType),
            Codec.INT.fieldOf("selection_slot").forGetter(ChiselData::selectionSlot),
            Codec.INT.fieldOf("target_slot").forGetter(ChiselData::targetSlot),
            Codec.BOOL.fieldOf("rotate").forGetter(ChiselData::rotate),
            Codec.BOOL.optionalFieldOf("fuzzy", false).forGetter(ChiselData::fuzzy),
            ItemStack.OPTIONAL_CODEC.fieldOf("target").forGetter(ChiselData::target)
    ).apply(instance, ChiselData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, ChiselData> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public ChiselData decode(RegistryFriendlyByteBuf buf) {
            String mode = ByteBufCodecs.STRING_UTF8.decode(buf);
            int previewType = ByteBufCodecs.VAR_INT.decode(buf);
            int selectionSlot = ByteBufCodecs.VAR_INT.decode(buf);
            int targetSlot = ByteBufCodecs.VAR_INT.decode(buf);
            boolean rotate = ByteBufCodecs.BOOL.decode(buf);
            boolean fuzzy = ByteBufCodecs.BOOL.decode(buf);
            ItemStack target = ItemStack.OPTIONAL_STREAM_CODEC.decode(buf);
            return new ChiselData(mode, previewType, selectionSlot, targetSlot, rotate, fuzzy, target);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ChiselData data) {
            ByteBufCodecs.STRING_UTF8.encode(buf, data.mode());
            ByteBufCodecs.VAR_INT.encode(buf, data.previewType());
            ByteBufCodecs.VAR_INT.encode(buf, data.selectionSlot());
            ByteBufCodecs.VAR_INT.encode(buf, data.targetSlot());
            ByteBufCodecs.BOOL.encode(buf, data.rotate());
            ByteBufCodecs.BOOL.encode(buf, data.fuzzy());
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, data.target());
        }
    };

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
