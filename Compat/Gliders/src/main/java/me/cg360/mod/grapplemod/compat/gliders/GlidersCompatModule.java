package me.cg360.mod.grapplemod.compat.gliders;

import com.mojang.logging.LogUtils;

public class GlidersCompatModule {

    public GlidersCompatModule() {
        LogUtils.getLogger().info("Enabled Gliders Compatibility Module");

        //todo oh god
        // Limit speed on hook physics when gliding
        // Fix weird acceleration (seems to be like infinity downwards)
        // Add some way to deploy the glider after using a hook (ditch hook & just glide?)

    }
}
