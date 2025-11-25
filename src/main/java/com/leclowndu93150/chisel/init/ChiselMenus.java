package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.inventory.AutoChiselMenu;
import com.leclowndu93150.chisel.inventory.ChiselMenu;
import com.leclowndu93150.chisel.inventory.HitechChiselMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * Registration class for Chisel menus (containers).
 */
public class ChiselMenus {

    public static final DeferredHolder<MenuType<?>, MenuType<ChiselMenu>> CHISEL_MENU =
            ChiselRegistries.MENU_TYPES.register("chisel", () -> IMenuTypeExtension.create(ChiselMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<HitechChiselMenu>> HITECH_CHISEL_MENU =
            ChiselRegistries.MENU_TYPES.register("hitech_chisel", () -> IMenuTypeExtension.create(HitechChiselMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<AutoChiselMenu>> AUTO_CHISEL_MENU =
            ChiselRegistries.MENU_TYPES.register("auto_chisel", () -> IMenuTypeExtension.create(AutoChiselMenu::new));

    /**
     * Called during mod initialization to trigger static initialization.
     */
    public static void init() {
        ChiselMenu.MENU_TYPE_SUPPLIER = CHISEL_MENU::get;
        HitechChiselMenu.MENU_TYPE_SUPPLIER = HITECH_CHISEL_MENU::get;
    }
}
