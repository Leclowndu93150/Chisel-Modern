package com.leclowndu93150.chisel.client.ctm;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.Locale;

public class CTMTextureData {

    public enum CTMType {
        CTM, CTMH, CTMV, PILLAR, SCTM, PATTERN, RANDOM, AR, ELDRITCH, NORMAL
    }

    public enum RenderLayer {
        SOLID, CUTOUT, TRANSLUCENT
    }

    private final CTMType type;
    private final String[] textures;
    private final String proxy;
    private final RenderLayer layer;
    private final int size;
    private final boolean connectInside;
    private final boolean ignoreStates;

    public CTMTextureData(CTMType type, String[] textures, String proxy, RenderLayer layer, int size, boolean connectInside, boolean ignoreStates) {
        this.type = type;
        this.textures = textures;
        this.proxy = proxy;
        this.layer = layer;
        this.size = size;
        this.connectInside = connectInside;
        this.ignoreStates = ignoreStates;
    }

    public CTMType getType() {
        return type;
    }

    public String[] getTextures() {
        return textures;
    }

    public String getProxy() {
        return proxy;
    }

    public RenderLayer getLayer() {
        return layer;
    }

    public int getSize() {
        return size;
    }

    public boolean isConnectInside() {
        return connectInside;
    }

    public boolean isIgnoreStates() {
        return ignoreStates;
    }

    public static CTMTextureData fromJson(JsonObject json) {
        CTMType type = CTMType.NORMAL;
        if (json.has("type")) {
            type = parseType(json.get("type").getAsString());
        }

        String[] textures = new String[0];
        if (json.has("textures")) {
            JsonArray arr = json.getAsJsonArray("textures");
            textures = new String[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                textures[i] = arr.get(i).getAsString();
            }
        }

        String proxy = null;
        if (json.has("proxy")) {
            proxy = json.get("proxy").getAsString();
        }

        RenderLayer layer = null;
        if (json.has("layer")) {
            layer = parseLayer(json.get("layer").getAsString());
        }

        int size = 2;
        boolean connectInside = false;
        boolean ignoreStates = false;

        if (json.has("extra")) {
            JsonObject extra = json.getAsJsonObject("extra");
            if (extra.has("size")) {
                size = extra.get("size").getAsInt();
            }
            if (extra.has("connect_inside")) {
                connectInside = extra.get("connect_inside").getAsBoolean();
            }
            if (extra.has("ignore_states")) {
                ignoreStates = extra.get("ignore_states").getAsBoolean();
            }
        }

        return new CTMTextureData(type, textures, proxy, layer, size, connectInside, ignoreStates);
    }

    private static CTMType parseType(String raw) {
        String lower = raw.toLowerCase(Locale.ROOT);
        switch (lower) {
            case "ctm":
                return CTMType.CTM;
            case "ctmh":
            case "ctm_horizontal":
                return CTMType.CTMH;
            case "ctmv":
            case "pillar":
                return CTMType.PILLAR;
            case "ctm_simple":
            case "sctm":
                return CTMType.SCTM;
            case "v":
            case "pattern":
                return CTMType.PATTERN;
            case "r":
            case "random":
                return CTMType.RANDOM;
            case "ar":
                return CTMType.AR;
            case "eldritch":
                return CTMType.ELDRITCH;
            case "normal":
                return CTMType.NORMAL;
            default:
                return CTMType.NORMAL;
        }
    }

    private static RenderLayer parseLayer(String raw) {
        String lower = raw.toLowerCase(Locale.ROOT);
        switch (lower) {
            case "solid":
                return RenderLayer.SOLID;
            case "cutout":
                return RenderLayer.CUTOUT;
            case "translucent":
                return RenderLayer.TRANSLUCENT;
            default:
                return RenderLayer.SOLID;
        }
    }
}
