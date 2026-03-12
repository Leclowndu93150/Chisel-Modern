package com.leclowndu93150.chisel.client.ctm;

import com.google.gson.JsonObject;
import com.leclowndu93150.chisel.Chisel;
import com.supermartijn642.fusion.api.texture.TextureType;
import com.supermartijn642.fusion.api.util.Pair;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;

public class CTMMetadataSection implements MetadataSectionSerializer<Pair<TextureType<Object>, Object>> {

    public static final CTMMetadataSection INSTANCE = new CTMMetadataSection();
    private static boolean loggedFirst = false;

    @Override
    public String getMetadataSectionName() {
        return "ctm";
    }

    @Override
    public Pair<TextureType<Object>, Object> fromJson(JsonObject json) {
        CTMTextureData data = CTMTextureData.fromJson(json);
        if (!loggedFirst) {
            Chisel.LOGGER.info("[Chisel/CTM] CTMMetadataSection.fromJson() called for the first time! type={}", data.getType());
            loggedFirst = true;
        }
        Chisel.LOGGER.debug("[Chisel/CTM] Parsing ctm metadata: type={}", data.getType());
        return ChiselCTMTypes.resolve(data);
    }
}
