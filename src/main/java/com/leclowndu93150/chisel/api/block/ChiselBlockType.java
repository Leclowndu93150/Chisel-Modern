package com.leclowndu93150.chisel.api.block;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.block.BlockCarvable;
import com.leclowndu93150.chisel.data.ChiselModelTemplates;
import com.leclowndu93150.chisel.init.ChiselRegistries;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Fluent builder for registering chisel block types with their variants.
 * Replaces the Registrate-based ChiselBlockFactory from 1.18.
 */
public class ChiselBlockType<T extends Block & ICarvable> {
    private final String name;
    private final List<VariationData> variations = new ArrayList<>();
    private final List<ResourceLocation> vanillaBlocks = new ArrayList<>();
    private final List<TagKey<Block>> blockTags = new ArrayList<>();
    private final List<TagKey<Item>> itemTags = new ArrayList<>();

    @Nullable
    private Supplier<Block> propertiesFrom;
    @Nullable
    private BlockBehaviour.Properties customProperties;
    @Nullable
    private String groupName;
    @Nullable
    private TagKey<Block> carvingGroupTag;
    @Nullable
    private MapColor mapColor;
    @Nullable
    private ChiselModelTemplates.ModelTemplate defaultModelTemplate;

    private BiFunction<BlockBehaviour.Properties, VariationData, T> blockFactory;

    private final Map<String, DeferredBlock<T>> registeredBlocks = new LinkedHashMap<>();
    private final Map<String, DeferredItem<BlockItem>> registeredItems = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    public ChiselBlockType(String name) {
        this.name = name;
        this.blockFactory = (props, data) -> (T) new BlockCarvable(props, data, name);
    }

    public ChiselBlockType(String name, BiFunction<BlockBehaviour.Properties, VariationData, T> factory) {
        this.name = name;
        this.blockFactory = factory;
    }

    /**
     * Add a vanilla block to be included in this carving group.
     */
    public ChiselBlockType<T> addVanillaBlock(Block block) {
        ResourceLocation key = BuiltInRegistries.BLOCK.getKey(block);
        if (key != null) {
            vanillaBlocks.add(key);
        }
        return this;
    }

    /**
     * Add a vanilla block by resource location.
     */
    public ChiselBlockType<T> addVanillaBlock(ResourceLocation blockId) {
        vanillaBlocks.add(blockId);
        return this;
    }

    /**
     * Copy properties from an existing block.
     */
    public ChiselBlockType<T> properties(Supplier<Block> copyFrom) {
        this.propertiesFrom = copyFrom;
        return this;
    }

    /**
     * Set custom properties directly.
     */
    public ChiselBlockType<T> properties(BlockBehaviour.Properties properties) {
        this.customProperties = properties;
        return this;
    }

    /**
     * Add a block tag to all variants.
     */
    public ChiselBlockType<T> tag(TagKey<Block> tag) {
        this.blockTags.add(tag);
        return this;
    }

    /**
     * Add an item tag to all variant items.
     */
    public ChiselBlockType<T> itemTag(TagKey<Item> tag) {
        this.itemTags.add(tag);
        return this;
    }

    /**
     * Set a custom carving group tag (defaults to chisel:carving/{name}).
     */
    public ChiselBlockType<T> carvingGroup(TagKey<Block> tag) {
        this.carvingGroupTag = tag;
        return this;
    }

    /**
     * Set the display name for the group (used in lang generation).
     */
    public ChiselBlockType<T> groupName(String name) {
        this.groupName = name;
        return this;
    }

    /**
     * Set the map color for all variants.
     */
    public ChiselBlockType<T> mapColor(MapColor color) {
        this.mapColor = color;
        return this;
    }

    /**
     * Add a single variation.
     */
    public ChiselBlockType<T> variation(VariationData variation) {
        this.variations.add(variation);
        return this;
    }

