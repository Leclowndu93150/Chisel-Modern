package com.leclowndu93150.chisel.data.provider;

import com.leclowndu93150.chisel.Chisel;
import com.leclowndu93150.chisel.api.block.ChiselBlockType;
import com.leclowndu93150.chisel.api.block.ICarvable;
import com.leclowndu93150.chisel.api.block.VariationData;
import com.leclowndu93150.chisel.block.BlockCarvableGlass;
import com.leclowndu93150.chisel.data.ChiselModelTemplates;
import com.leclowndu93150.chisel.init.ChiselBlocks;
import com.leclowndu93150.chisel.init.ChiselItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.Set;
import java.util.stream.Stream;

public class ChiselBlockStateProvider extends ModelProvider {

    private static final Set<Block> HAND_WRITTEN_BLOCKS = Set.of();
    private static final Set<Item> HAND_WRITTEN_ITEMS = Set.of();

    public ChiselBlockStateProvider(PackOutput output) {
        super(output, Chisel.MODID);
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return super.getKnownBlocks().filter(holder -> {
            Block block = holder.value();
            if (HAND_WRITTEN_BLOCKS.contains(block)) return false;
            if (block == ChiselBlocks.AUTO_CHISEL.get()) return false;
            return true;
        });
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> {
            Item item = holder.value();
            if (HAND_WRITTEN_ITEMS.contains(item)) return false;
            if (item == ChiselItems.AUTO_CHISEL.get()) return false;
            return true;
        });
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(ChiselItems.IRON_CHISEL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ChiselItems.DIAMOND_CHISEL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ChiselItems.HITECH_CHISEL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ChiselItems.OFFSET_TOOL.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ChiselItems.BALL_O_MOSS.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(ChiselItems.CLOUD_IN_A_BOTTLE.get(), ModelTemplates.FLAT_ITEM);

        for (ChiselBlockType<?> blockType : ChiselBlocks.ALL_BLOCK_TYPES) {
            ChiselModelTemplates.ChiselModelTemplate defaultTemplate = blockType.getDefaultModelTemplate();
            for (DeferredBlock<?> deferredBlock : blockType.getAllBlocks()) {
                Block block = deferredBlock.get();
                ChiselModelTemplates.ChiselModelTemplate template = defaultTemplate;
                if (block instanceof ICarvable carvable) {
                    VariationData variation = carvable.getVariation();
                    if (variation.modelTemplate() != null) {
                        template = variation.modelTemplate();
                    }
                }
                if (template == null) {
                    template = ChiselModelTemplates.simpleBlock();
                }
                if (block instanceof BlockCarvableGlass) {
                    template = ChiselModelTemplates.simpleBlockTranslucent();
                }
                template.apply(block, blockModels);
            }
        }
    }
}
