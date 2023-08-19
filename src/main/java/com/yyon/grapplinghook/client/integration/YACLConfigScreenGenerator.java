package com.yyon.grapplinghook.client.integration;

import dev.isxander.yacl3.api.YetAnotherConfigLib;
import net.minecraft.client.gui.screens.Screen;

public class YACLConfigScreenGenerator {

    public static Screen getScreen(Screen lastScreen) {
        return YetAnotherConfigLib.createBuilder()
                .build()
                .generateScreen(lastScreen);
    }
}
