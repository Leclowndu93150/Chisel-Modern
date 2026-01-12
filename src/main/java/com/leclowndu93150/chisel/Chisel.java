package com.leclowndu93150.chisel;

import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.block.BlockCarvableGlass;
import com.leclowndu93150.chisel.block.BlockCarvablePane;
import com.leclowndu93150.chisel.carving.ChiselMode;
import com.leclowndu93150.chisel.client.gui.AutoChiselScreen;
import com.leclowndu93150.chisel.client.gui.ChiselScreen;
import com.leclowndu93150.chisel.client.gui.HitechChiselScreen;
import com.leclowndu93150.chisel.command.ChiselDebugCommands;
import com.leclowndu93150.chisel.compat.ChiselRebornCompat;
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
import com.leclowndu93150.chisel.network.ChiselNetwork;
import com.leclowndu93150.chisel.worldgen.ChiselBiomeModifiers;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.slf4j.Logger;

@Mod(Chisel.MODID)
public class Chisel {
    public static final String MODID = "chisel";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Chisel() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModContainer modContainer = ModLoadingContext.get().getContainer();
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        ChiselRebornCompat.init();

        ChiselNetwork.register();

        ChiselRegistries.BLOCKS.register(modEventBus);
        ChiselRegistries.ITEMS.register(modEventBus);
        ChiselRegistries.CREATIVE_TABS.register(modEventBus);
        ChiselRegistries.BLOCK_ENTITY_TYPES.register(modEventBus);
        ChiselRegistries.MENU_TYPES.register(modEventBus);
        ChiselRegistries.SOUND_EVENTS.register(modEventBus);
        ChiselRegistries.PARTICLE_TYPES.register(modEventBus);
        ChiselRegistries.ENTITY_TYPES.register(modEventBus);

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

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ChiselConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        ChiselMode.registerAll();
    }

    private void registerCommands(RegisterCommandsEvent event) {
        ChiselDebugCommands.register(event.getDispatcher());
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MODID, path);
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
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
            for (net.minecraftforge.registries.RegistryObject<?> registryObject : blockType.getAllBlocks()) {
                Block block = (Block) registryObject.get();
                ItemBlockRenderTypes.setRenderLayer(block, renderType);
            }
        }

        @SubscribeEvent
        public static void registerMenuScreens(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                MenuScreens.register(ChiselMenus.CHISEL_MENU.get(), ChiselScreen::new);
                MenuScreens.register(ChiselMenus.HITECH_CHISEL_MENU.get(), HitechChiselScreen::new);
                MenuScreens.register(ChiselMenus.AUTO_CHISEL_MENU.get(), AutoChiselScreen::new);
            });
        }

        @SubscribeEvent
        public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ChiselParticles.HOLYSTONE_STAR.get(), HolystoneStarParticle.Provider::new);
        }
    }
}
