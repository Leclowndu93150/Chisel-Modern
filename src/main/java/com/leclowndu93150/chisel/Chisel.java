package com.leclowndu93150.chisel;

import com.leclowndu93150.chisel.carving.ChiselMode;
import com.leclowndu93150.chisel.client.gui.AutoChiselScreen;
import com.leclowndu93150.chisel.client.gui.ChiselScreen;
import com.leclowndu93150.chisel.client.gui.HitechChiselScreen;
import com.leclowndu93150.chisel.init.ChiselBlockEntities;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselCreativeTabs;
import com.leclowndu93150.chisel.init.ChiselDataComponents;
import com.leclowndu93150.chisel.init.ChiselItems;
import com.leclowndu93150.chisel.init.ChiselMenus;
import com.leclowndu93150.chisel.init.ChiselRegistries;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(Chisel.MODID)
public class Chisel {
    public static final String MODID = "chisel";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Chisel(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        // Register all deferred registers
        ChiselRegistries.BLOCKS.register(modEventBus);
        ChiselRegistries.ITEMS.register(modEventBus);
        ChiselRegistries.CREATIVE_TABS.register(modEventBus);
        ChiselRegistries.BLOCK_ENTITY_TYPES.register(modEventBus);
        ChiselRegistries.MENU_TYPES.register(modEventBus);
        ChiselRegistries.SOUND_EVENTS.register(modEventBus);
        ChiselRegistries.DATA_COMPONENT_TYPES.register(modEventBus);

        ChiselBlocks.init();
        ChiselItems.init();
        ChiselMenus.init();
        ChiselCreativeTabs.init();
        ChiselDataComponents.init();
        ChiselBlockEntities.init();

        NeoForge.EVENT_BUS.register(this);

        // Register config
        modContainer.registerConfig(ModConfig.Type.COMMON, ChiselConfig.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("Chisel common setup");

        // Register chisel modes
        ChiselMode.registerAll();
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Chisel server starting");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }

    @EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            LOGGER.info("Chisel client setup");
        }

        @SubscribeEvent
        public static void registerMenuScreens(RegisterMenuScreensEvent event) {
            event.register(ChiselMenus.CHISEL_MENU.get(), ChiselScreen::new);
            event.register(ChiselMenus.HITECH_CHISEL_MENU.get(), HitechChiselScreen::new);
            event.register(ChiselMenus.AUTO_CHISEL_MENU.get(), AutoChiselScreen::new);
        }
    }
}
