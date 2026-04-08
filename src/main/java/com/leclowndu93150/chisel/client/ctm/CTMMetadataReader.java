package com.leclowndu93150.chisel.client.ctm;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CTMMetadataReader {

    private static final Map<ResourceLocation, CTMTextureData> CACHE = new HashMap<>();
    private static final Map<ResourceLocation, ResolvedMetadata> RESOLVED_CACHE = new HashMap<>();

    public static final class ResolvedMetadata {
        private final ResourceLocation spriteLocation;
        private final CTMTextureData data;

        public ResolvedMetadata(ResourceLocation spriteLocation, CTMTextureData data) {
            this.spriteLocation = spriteLocation;
            this.data = data;
        }

        public ResourceLocation getSpriteLocation() {
            return spriteLocation;
        }

        public CTMTextureData getData() {
            return data;
        }
    }

    public static void clearCache() {
        CACHE.clear();
        RESOLVED_CACHE.clear();
    }

    public static CTMTextureData getMetadata(ResourceLocation spriteLocation) {
        if (CACHE.containsKey(spriteLocation)) {
            return CACHE.get(spriteLocation);
        }
        CTMTextureData data = loadMetadata(spriteLocation);
        CACHE.put(spriteLocation, data);
        return data;
    }

    public static ResolvedMetadata getResolvedMetadata(ResourceLocation spriteLocation) {
        if (RESOLVED_CACHE.containsKey(spriteLocation)) {
            return RESOLVED_CACHE.get(spriteLocation);
        }
        ResolvedMetadata resolved = resolveMetadata(spriteLocation, new HashSet<>());
        RESOLVED_CACHE.put(spriteLocation, resolved);
        return resolved;
    }

    private static ResolvedMetadata resolveMetadata(ResourceLocation spriteLocation, Set<ResourceLocation> seen) {
        if (!seen.add(spriteLocation)) {
            return null;
        }

        CTMTextureData data = getMetadata(spriteLocation);
        if (data == null) {
            return null;
        }

        String proxy = data.getProxy();
        if (proxy != null && !proxy.isBlank()) {
            try {
                ResourceLocation proxyLocation = ResourceLocation.parse(proxy);
                ResolvedMetadata resolvedProxy = resolveMetadata(proxyLocation, seen);
                if (resolvedProxy != null) {
                    return resolvedProxy;
                }
            } catch (Exception ignored) {
            }
        }

        return new ResolvedMetadata(spriteLocation, data);
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
