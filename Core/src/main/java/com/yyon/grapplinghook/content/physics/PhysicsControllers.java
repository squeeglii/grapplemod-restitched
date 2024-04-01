package com.yyon.grapplinghook.content.physics;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.resources.ResourceLocation;

/**
 * Physics are handled on the client side, however, these IDs are useful
 * on the server side for advancements.
 */
public class PhysicsControllers {

    public static final ResourceLocation GRAPPLING_HOOK = GrappleMod.id("grappling_hook");
    public static final ResourceLocation AIR_FRICTION = GrappleMod.id("air_friction");
    public static final ResourceLocation FORCEFIELD = GrappleMod.id("forcefield");

}
