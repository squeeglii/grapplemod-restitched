package com.yyon.grapplinghook.content.customization.helper;

import com.yyon.grapplinghook.content.customization.type.CustomizationProperty;

public record PropertyOverride<T>(CustomizationProperty<T> property, T value) {

    public PropertyOverride {
        if(property == null) throw new IllegalArgumentException("Property cannot be null");
    }

}