package com.leclowndu93150.chisel.client.ctm;

import com.google.gson.JsonObject;
import com.supermartijn642.fusion.api.texture.TextureType;
import com.supermartijn642.fusion.api.util.Pair;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public class CTMMetadataSection implements MetadataSectionSerializer<Pair<TextureType<Object>, Object>> {

    public static final CTMMetadataSection INSTANCE = new CTMMetadataSection();

    @Override
    public String getMetadataSectionName() {
        return "ctm";
    }

    @Override
    public Pair<TextureType<Object>, Object> fromJson(JsonObject json) {
        CTMTextureData data = CTMTextureData.fromJson(json);
        return ChiselCTMTypes.resolve(data);
    }
}
