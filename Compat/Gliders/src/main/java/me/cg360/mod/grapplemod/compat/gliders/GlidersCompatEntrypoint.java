package me.cg360.mod.grapplemod.compat.gliders;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

public class GlidersCompatEntrypoint implements ModInitializer {

    private static boolean hasLoaded = false;

    @Override
    public void onInitialize() {
        Logger logger = LogUtils.getLogger();
        if(!FabricLoader.getInstance().isModLoaded("vc_gliders")) {
            logger.info("'vc_gliders' is not installed. Disabling compatibility");
            return;
        }

        logger.info("'vc_gliders' is installed. Enabling compatibility");

        try {
            Class.forName("me.cg360.mod.grapplemod.compat.gliders.GlidersCompatModule")
                    .getConstructor()
                    .newInstance();

            hasLoaded = true;

        } catch (Exception err) {
            logger.error(err.getMessage());
        }
    }

}
