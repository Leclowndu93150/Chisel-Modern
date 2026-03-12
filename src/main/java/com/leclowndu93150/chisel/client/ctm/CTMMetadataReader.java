package com.leclowndu93150.chisel.client.ctm;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CTMMetadataReader {

    private static final Map<ResourceLocation, CTMTextureData> CACHE = new HashMap<>();

    public static void clearCache() {
        CACHE.clear();
    }

    public static CTMTextureData getMetadata(ResourceLocation spriteLocation) {
        if (CACHE.containsKey(spriteLocation)) {
            return CACHE.get(spriteLocation);
        }
        CTMTextureData data = loadMetadata(spriteLocation);
        CACHE.put(spriteLocation, data);
        return data;
    }

    private static CTMTextureData loadMetadata(ResourceLocation spriteLocation) {
        ResourceLocation mcmetaLoc = ResourceLocation.fromNamespaceAndPath(
            spriteLocation.getNamespace(),
            "textures/" + spriteLocation.getPath() + ".png.mcmeta"
        );

        ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
        Optional<Resource> resource = resourceManager.getResource(mcmetaLoc);
        if (resource.isEmpty()) return null;

        try (InputStream stream = resource.get().open();
             InputStreamReader reader = new InputStreamReader(stream)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            if (!root.has("ctm")) return null;
            JsonObject ctmJson = root.getAsJsonObject("ctm");
            return CTMTextureData.fromJson(ctmJson);
        } catch (Exception e) {
            return null;
        }
    }
}
