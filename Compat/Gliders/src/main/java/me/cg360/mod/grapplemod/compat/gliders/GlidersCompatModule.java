package me.cg360.mod.grapplemod.compat.gliders;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class GlidersCompatModule implements ModInitializer {

    @Override
    public void onInitialize() {
        if(!FabricLoader.getInstance().isModLoaded("vc_gliders")) return;
    }

}
