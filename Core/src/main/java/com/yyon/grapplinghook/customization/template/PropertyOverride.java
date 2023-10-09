package com.yyon.grapplinghook.customization.template;

import com.yyon.grapplinghook.customization.type.CustomizationProperty;

public record PropertyOverride<T>(CustomizationProperty<T> property, T value) {

    public PropertyOverride {
        if(property == null) throw new IllegalArgumentException("Property cannot be null");
    }

}
