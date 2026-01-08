package com.leclowndu93150.chisel;

import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.block.BlockCarvableGlass;
import com.leclowndu93150.chisel.block.BlockCarvablePane;
import com.leclowndu93150.chisel.carving.ChiselMode;
import com.leclowndu93150.chisel.client.gui.AutoChiselScreen;
import com.leclowndu93150.chisel.client.gui.ChiselScreen;
import com.leclowndu93150.chisel.client.gui.HitechChiselScreen;
import com.leclowndu93150.chisel.compat.ChiselRebornCompat;
import com.leclowndu93150.chisel.compat.ftbultimine.FTBUltimineCompat;
import com.leclowndu93150.chisel.client.particle.HolystoneStarParticle;
import com.leclowndu93150.chisel.init.ChiselBlockEntities;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselCreativeTabs;
import com.leclowndu93150.chisel.init.ChiselDataComponents;
import com.leclowndu93150.chisel.init.ChiselEntities;
import com.leclowndu93150.chisel.init.ChiselItems;
import com.leclowndu93150.chisel.init.ChiselMenus;
import com.leclowndu93150.chisel.init.ChiselParticles;
import com.leclowndu93150.chisel.init.ChiselRegistries;
import com.leclowndu93150.chisel.init.ChiselSounds;
import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModList;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.items.wrapper.CombinedInvWrapper;
import net.neoforged.neoforge.registries.DeferredBlock;
import org.slf4j.Logger;

@Mod(Chisel.MODID)
public class Chisel {
    public static final String MODID = "chisel";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Chisel(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);

        ChiselRebornCompat.registerAliases();

        ChiselRegistries.BLOCKS.register(modEventBus);
        ChiselRegistries.ITEMS.register(modEventBus);
        ChiselRegistries.CREATIVE_TABS.register(modEventBus);
        ChiselRegistries.BLOCK_ENTITY_TYPES.register(modEventBus);
        ChiselRegistries.MENU_TYPES.register(modEventBus);
        ChiselRegistries.SOUND_EVENTS.register(modEventBus);
        ChiselRegistries.DATA_COMPONENT_TYPES.register(modEventBus);
        ChiselRegistries.PARTICLE_TYPES.register(modEventBus);
        ChiselRegistries.ENTITY_TYPES.register(modEventBus);

        ChiselSounds.init();
        ChiselParticles.init();
        ChiselBlocks.init();
        ChiselItems.init();
        ChiselMenus.init();
        ChiselCreativeTabs.init();
        ChiselDataComponents.init();
        ChiselBlockEntities.init();
        ChiselEntities.init();

        modContainer.registerConfig(ModConfig.Type.COMMON, ChiselConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ChiselMode.registerAll();

        if (ModList.get().isLoaded("ftbultimine")) {
            FTBUltimineCompat.init();
        }
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.ItemHandler.BLOCK,
                ChiselBlockEntities.AUTO_CHISEL.get(),
                (blockEntity, side) -> {
                    if (side == Direction.DOWN) {
                        return blockEntity.getOutputInv();
                    } else if (side == Direction.UP) {
                        return blockEntity.getInputInv();
                    } else if (side == null) {
                        return new CombinedInvWrapper(
                                blockEntity.getInputInv(),
                                blockEntity.getOutputInv(),
                                blockEntity.getChiselSlot(),
                                blockEntity.getTargetSlot()
                        );
                    } else {
                        return new CombinedInvWrapper(blockEntity.getInputInv(), blockEntity.getOutputInv());
                    }
                }
        );

        event.registerBlockEntity(
                Capabilities.EnergyStorage.BLOCK,
                ChiselBlockEntities.AUTO_CHISEL.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage()
        );
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {

            event.enqueueWork(() -> {
                registerBlockRenderType(ChiselBlocks.GLASS, RenderType.cutout());

                for (ChiselBlockType<BlockCarvableGlass> stainedType : ChiselBlocks.GLASS_STAINED.values()) {
                    registerBlockRenderType(stainedType, RenderType.translucent());
                }

                for (ChiselBlockType<BlockCarvableGlass> dyedType : ChiselBlocks.GLASS_DYED.values()) {
                    registerBlockRenderType(dyedType, RenderType.translucent());
                }

                for (ChiselBlockType<BlockCarvablePane> paneType : ChiselBlocks.GLASSPANE_DYED.values()) {
                    registerBlockRenderType(paneType, RenderType.translucent());
                }

                registerBlockRenderType(ChiselBlocks.IRONPANE, RenderType.cutout());

                registerBlockRenderType(ChiselBlocks.WATERSTONE, RenderType.cutout());
                registerBlockRenderType(ChiselBlocks.LAVASTONE, RenderType.cutout());

                registerBlockRenderType(ChiselBlocks.ANTIBLOCK, RenderType.cutout());

                registerBlockRenderType(ChiselBlocks.CLOUD, RenderType.cutout());

                registerBlockRenderType(ChiselBlocks.ICE, RenderType.translucent());
                registerBlockRenderType(ChiselBlocks.ICE_PILLAR, RenderType.translucent());

                ItemBlockRenderTypes.setRenderLayer(ChiselBlocks.AUTO_CHISEL.get(), RenderType.cutout());
            });
        }

        private static void registerBlockRenderType(ChiselBlockType<?> blockType, RenderType renderType) {
            for (DeferredBlock<?> deferredBlock : blockType.getAllBlocks()) {
                Block block = deferredBlock.get();
                ItemBlockRenderTypes.setRenderLayer(block, renderType);
            }
        }

        @SubscribeEvent
        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(ChiselMenus.CHISEL_MENU.get(), ChiselScreen::new);
            event.register(ChiselMenus.HITECH_CHISEL_MENU.get(), HitechChiselScreen::new);
            event.register(ChiselMenus.AUTO_CHISEL_MENU.get(), AutoChiselScreen::new);
        }

        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ChiselParticles.HOLYSTONE_STAR.get(), HolystoneStarParticle.Provider::new);
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ChiselEntities.BALL_O_MOSS.get(),
                    context -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(context));
            event.registerEntityRenderer(ChiselEntities.CLOUD_IN_A_BOTTLE.get(),
                    context -> new net.minecraft.client.renderer.entity.ThrownItemRenderer<>(context));
        }
    }
}
