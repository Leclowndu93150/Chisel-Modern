package com.leclowndu93150.chisel.client.util;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class that detects and caches which Chisel blocks have CTM (Connected Textures Mod) support.
 * Detection is done once at resource reload to avoid performance impact on tooltips.
 */
public class CTMDetection {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Set<Item> CTM_ITEMS = new HashSet<>();
    private static boolean initialized = false;

    public static void init() {
        CTM_ITEMS.clear();

        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            for (DeferredBlock<?> deferredBlock : blockType.getAllBlocks()) {
                Block block = deferredBlock.get();
                ResourceLocation blockId = deferredBlock.getId();

                String variantName = getVariantName(blockId);
                if (hasCTMMetadata(blockType.getName(), variantName)) {
                    CTM_ITEMS.add(block.asItem());
                }
            }
        }

        initialized = true;
        LOGGER.info("CTM Detection initialized: found {} blocks with CTM", CTM_ITEMS.size());
    }

    public static boolean hasCTM(Item item) {
        if (!initialized) {
            return false;
        }
        return CTM_ITEMS.contains(item);
    }

    private static String getVariantName(ResourceLocation blockId) {
        String path = blockId.getPath();
        // Block IDs are in format: {blockTypeName}/{variantName}
        // e.g., "metals_aluminum/badgreggy" -> "badgreggy"
        // e.g., "andesite/dent" -> "dent"
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash > 0) {
            return path.substring(lastSlash + 1);
        }
        return path;
    }

    private static boolean hasCTMMetadata(String blockTypeName, String variantName) {
        ResourceLocation textureLocation = ResourceLocation.fromNamespaceAndPath(
                Chisel.MODID,
                "textures/block/" + blockTypeName + "/" + variantName + ".png.mcmeta"
        );

        try {
            var resourceManager = Minecraft.getInstance().getResourceManager();
            var optionalResource = resourceManager.getResource(textureLocation);

            if (optionalResource.isPresent()) {
                Resource resource = optionalResource.get();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                    StringBuilder content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }

                    return content.toString().contains("\"ctm\"") || content.toString().contains("\"CTMH\"");
                }
            }
        } catch (Exception ignored) {

        }

        return false;
    }

    public static void invalidate() {
        CTM_ITEMS.clear();
        initialized = false;
    }
}
