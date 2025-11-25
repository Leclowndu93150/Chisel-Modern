package com.leclowndu93150.chisel.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A lenient version of ExistingFileHelper that logs missing resources
 * instead of causing datagen to crash.
 */
public class LenientExistingFileHelper extends ExistingFileHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LenientExistingFileHelper.class);
    private final Set<String> missingResources = new HashSet<>();

    public LenientExistingFileHelper(Collection<Path> existingPacks, Set<String> existingMods, boolean enable,
                                     @Nullable String assetIndex, @Nullable File assetsDir) {
        super(existingPacks, existingMods, enable, assetIndex, assetsDir);
    }

    @Override
    public boolean exists(ResourceLocation loc, PackType packType) {
        boolean exists = super.exists(loc, packType);
        if (!exists) {
            String key = packType.name() + ":" + loc.toString();
            if (missingResources.add(key)) {
                LOGGER.warn("Missing {} resource: {}", packType.name(), loc);
            }
            return true; // Pretend it exists to continue datagen
        }
        return true;
    }

    @Override
    public boolean exists(ResourceLocation loc, IResourceType type) {
        boolean exists = super.exists(loc, type);
        if (!exists) {
            ResourceLocation fullLoc = ResourceLocation.fromNamespaceAndPath(
                    loc.getNamespace(),
                    type.getPrefix() + "/" + loc.getPath() + type.getSuffix()
            );
            String key = type.getPackType().name() + ":" + fullLoc.toString();
            if (missingResources.add(key)) {
                LOGGER.warn("Missing {} resource: {} (type: {})", type.getPackType().name(), fullLoc, type.getClass().getSimpleName());
            }
            return true; // Pretend it exists to continue datagen
        }
        return true;
    }

    @Override
    public boolean exists(ResourceLocation loc, PackType packType, String pathSuffix, String pathPrefix) {
        boolean exists = super.exists(loc, packType, pathSuffix, pathPrefix);
        if (!exists) {
            ResourceLocation fullLoc = ResourceLocation.fromNamespaceAndPath(
                    loc.getNamespace(),
                    pathPrefix + "/" + loc.getPath() + pathSuffix
            );
            String key = packType.name() + ":" + fullLoc.toString();
            if (missingResources.add(key)) {
                LOGGER.warn("Missing {} resource: {}", packType.name(), fullLoc);
            }
            return true; // Pretend it exists to continue datagen
        }
        return true;
    }

    /**
     * Get all missing resources that were encountered during datagen.
     */
    public Set<String> getMissingResources() {
        return new HashSet<>(missingResources);
    }

    /**
     * Print a summary of all missing resources.
     */
    public void printMissingSummary() {
        if (!missingResources.isEmpty()) {
            LOGGER.warn("========================================");
            LOGGER.warn("MISSING RESOURCES SUMMARY ({} total):", missingResources.size());
            LOGGER.warn("========================================");
            missingResources.stream()
                    .sorted()
                    .forEach(r -> LOGGER.warn("  - {}", r));
            LOGGER.warn("========================================");
        }
    }
}
