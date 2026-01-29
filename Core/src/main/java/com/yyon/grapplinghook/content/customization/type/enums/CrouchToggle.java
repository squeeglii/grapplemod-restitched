package com.yyon.grapplinghook.content.customization.type.enums;

import com.mojang.serialization.Codec;
import com.yyon.grapplinghook.util.IFriendlyNameProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum CrouchToggle implements IFriendlyNameProvider, StringRepresentable {

    ALWAYS, WHEN_CROUCHING, WHEN_NOT_CROUCHING;

    public static final Codec<CrouchToggle> CODEC = StringRepresentable.fromValues(CrouchToggle::values);

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

    @NotNull
    @Override
    public String getSerializedName() {
        return this.name();
    }
}
