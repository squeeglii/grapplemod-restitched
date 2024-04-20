package me.cg360.mod.grapplemod.compat.gliders;

import com.mojang.logging.LogUtils;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.api.GrappleModClientAPI;
import com.yyon.grapplinghook.client.api.GrappleModClientEvents;
import com.yyon.grapplinghook.content.physics.PhysicsControllers;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.venturecraft.gliders.util.GliderUtil;

public class GlidersCompatModule {

    public GlidersCompatModule() {
        LogUtils.getLogger().info("Enabled Gliders Compatibility Module");

        //todo oh god
        // Limit speed on hook physics when gliding
        // Fix weird acceleration (seems to be like infinity downwards)
        // Add some way to deploy the glider after using a hook (ditch hook & just glide?)

        ClientTickEvents.START_WORLD_TICK.register(level -> {
            LocalPlayer player = Minecraft.getInstance().player;

            if(player == null) return;
            if(!GliderUtil.isGliderActive(player)) return;

            ResourceLocation currentPhysicsType = GrappleModClientAPI.getPhysicsTypeFor(player);

            if(currentPhysicsType == PhysicsControllers.AIR_FRICTION) {
                GrappleModClientAPI.abortAllPhysicsOverrides(player);
            }
        });
    }
}
