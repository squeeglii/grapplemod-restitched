package com.yyon.grapplinghook.customization;

public abstract class CustomizationProperty<T> {

    //TODO:
    // 3 components requires:
    // - Renderer: A handler of a given property type (such as double or boolean currently)
    //    - Handles rendering of elements in UI
    // - Property: An overview of the defaults for a property, so that a value can be mapped to it.
    //     - Can store the min, max for a double for example
    //     - Stores name and config status
    // - Category: A reference with an associated upgrade, linked to a range of keys.

    private CustomizationAvailability status; // config can update this at any time.

    private final T defaultValue;


    public CustomizationProperty(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public abstract CustomizationRendererProvider<T, CustomizationProperty<T>> getRenderer();


    public CustomizationAvailability getAvailability() {
        return this.status;
    }

    public final T getDefaultValue() {
        return this.defaultValue;
    }

}
