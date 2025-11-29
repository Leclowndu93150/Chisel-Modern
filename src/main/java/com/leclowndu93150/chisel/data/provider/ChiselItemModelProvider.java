package com.leclowndu93150.chisel.data.provider;

import com.google.gson.JsonObject;
import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.api.block.ICarvable;
import com.leclowndu93150.chisel.block.BlockCarvablePane;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselItems;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Data provider for generating item model JSON files.
 * Generates simple JSON directly without validation to avoid issues with missing parent models.
 */
public class ChiselItemModelProvider implements DataProvider {

    private final PackOutput output;

    public ChiselItemModelProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            for (var entry : blockType.getBlocks().entrySet()) {
                String variantName = entry.getKey();
                RegistryObject<?> registryObject = entry.getValue();
                RegistryObject<BlockItem> item = (RegistryObject<BlockItem>) blockType.getItem(variantName);

                if (item != null) {
                    String registryPath = item.getId().getPath();
                    Block block = (Block) registryObject.get();
                    JsonObject json;

                    String texturePath = blockType.getName() + "/" + variantName;
                    if (block instanceof ICarvable carvable) {
                        texturePath = carvable.getBlockType() + "/" + carvable.getVariation().getTextureName();
                    }

                    if (block instanceof BlockCarvablePane paneBlock) {
                        if (blockType == ChiselBlocks.IRONPANE) {
                            String mainTexture = "chisel:block/" + texturePath;
                            json = createGeneratedItemModel(mainTexture);
                        } else {
                            String sideTexture = "chisel:block/" + texturePath + "-side";
                            json = createGeneratedItemModel(sideTexture);
                        }
                    } else {
                        json = createBlockItemModel("chisel:block/" + registryPath);
                    }

                    Path path = output.getOutputFolder().resolve("assets/chisel/models/item/" + registryPath + ".json");
                    futures.add(DataProvider.saveStable(cache, json, path));
                }
            }
        }

        futures.add(saveToolItemModel(cache, "iron_chisel"));
        futures.add(saveToolItemModel(cache, "diamond_chisel"));
        futures.add(saveToolItemModel(cache, "hitech_chisel"));
        futures.add(saveToolItemModel(cache, "offset_tool"));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<?> saveToolItemModel(CachedOutput cache, String name) {
        JsonObject json = createGeneratedItemModel("chisel:item/" + name);
        Path path = output.getOutputFolder().resolve("assets/chisel/models/item/" + name + ".json");
        return DataProvider.saveStable(cache, json, path);
    }

    /**
     * Creates a simple item model that inherits from a block model.
     */
    private JsonObject createBlockItemModel(String parentModel) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", parentModel);
        return json;
    }

    /**
     * Creates a flat item model using item/generated with a texture layer.
     */
    private JsonObject createGeneratedItemModel(String texture) {
        JsonObject json = new JsonObject();
        json.addProperty("parent", "minecraft:item/generated");
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", texture);
        json.add("textures", textures);
        return json;
    }

    @Override
    public String getName() {
        return "Chisel Item Models";
    }
}
