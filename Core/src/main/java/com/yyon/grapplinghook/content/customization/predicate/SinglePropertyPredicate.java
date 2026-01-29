package com.yyon.grapplinghook.content.customization.predicate;

import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.content.customization.type.CustomizationProperty;

public abstract class SinglePropertyPredicate<T> implements PropertyPredicate<T> {

    private final CustomizationProperty<T> property;

    public SinglePropertyPredicate(CustomizationProperty<T> property) {
        if(property == null) throw new IllegalArgumentException("Property cannot be null");
        this.property = property;
    }

    @Override
    public boolean shouldPass(HookCustomization volume) {
        return this.shouldPass(volume.get(this.property));
    }

    public final CustomizationProperty<T> getProperty() {
        return property;
    }
}
