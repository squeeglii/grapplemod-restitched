package com.yyon.grapplinghook.customization.predicate;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;

public abstract class SingleCustomizationPredicate<T> implements CustomizationPredicate<T> {

    private final CustomizationProperty<T> property;

    public SingleCustomizationPredicate(CustomizationProperty<T> property) {
        if(property == null) throw new IllegalArgumentException("Property cannot be null");
        this.property = property;
    }

    @Override
    public boolean shouldPass(CustomizationVolume volume) {
        return this.shouldPass(volume.get(this.property));
    }

    public final CustomizationProperty<T> getProperty() {
        return property;
    }
}
