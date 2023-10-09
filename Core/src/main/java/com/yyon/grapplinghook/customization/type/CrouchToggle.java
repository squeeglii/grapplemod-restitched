package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.util.IFriendlyNameProvider;
import net.minecraft.client.KeyMapping;

public enum CrouchToggle implements IFriendlyNameProvider {

    ALWAYS, WHEN_CROUCHING, WHEN_NOT_CROUCHING;

    public boolean meetsActivationCondition(KeyMapping keyMapping) {
        return this.meetsActivationCondition(keyMapping.isDown());
    }

    public boolean meetsActivationCondition(boolean isKeyDown) {
        if(this == ALWAYS) return true;
        return (this == WHEN_CROUCHING) == isKeyDown;
    }

    @Override
    public String getFriendlyName() {
        return "crouch_activation";
    }
}
