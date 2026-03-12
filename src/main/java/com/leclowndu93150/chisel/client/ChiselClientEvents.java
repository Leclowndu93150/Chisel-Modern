package com.leclowndu93150.chisel.client;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.block.BlockCarvable;
import com.leclowndu93150.chisel.client.ctm.ChiselBakedModelWrapper;
import com.leclowndu93150.chisel.client.ctm.ChiselQuadProcessor;
import com.leclowndu93150.chisel.client.util.CTMDetection;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.supermartijn642.fusion.extensions.TextureAtlasSpriteExtension;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.client.resources.model.ModelResourceLocation;
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

import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = Chisel.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ChiselClientEvents {

    private static final int DEFAULT_WATER_COLOR = 0x3F76E4;

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(CTMDetection::init);
    }

    @SubscribeEvent
    public static void onModifyBakingResult(ModelEvent.ModifyBakingResult event) {
        int wrapped = 0;
        for (Map.Entry<ModelResourceLocation, BakedModel> entry : event.getModels().entrySet()) {
            if (!entry.getKey().id().getNamespace().equals(Chisel.MODID)) continue;
            BakedModel model = entry.getValue();
            if (model instanceof ChiselBakedModelWrapper) continue;
            if (hasChiselQuads(model)) {
                entry.setValue(new ChiselBakedModelWrapper(model));
                wrapped++;
            }
        }
        Chisel.LOGGER.info("[Chisel/CTM] Wrapped {} models with ChiselBakedModelWrapper", wrapped);
    }

    private static boolean hasChiselQuads(BakedModel model) {
        try {
            for (Direction dir : Direction.values()) {
                List<BakedQuad> quads = model.getQuads(null, dir, null, ModelData.EMPTY, null);
                for (BakedQuad quad : quads) {
                    var type = ((TextureAtlasSpriteExtension) quad.getSprite()).getFusionTextureType();
                    if (type instanceof ChiselQuadProcessor) return true;
                }
            }
            List<BakedQuad> quads = model.getQuads(null, null, null, ModelData.EMPTY, null);
            for (BakedQuad quad : quads) {
                var type = ((TextureAtlasSpriteExtension) quad.getSprite()).getFusionTextureType();
                if (type instanceof ChiselQuadProcessor) return true;
            }
        } catch (Exception ignored) {
        }
        return false;
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
