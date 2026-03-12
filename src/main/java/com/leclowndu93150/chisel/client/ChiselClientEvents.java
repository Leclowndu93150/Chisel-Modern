package com.leclowndu93150.chisel.client;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.block.BlockCarvable;
import com.leclowndu93150.chisel.client.ctm.*;
import com.leclowndu93150.chisel.client.util.CTMDetection;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@EventBusSubscriber(modid = Chisel.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChiselClientEvents {

    private static final int DEFAULT_WATER_COLOR = 0x3F76E4;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(CTMDetection::init);
    }

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        CTMMetadataReader.clearCache();
        int wrapped = 0;
        int chiselTotal = 0;

        for (Map.Entry<ModelResourceLocation, BakedModel> entry : event.getModels().entrySet()) {
            if (!entry.getKey().id().getNamespace().equals(Chisel.MODID)) continue;
            chiselTotal++;
            BakedModel model = entry.getValue();
            if (model instanceof ChiselBakedModelWrapper) continue;

            Map<ResourceLocation, ChiselQuadProcessor> processors = buildProcessors(model);
            if (!processors.isEmpty()) {
                entry.setValue(new ChiselBakedModelWrapper(model, processors));
                wrapped++;
            }
        }
        Chisel.LOGGER.info("[Chisel/CTM] Wrapped {} / {} chisel models with ChiselBakedModelWrapper", wrapped, chiselTotal);
    }

    private static Map<ResourceLocation, ChiselQuadProcessor> buildProcessors(BakedModel model) {
        Set<ResourceLocation> checked = new HashSet<>();
        Map<ResourceLocation, ChiselQuadProcessor> processors = new HashMap<>();

        RandomSource random = RandomSource.create(42);
        for (Direction dir : Direction.values()) {
            collectFromQuads(model.getQuads(null, dir, random, ModelData.EMPTY, null), checked, processors);
        }
        collectFromQuads(model.getQuads(null, null, random, ModelData.EMPTY, null), checked, processors);

        return processors;
    }

    private static void collectFromQuads(List<BakedQuad> quads, Set<ResourceLocation> checked,
                                          Map<ResourceLocation, ChiselQuadProcessor> processors) {
        for (BakedQuad quad : quads) {
            ResourceLocation spriteName = quad.getSprite().contents().name();
            if (!checked.add(spriteName)) continue;

            CTMTextureData data = CTMMetadataReader.getMetadata(spriteName);
            if (data == null) continue;

            ChiselQuadProcessor processor = createProcessor(data);
            if (processor != null) {
                processors.put(spriteName, processor);
            }
        }
    }

    private static ChiselQuadProcessor createProcessor(CTMTextureData data) {
        return switch (data.getType()) {
            case CTM -> {
                TextureAtlasSprite ctmSheet = resolveCtmSheet(data);
                yield new CTMFullProcessor(ctmSheet);
            }
            case CTMH -> new CTMHProcessor();
            case CTMV, PILLAR -> new PillarProcessor();
            case SCTM -> new SCTMProcessor();
            case PATTERN -> new PatternProcessor(data.getSize());
            case RANDOM -> new RandomProcessor(data.getSize());
            case AR -> new AlterRProcessor();
            case ELDRITCH -> new EldritchProcessor();
            case NORMAL -> null;
        };
    }

    private static TextureAtlasSprite resolveCtmSheet(CTMTextureData data) {
        String[] textures = data.getTextures();
        if (textures.length == 0) return null;
        try {
            ResourceLocation ctmLoc = ResourceLocation.parse(textures[0]);
            TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
                .apply(ctmLoc);
            if (sprite.contents().name().equals(ResourceLocation.withDefaultNamespace("missingno"))) {
                return null;
            }
            return sprite;
        } catch (Exception e) {
            return null;
        }
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        for (Map.Entry<DyeColor, ChiselBlockType<BlockCarvable>> entry : ChiselBlocks.HEX_PLATING.entrySet()) {
            DyeColor color = entry.getKey();
            ChiselBlockType<?> blockType = entry.getValue();

            Block[] blocks = blockType.getAllBlocks().stream()
                    .map(DeferredBlock::get)
                    .toArray(Block[]::new);

            event.register((state, level, pos, tintIndex) ->
                    tintIndex == 1 ? color.getTextColor() : -1, blocks);
        }

        Block[] waterstoneBlocks = ChiselBlocks.WATERSTONE.getAllBlocks().stream()
                .map(DeferredBlock::get)
                .toArray(Block[]::new);

        event.register((state, level, pos, tintIndex) -> {
            if (tintIndex == 1) {
                return (level != null && pos != null) ? BiomeColors.getAverageWaterColor(level, pos) : DEFAULT_WATER_COLOR;
            }
            return -1;
        }, waterstoneBlocks);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for (Map.Entry<DyeColor, ChiselBlockType<BlockCarvable>> entry : ChiselBlocks.HEX_PLATING.entrySet()) {
            DyeColor color = entry.getKey();
            ChiselBlockType<?> blockType = entry.getValue();

            var items = blockType.getAllBlocks().stream()
                    .map(block -> block.get().asItem())
                    .toArray(Item[]::new);

            event.register((stack, tintIndex) ->
                    tintIndex == 1 ? color.getTextColor() : -1, items);
        }

        var waterstoneItems = ChiselBlocks.WATERSTONE.getAllBlocks().stream()
                .map(block -> block.get().asItem())
                .toArray(Item[]::new);

        event.register((stack, tintIndex) ->
                tintIndex == 1 ? DEFAULT_WATER_COLOR : -1, waterstoneItems);
    }
}