    /**
     * Add a single variation by name.
     */
    public ChiselBlockType<T> variation(String name) {
        this.variations.add(new VariationData(name));
        return this;
    }

    /**
     * Add a single variation with custom localized name.
     */
    public ChiselBlockType<T> variation(String name, String localizedName) {
        this.variations.add(new VariationData(name, localizedName));
        return this;
    }

    /**
     * Add a single variation with custom localized name and model template.
     */
    public ChiselBlockType<T> variation(String name, String localizedName, ChiselModelTemplates.ModelTemplate modelTemplate) {
        this.variations.add(new VariationData(name, localizedName, modelTemplate, null));
        return this;
    }

    /**
     * Add multiple variations from a list.
     */
    public ChiselBlockType<T> variations(List<VariationData> variations) {
        this.variations.addAll(variations);
        return this;
    }

    /**
     * Set a default model template for all variations that don't specify one.
     */
    public ChiselBlockType<T> defaultModelTemplate(ChiselModelTemplates.ModelTemplate template) {
        this.defaultModelTemplate = template;
        return this;
    }

    /**
     * Get the default model template for this block type.
     */
    @Nullable
    public ChiselModelTemplates.ModelTemplate getDefaultModelTemplate() {
        return defaultModelTemplate;
    }

    /**
     * Build and register all blocks.
     */
    public ChiselBlockType<T> build() {
        for (VariationData variation : variations) {
            String registryName = name.replace("/", "_") + "/" + variation.name();

            BlockBehaviour.Properties props;
            if (customProperties != null) {
                props = customProperties;
            } else if (propertiesFrom != null) {
                props = BlockBehaviour.Properties.ofFullCopy(propertiesFrom.get());
            } else {
                props = BlockBehaviour.Properties.of();
            }

            if (mapColor != null) {
                props = props.mapColor(mapColor);
            }

            final VariationData finalVariation = variation;
            final BlockBehaviour.Properties finalProps = props;

            DeferredBlock<T> block = ChiselRegistries.BLOCKS.register(registryName,
                    () -> blockFactory.apply(finalProps, finalVariation));
            registeredBlocks.put(variation.name(), block);

            DeferredItem<BlockItem> item = ChiselRegistries.ITEMS.registerSimpleBlockItem(registryName, block);
            registeredItems.put(variation.name(), item);
        }

        return this;
    }

    public String getName() {
        return name;
    }

    public String getGroupName() {
        return groupName != null ? groupName : toTitleCase(name) + " Block";
    }

    public List<VariationData> getVariations() {
        return Collections.unmodifiableList(variations);
    }

    public List<ResourceLocation> getVanillaBlocks() {
        return Collections.unmodifiableList(vanillaBlocks);
    }

    public List<TagKey<Block>> getBlockTags() {
        return Collections.unmodifiableList(blockTags);
    }

    public List<TagKey<Item>> getItemTags() {
        return Collections.unmodifiableList(itemTags);
    }

    public TagKey<Block> getCarvingGroupTag() {
        if (carvingGroupTag != null) {
            return carvingGroupTag;
        }
        return net.minecraft.tags.BlockTags.create(Chisel.id("carving/" + name.replace("/", "_")));
    }

    public Map<String, DeferredBlock<T>> getBlocks() {
        return Collections.unmodifiableMap(registeredBlocks);
    }

    public Map<String, DeferredItem<BlockItem>> getItems() {
        return Collections.unmodifiableMap(registeredItems);
    }

    @Nullable
    public DeferredBlock<T> getBlock(String variationName) {
        return registeredBlocks.get(variationName);
    }

    @Nullable
    public DeferredItem<BlockItem> getItem(String variationName) {
        return registeredItems.get(variationName);
    }

    public List<DeferredBlock<T>> getAllBlocks() {
        return new ArrayList<>(registeredBlocks.values());
    }

    public List<DeferredItem<BlockItem>> getAllItems() {
        return new ArrayList<>(registeredItems.values());
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
