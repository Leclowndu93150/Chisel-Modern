package com.leclowndu93150.chisel;

import com.leclowndu93150.chisel.carving.ChiselMode;
import com.leclowndu93150.chisel.command.ChiselDebugCommands;
import com.leclowndu93150.chisel.client.gui.AutoChiselScreen;
import com.leclowndu93150.chisel.client.gui.ChiselScreen;
import com.leclowndu93150.chisel.client.gui.HitechChiselScreen;
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
import com.leclowndu93150.chisel.worldgen.ChiselBiomeModifiers;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.neoforged.fml.ModList;
// import net.minecraft.client.renderer.ItemBlockRenderTypes; // Removed in 26.1
import net.minecraft.resources.Identifier;
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
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import com.leclowndu93150.chisel.client.render.BlockPreviewRenderState;
import com.leclowndu93150.chisel.client.render.BlockPreviewRenderer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
// CombinedInvWrapper replaced by CombinedResourceHandler in transfer API
import org.slf4j.Logger;

@Mod(Chisel.MODID)
public class Chisel {
    public static final String MODID = "chisel";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Chisel(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::registerCapabilities);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);

        ChiselRegistries.BLOCKS.register(modEventBus);
        ChiselRegistries.ITEMS.register(modEventBus);
        ChiselRegistries.CREATIVE_TABS.register(modEventBus);
        ChiselRegistries.BLOCK_ENTITY_TYPES.register(modEventBus);
        ChiselRegistries.MENU_TYPES.register(modEventBus);
        ChiselRegistries.SOUND_EVENTS.register(modEventBus);
        ChiselRegistries.DATA_COMPONENT_TYPES.register(modEventBus);
        ChiselRegistries.PARTICLE_TYPES.register(modEventBus);
        ChiselRegistries.ENTITY_TYPES.register(modEventBus);
        ChiselRegistries.ATTACHMENT_TYPES.register(modEventBus);

        ChiselBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);

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

    private void registerCommands(RegisterCommandsEvent event) {
        ChiselDebugCommands.register(event.getDispatcher());
    }

    private void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlockEntity(
                Capabilities.Item.BLOCK,
                ChiselBlockEntities.AUTO_CHISEL.get(),
                (blockEntity, side) -> blockEntity.getCombinedItemHandler(side)
        );

        event.registerBlockEntity(
                Capabilities.Energy.BLOCK,
                ChiselBlockEntities.AUTO_CHISEL.get(),
                (blockEntity, side) -> blockEntity.getEnergyStorage()
        );
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MODID, path);
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Render types are now set via model JSON render_type field in 26.1
            // ItemBlockRenderTypes was removed - no programmatic registration needed
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
        public static void registerPipRenderers(RegisterPictureInPictureRenderersEvent event) {
            event.register(BlockPreviewRenderState.class, BlockPreviewRenderer::new);
        }

        @SubscribeEvent
        public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(ChiselEntities.BALL_O_MOSS.get(),
                    ThrownItemRenderer::new);
            event.registerEntityRenderer(ChiselEntities.CLOUD_IN_A_BOTTLE.get(),
                    ThrownItemRenderer::new);
        }
    }
}
