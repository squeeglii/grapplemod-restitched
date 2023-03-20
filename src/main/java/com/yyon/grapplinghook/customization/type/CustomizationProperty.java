package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.content.registry.GrappleModMetaRegistry;
import com.yyon.grapplinghook.customization.CustomizationAvailability;
import com.yyon.grapplinghook.customization.render.AbstractCustomizationRenderer;
import net.minecraft.resources.ResourceLocation;

public abstract class CustomizationProperty<T> {

    private CustomizationAvailability status; // config can update this at any time.

    private final T defaultValue;


    public CustomizationProperty(T defaultValue) {
        this.defaultValue = defaultValue;
        this.status = CustomizationAvailability.ALLOWED;
    }

    public abstract AbstractCustomizationRenderer<T, CustomizationProperty<T>> getRenderer();


    public ResourceLocation getIdentifier() {
        return GrappleModMetaRegistry.CUSTOMIZATION_PROPERTIES.getKey(this);
    }

    public CustomizationAvailability getAvailability() {
        return this.status;
    }

    public CustomizationProperty<T> setStatus(CustomizationAvailability status) {
        this.status = status;
        return this;
    }

    public final T getDefaultValue() {
        return this.defaultValue;
    }

}
