package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.item.ItemChisel;
import com.leclowndu93150.chisel.item.ItemChisel.ChiselType;
import com.leclowndu93150.chisel.item.ItemOffsetTool;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;

public class ChiselItems {

    public static final RegistryObject<ItemChisel> IRON_CHISEL = ChiselRegistries.ITEMS.register("iron_chisel",
            () -> new ItemChisel(ChiselType.IRON, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<ItemChisel> DIAMOND_CHISEL = ChiselRegistries.ITEMS.register("diamond_chisel",
            () -> new ItemChisel(ChiselType.DIAMOND, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<ItemChisel> HITECH_CHISEL = ChiselRegistries.ITEMS.register("hitech_chisel",
            () -> new ItemChisel(ChiselType.HITECH, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<ItemOffsetTool> OFFSET_TOOL = ChiselRegistries.ITEMS.register("offset_tool",
            () -> new ItemOffsetTool(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<BlockItem> AUTO_CHISEL = ChiselRegistries.ITEMS.register("auto_chisel",
            () -> new BlockItem(ChiselBlocks.AUTO_CHISEL.get(), new Item.Properties()));

    public static void init() {
    }
}
