package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.item.ItemBallOMoss;
import com.leclowndu93150.chisel.item.ItemChisel;
import com.leclowndu93150.chisel.item.ItemChisel.ChiselType;
import com.leclowndu93150.chisel.item.ItemCloudInABottle;
import com.leclowndu93150.chisel.item.ItemOffsetTool;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public class ChiselItems {

    public static final DeferredItem<ItemChisel> IRON_CHISEL = ChiselRegistries.ITEMS.registerItem("iron_chisel",
            props -> new ItemChisel(ChiselType.IRON, props),
            () -> new Item.Properties().stacksTo(1).durability(ChiselType.IRON.getMaxDamage()));

    public static final DeferredItem<ItemChisel> DIAMOND_CHISEL = ChiselRegistries.ITEMS.registerItem("diamond_chisel",
            props -> new ItemChisel(ChiselType.DIAMOND, props),
            () -> new Item.Properties().stacksTo(1).durability(ChiselType.DIAMOND.getMaxDamage()));

    public static final DeferredItem<ItemChisel> HITECH_CHISEL = ChiselRegistries.ITEMS.registerItem("hitech_chisel",
            props -> new ItemChisel(ChiselType.HITECH, props),
            () -> new Item.Properties().stacksTo(1).durability(ChiselType.HITECH.getMaxDamage()));

    public static final DeferredItem<ItemOffsetTool> OFFSET_TOOL = ChiselRegistries.ITEMS.registerItem("offset_tool",
            ItemOffsetTool::new,
            () -> new Item.Properties().stacksTo(1));

    public static final DeferredItem<BlockItem> AUTO_CHISEL = ChiselRegistries.ITEMS.registerItem("auto_chisel",
            props -> new BlockItem(ChiselBlocks.AUTO_CHISEL.get(), props),
            () -> new Item.Properties());

    public static final DeferredItem<ItemBallOMoss> BALL_O_MOSS = ChiselRegistries.ITEMS.registerItem("ball_o_moss",
            ItemBallOMoss::new,
            () -> new Item.Properties().stacksTo(16));

    public static final DeferredItem<ItemCloudInABottle> CLOUD_IN_A_BOTTLE = ChiselRegistries.ITEMS.registerItem("cloud_in_a_bottle",
            ItemCloudInABottle::new,
            () -> new Item.Properties().stacksTo(16));

    public static void init() {
    }
}
