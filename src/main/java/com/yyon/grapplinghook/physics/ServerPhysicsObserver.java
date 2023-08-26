package com.yyon.grapplinghook.physics;

import com.yyon.grapplinghook.GrappleMod;

/**
 * All the custom physics is handled on the client side, however
 * advancements and other trackers run on the server-side.
 *
 * This class acts as a bridge between the two, allowing server-side
 * access to some of the calculations made with the physics. This data
 * could be stale, so it should not be used for simulating physics on
 * the server side.
 */
public class ServerPhysicsObserver {


    public void receiveNewFrame(PlayerPhysicsFrame frame) {
        GrappleMod.LOGGER.info(frame.toString());
    }

}
