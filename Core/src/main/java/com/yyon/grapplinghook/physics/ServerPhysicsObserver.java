package com.yyon.grapplinghook.physics;

import com.yyon.grapplinghook.content.registry.GrappleModAdvancementTriggers;
import net.minecraft.server.level.ServerPlayer;


/**
 * All the custom physics is handled on the client side, however
 * advancements and other trackers run on the server-side.
 * This class acts as a bridge between the two, allowing server-side
 * access to some of the calculations made with the physics. This data
 * could be stale, so it should not be used for simulating physics on
 * the server side.
 * Currently though, it's not stored - just passed through to the advancement
 * trigger.
 */
public class ServerPhysicsObserver {

    public void receiveNewFrame(ServerPlayer player, PlayerPhysicsFrame frame) {
        GrappleModAdvancementTriggers.PHYSICS_UPDATE_TRIGGER.get().trigger(player, frame);
    }

}
