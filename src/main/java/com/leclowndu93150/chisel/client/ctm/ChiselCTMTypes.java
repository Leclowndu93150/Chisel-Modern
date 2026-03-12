package com.leclowndu93150.chisel.client.ctm;

import com.google.common.collect.ImmutableSet;
import com.supermartijn642.fusion.api.texture.FusionTextureTypeRegistry;
import com.supermartijn642.fusion.api.texture.TextureType;
import com.supermartijn642.fusion.api.util.Pair;
import net.minecraft.client.renderer.texture.SpriteLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import com.leclowndu93150.chisel.Chisel;

import java.util.EnumMap;
import java.util.Map;

public class ChiselCTMTypes {

    private static final Map<CTMTextureData.CTMType, TextureType<CTMTextureData>> TYPES = new EnumMap<>(CTMTextureData.CTMType.class);

    public static final TextureType<CTMTextureData> CTM = new CTMFullTextureType();
    public static final TextureType<CTMTextureData> CTMH = new CTMHTextureType();
    public static final TextureType<CTMTextureData> PILLAR = new PillarTextureType();
    public static final TextureType<CTMTextureData> SCTM = new SCTMTextureType();
    public static final TextureType<CTMTextureData> PATTERN = new PatternTextureType();
    public static final TextureType<CTMTextureData> RANDOM = new RandomTextureType();
    public static final TextureType<CTMTextureData> AR = new AlterRTextureType();
    public static final TextureType<CTMTextureData> ELDRITCH = new EldritchTextureType();

    static {
        TYPES.put(CTMTextureData.CTMType.CTM, CTM);
        TYPES.put(CTMTextureData.CTMType.CTMH, CTMH);
        TYPES.put(CTMTextureData.CTMType.PILLAR, PILLAR);
        TYPES.put(CTMTextureData.CTMType.SCTM, SCTM);
        TYPES.put(CTMTextureData.CTMType.PATTERN, PATTERN);
        TYPES.put(CTMTextureData.CTMType.RANDOM, RANDOM);
        TYPES.put(CTMTextureData.CTMType.AR, AR);
        TYPES.put(CTMTextureData.CTMType.ELDRITCH, ELDRITCH);
    }

    @SuppressWarnings("unchecked")
    public static Pair<TextureType<Object>, Object> resolve(CTMTextureData data) {
        TextureType<CTMTextureData> textureType = TYPES.get(data.getType());
        if (textureType == null) {
            textureType = CTM;
        }
        return Pair.of((TextureType<Object>) (TextureType<?>) textureType, data);
    }

    public static void init() {
        Chisel.LOGGER.info("[Chisel/CTM] ChiselCTMTypes.init() called");
        FusionTextureTypeRegistry.registerTextureType(ResourceLocation.fromNamespaceAndPath("chisel", "ctm"), CTM);
        FusionTextureTypeRegistry.registerTextureType(ResourceLocation.fromNamespaceAndPath("chisel", "ctmh"), CTMH);
        FusionTextureTypeRegistry.registerTextureType(ResourceLocation.fromNamespaceAndPath("chisel", "pillar"), PILLAR);
        FusionTextureTypeRegistry.registerTextureType(ResourceLocation.fromNamespaceAndPath("chisel", "sctm"), SCTM);
        FusionTextureTypeRegistry.registerTextureType(ResourceLocation.fromNamespaceAndPath("chisel", "pattern"), PATTERN);
        FusionTextureTypeRegistry.registerTextureType(ResourceLocation.fromNamespaceAndPath("chisel", "random"), RANDOM);
        FusionTextureTypeRegistry.registerTextureType(ResourceLocation.fromNamespaceAndPath("chisel", "ar"), AR);
        FusionTextureTypeRegistry.registerTextureType(ResourceLocation.fromNamespaceAndPath("chisel", "eldritch"), ELDRITCH);

        Chisel.LOGGER.info("[Chisel/CTM] Registering CTMMetadataSection into DEFAULT_METADATA_SECTIONS (current size: {})", SpriteLoader.DEFAULT_METADATA_SECTIONS.size());
        SpriteLoader.DEFAULT_METADATA_SECTIONS = ImmutableSet.<MetadataSectionSerializer<?>>builder()
            .addAll(SpriteLoader.DEFAULT_METADATA_SECTIONS)
            .add(CTMMetadataSection.INSTANCE)
            .build();
        Chisel.LOGGER.info("[Chisel/CTM] DEFAULT_METADATA_SECTIONS now has {} entries", SpriteLoader.DEFAULT_METADATA_SECTIONS.size());
    }
}
