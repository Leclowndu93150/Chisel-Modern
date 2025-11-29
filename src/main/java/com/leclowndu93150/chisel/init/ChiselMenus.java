package com.leclowndu93150.chisel.init;

import com.leclowndu93150.chisel.inventory.AutoChiselMenu;
import com.leclowndu93150.chisel.inventory.ChiselMenu;
import com.leclowndu93150.chisel.inventory.HitechChiselMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;

public class ChiselMenus {

    public static final RegistryObject<MenuType<ChiselMenu>> CHISEL_MENU =
            ChiselRegistries.MENU_TYPES.register("chisel", () -> IForgeMenuType.create(ChiselMenu::new));

    public static final RegistryObject<MenuType<HitechChiselMenu>> HITECH_CHISEL_MENU =
            ChiselRegistries.MENU_TYPES.register("hitech_chisel", () -> IForgeMenuType.create(HitechChiselMenu::new));

    public static final RegistryObject<MenuType<AutoChiselMenu>> AUTO_CHISEL_MENU =
            ChiselRegistries.MENU_TYPES.register("auto_chisel", () -> IForgeMenuType.create(AutoChiselMenu::new));

    public static void init() {
        ChiselMenu.MENU_TYPE_SUPPLIER = CHISEL_MENU::get;
        HitechChiselMenu.MENU_TYPE_SUPPLIER = HITECH_CHISEL_MENU::get;
    }
}
