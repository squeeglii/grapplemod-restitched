package com.yyon.grapplinghook.config;

import com.yyon.grapplinghook.GrappleMod;

// HEY! FUTURE MAINTAINER!
// These features are not synced to the client!!!
// If you want them to be accessible across both the client & the server, sync
// them every time they are changed (mark dirty and do it on the next tick?)

// todo: ensure this is actually implemented - I think there was some work to make this a datapack thing?

/**
 * A set of boolean toggles for general features that should be enabled
 * or disabled on the server.
 */
public class ServerFeatures {

    private boolean blockingOldLongFallBootsRecipe;


    public ServerFeatures() {
        this.reset(); // assign defaults.
    }


    public boolean isBlockingOldLongFallBootsRecipe() {
        return this.blockingOldLongFallBootsRecipe;
    }

    public void setBlockingOldLongFallBootsRecipe(boolean blockingOldLongFallBootsRecipe) {
        this.blockingOldLongFallBootsRecipe = blockingOldLongFallBootsRecipe;
    }


    public static ServerFeatures get() {
        return GrappleMod.get().getServerFeatures();
    }


    // Update these each time a new field is added
    // todo: make this more difficult to break.
    
    public void reset() {
        this.blockingOldLongFallBootsRecipe = false;
    }

    public void copyFrom(ServerFeatures source) {
        this.blockingOldLongFallBootsRecipe = source.blockingOldLongFallBootsRecipe;
    }
}
