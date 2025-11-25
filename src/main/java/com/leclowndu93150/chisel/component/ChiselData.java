package com.leclowndu93150.chisel.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Data component for storing chisel item data.
 * Replaces the old NBT-based storage system.
 */
public record ChiselData(
        String mode,
        int previewType,
        int selectionSlot,
        int targetSlot,
        boolean rotate
) {
    public static final ChiselData DEFAULT = new ChiselData("single", 0, -1, -1, false);

    public static final Codec<ChiselData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("mode").forGetter(ChiselData::mode),
            Codec.INT.fieldOf("preview_type").forGetter(ChiselData::previewType),
            Codec.INT.fieldOf("selection_slot").forGetter(ChiselData::selectionSlot),
            Codec.INT.fieldOf("target_slot").forGetter(ChiselData::targetSlot),
            Codec.BOOL.fieldOf("rotate").forGetter(ChiselData::rotate)
    ).apply(instance, ChiselData::new));

    public static final StreamCodec<ByteBuf, ChiselData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ChiselData::mode,
            ByteBufCodecs.VAR_INT, ChiselData::previewType,
            ByteBufCodecs.VAR_INT, ChiselData::selectionSlot,
            ByteBufCodecs.VAR_INT, ChiselData::targetSlot,
            ByteBufCodecs.BOOL, ChiselData::rotate,
            ChiselData::new
    );

    public ChiselData withMode(String newMode) {
        return new ChiselData(newMode, previewType, selectionSlot, targetSlot, rotate);
    }

    public ChiselData withPreviewType(int newPreviewType) {
        return new ChiselData(mode, newPreviewType, selectionSlot, targetSlot, rotate);
    }

    public ChiselData withSelectionSlot(int newSelectionSlot) {
        return new ChiselData(mode, previewType, newSelectionSlot, targetSlot, rotate);
    }

    public ChiselData withTargetSlot(int newTargetSlot) {
        return new ChiselData(mode, previewType, selectionSlot, newTargetSlot, rotate);
    }

    public ChiselData withRotate(boolean newRotate) {
        return new ChiselData(mode, previewType, selectionSlot, targetSlot, newRotate);
    }
}
