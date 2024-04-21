package me.cg360.mod.grapplemod.compat.gliders;

import com.mojang.logging.LogUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;

public class GlidersCompatEntrypoint implements ClientModInitializer {

    private static boolean hasLoaded = false;

    @Override
    public void onInitializeClient() {
        Logger logger = LogUtils.getLogger();
        if(!FabricLoader.getInstance().isModLoaded("vc_gliders")) {
            logger.info("'vc_gliders' is not installed. Disabling compatibility");
            return;
        }

        logger.info("'vc_gliders' is installed. Enabling compatibility [client-only]");

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
