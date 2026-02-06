package com.yyon.grapplinghook.content.registry.internal;

import com.yyon.grapplinghook.client.gui.menu.ModificationTableMenu;
import com.yyon.grapplinghook.client.gui.screen.ModificationTableMenuScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class ModMenuScreens {

    public static void registerAll() {
        MenuScreens.register(ModMenus.MODIFICATION_TABLE, ModificationTableMenuScreen::new);
    }

}
