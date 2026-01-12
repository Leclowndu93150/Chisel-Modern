package com.leclowndu93150.chisel.data;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.data.provider.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * Event handler for data generation.
 */
@Mod.EventBusSubscriber(modid = Chisel.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChiselDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        LenientExistingFileHelper lenientHelper = new LenientExistingFileHelper(
                Collections.emptyList(),
                Collections.singleton(Chisel.MODID),
                true,
                null,
                null
        );

        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new ChiselBlockStateProvider(output, lenientHelper));
        generator.addProvider(event.includeClient(), new ChiselItemModelProvider(output));
        generator.addProvider(event.includeClient(), new ChiselLanguageProvider(output));

        ChiselBlockTagsProvider blockTagsProvider = new ChiselBlockTagsProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new ChiselItemTagsProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new ChiselRecipeProvider(output));
        generator.addProvider(event.includeServer(), new ChiselLootTableProvider(output));
        generator.addProvider(event.includeServer(), new ChiselWorldGenProvider(output, lookupProvider));

        Runtime.getRuntime().addShutdownHook(new Thread(lenientHelper::printMissingSummary));
    }
}
