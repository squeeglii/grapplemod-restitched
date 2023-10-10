package me.cg360.mod.grapplemod.compat.gliders;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class GlidersCompatEntrypoint implements ModInitializer {

    @Override
    public void onInitialize() {
        if(!FabricLoader.getInstance().isModLoaded("vc_gliders"))
            return;

        try {
            Class.forName("me.cg360.mod.grapplemod.compat.gliders.GlidersCompatEntrypoint")
                    .getConstructor()
                    .newInstance();

        } catch (Exception err) {
            LogUtils.getLogger().error(err.getMessage());
        }
    }

}
