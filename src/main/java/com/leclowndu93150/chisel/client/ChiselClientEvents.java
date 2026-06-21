package com.leclowndu93150.chisel.client;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.block.BlockCarvable;
import com.leclowndu93150.chisel.client.util.CTMDetection;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.color.block.BlockTintSources;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import team.chisel.ctm.client.texture.ctx.OffsetProviderRegistry;

import java.util.List;
import java.util.Map;

@EventBusSubscriber(modid = Chisel.MODID, value = Dist.CLIENT)
public class ChiselClientEvents {

    private static final int DEFAULT_WATER_COLOR = 0x3F76E4;
    private static final BlockTintSource BLANK = BlockTintSources.constant(-1);

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            OffsetProviderRegistry.INSTANCE.registerProvider((world, pos) ->
                    ChunkDataClient.getOffsetData(world, pos).getOffset(pos));

            CTMDetection.init();
        });
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.BlockTintSources event) {
        for (Map.Entry<DyeColor, ChiselBlockType<BlockCarvable>> entry : ChiselBlocks.HEX_PLATING.entrySet()) {
            DyeColor color = entry.getKey();
            ChiselBlockType<?> blockType = entry.getValue();

            Block[] blocks = blockType.getAllBlocks().stream()
                    .map(DeferredBlock::get)
                    .toArray(Block[]::new);

            int tintColor = color.getTextColor();
            BlockTintSource particleOnlyTint = new BlockTintSource() {
                @Override
                public int color(BlockState state) {
                    return -1;
                }

                @Override
                public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                    return -1;
                }

                @Override
                public int colorAsTerrainParticle(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                    return tintColor;
                }
            };
            event.register(List.of(particleOnlyTint, BlockTintSources.constant(tintColor)), blocks);
        }

        Block[] waterstoneBlocks = ChiselBlocks.WATERSTONE.getAllBlocks().stream()
                .map(DeferredBlock::get)
                .toArray(Block[]::new);

        BlockTintSource waterTint = new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return DEFAULT_WATER_COLOR;
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter level, BlockPos pos) {
                return BiomeColors.getAverageWaterColor(level, pos);
            }
        };

        event.register(List.of(BLANK, waterTint), waterstoneBlocks);
    }
}
