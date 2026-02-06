package com.yyon.grapplinghook.content.registry.internal;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.gui.menu.ModificationTableMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class ModMenus {

    public static final MenuType<ModificationTableMenu> MODIFICATION_TABLE = Registry.register(
            BuiltInRegistries.MENU,
            GrappleMod.id("modification_table"),
            new MenuType<>(ModificationTableMenu::new, FeatureFlags.VANILLA_SET)
    );

    public static void bump() {
        GrappleMod.LOGGER.info("Registering Mod Menus");
    }

}
