package com.leclowndu93150.chisel.data;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.data.provider.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * Event handler for data generation.
 */
@EventBusSubscriber(modid = Chisel.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ChiselDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // Use lenient file helper that logs missing textures instead of crashing
        LenientExistingFileHelper lenientHelper = new LenientExistingFileHelper(
                Collections.emptyList(),
                Collections.singleton(Chisel.MODID),
                true,
                null,
                null
        );

        // Also keep reference to original for tags
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        // Client-side data (use lenient helper for models)
        generator.addProvider(event.includeClient(), new ChiselBlockStateProvider(output, lenientHelper));
        generator.addProvider(event.includeClient(), new ChiselItemModelProvider(output));
        generator.addProvider(event.includeClient(), new ChiselLanguageProvider(output));

        // Server-side data (use original helper for tags)
        ChiselBlockTagsProvider blockTagsProvider = new ChiselBlockTagsProvider(output, lookupProvider, existingFileHelper);
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new ChiselItemTagsProvider(output, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new ChiselRecipeProvider(output, lookupProvider));
        generator.addProvider(event.includeServer(), new ChiselLootTableProvider(output, lookupProvider));

        // World generation data (configured features, placed features, biome modifiers)
        generator.addProvider(event.includeServer(), new ChiselWorldGenProvider(output, lookupProvider));

        // Print missing resources summary at the end
        Runtime.getRuntime().addShutdownHook(new Thread(lenientHelper::printMissingSummary));
    }
}
