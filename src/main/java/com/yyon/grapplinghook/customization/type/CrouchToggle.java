package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.keybind.GrappleModKey;
import com.yyon.grapplinghook.client.keybind.MinecraftKey;
import com.yyon.grapplinghook.util.FriendlyNameProvider;

public enum CrouchToggle implements FriendlyNameProvider {

    ALWAYS, WHEN_CROUCHING, WHEN_NOT_CROUCHING;

    public boolean isActive(GrappleModKey keybind) {
        return this.isActive(GrappleModClient.get().isKeyDown(keybind));
    }

    public boolean isActive(MinecraftKey keybind) {
        return this.isActive(GrappleModClient.get().isKeyDown(keybind));
    }

    public boolean isActive(boolean isKeyDown) {
        if(this == ALWAYS) return true;
        return (this == WHEN_CROUCHING) == isKeyDown;
    }

    @Override
    public String getFriendlyName() {
        return "crouch_activation";
    }
}
