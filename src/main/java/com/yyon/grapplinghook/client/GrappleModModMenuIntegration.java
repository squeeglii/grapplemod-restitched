package com.yyon.grapplinghook.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.yyon.grapplinghook.config.GrappleConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class GrappleModModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return screen -> AutoConfig.getConfigScreen(GrappleConfig.class, screen).get();
    }

}
