package com.leclowndu93150.chisel.data;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.data.provider.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Chisel.MODID)
public class ChiselDataGenerators {

    @SubscribeEvent
    public static void gatherClientData(GatherDataEvent.Client event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new ChiselBlockStateProvider(output));
        generator.addProvider(true, new ChiselItemModelProvider(output));
        generator.addProvider(true, new ChiselLanguageProvider(output));

        ChiselBlockTagsProvider blockTagsProvider = new ChiselBlockTagsProvider(output, lookupProvider);
        generator.addProvider(true, blockTagsProvider);
        generator.addProvider(true, new ChiselItemTagsProvider(output, lookupProvider));
        generator.addProvider(true, new ChiselRecipeProvider.Runner(output, lookupProvider));
        generator.addProvider(true, new ChiselLootTableProvider(output, lookupProvider));
        generator.addProvider(true, new ChiselWorldGenProvider(output, lookupProvider));
    }
}
