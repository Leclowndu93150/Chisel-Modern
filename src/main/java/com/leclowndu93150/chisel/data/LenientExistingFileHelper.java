package com.leclowndu93150.chisel.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class that tracks missing resource names during datagen.
 * ExistingFileHelper was removed in NeoForge 26.1, so this is now a standalone
 * tracker rather than a subclass.
 */
public class LenientExistingFileHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(LenientExistingFileHelper.class);
    private final Set<String> missingResources = new HashSet<>();

    public LenientExistingFileHelper() {
    }

    /**
     * Record a missing resource by key.
     */
    public void trackMissing(String key) {
        if (missingResources.add(key)) {
            LOGGER.warn("Missing resource: {}", key);
        }
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
