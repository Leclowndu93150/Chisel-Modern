package com.leclowndu93150.chisel.api.block;

import com.leclowndu93150.chisel.data.ChiselModelTemplates;

import javax.annotation.Nullable;

/**
 * Data class representing a single block variation within a chisel group.
 */
public record VariationData(
        String name,
        String localizedName,
        @Nullable ChiselModelTemplates.ModelTemplate modelTemplate,
        @Nullable String[] tooltip,
        @Nullable String textureOverride
) {
    public VariationData(String name) {
        this(name, toTitleCase(name), null, null, null);
    }

    public VariationData(String name, String localizedName) {
        this(name, localizedName, null, null, null);
    }

    public VariationData(String name, ChiselModelTemplates.ModelTemplate modelTemplate) {
        this(name, toTitleCase(name), modelTemplate, null, null);
    }

    public VariationData(String name, String localizedName, ChiselModelTemplates.ModelTemplate modelTemplate) {
        this(name, localizedName, modelTemplate, null, null);
    }

    public VariationData(String name, String localizedName, ChiselModelTemplates.ModelTemplate modelTemplate, @Nullable String[] tooltip) {
        this(name, localizedName, modelTemplate, tooltip, null);
    }

    public VariationData withTooltip(String... tooltip) {
        return new VariationData(name, localizedName, modelTemplate, tooltip, textureOverride);
    }

    public VariationData withModelTemplate(ChiselModelTemplates.ModelTemplate template) {
        return new VariationData(name, localizedName, template, tooltip, textureOverride);
    }

    public VariationData withName(String newLocalizedName) {
        return new VariationData(name, newLocalizedName, modelTemplate, tooltip, textureOverride);
    }

    /**
     * Use a different texture file than the variant name.
     * e.g., "circularct" variant can use "circular" texture.
     */
    public VariationData withTexture(String textureName) {
        return new VariationData(name, localizedName, modelTemplate, tooltip, textureName);
    }

    /**
     * Gets the texture name to use (either override or the variant name).
     */
    public String getTextureName() {
        return textureOverride != null ? textureOverride : name;
    }

    private static String toTitleCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (c == '_' || c == ' ' || c == '/') {
                result.append(' ');
                capitalizeNext = true;
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
