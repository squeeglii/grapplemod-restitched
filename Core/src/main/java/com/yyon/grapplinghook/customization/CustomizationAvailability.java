package com.yyon.grapplinghook.customization;

import net.minecraft.network.chat.Component;

public enum CustomizationAvailability {

    ALLOWED,
    REQUIRES_LIMITS,
    BLOCKED;

    public Component getTranslationString() {
        return Component.translatable(
                "grapple_customization.availability.%s"
                .formatted(this.name().toLowerCase())
        );
    }
}
