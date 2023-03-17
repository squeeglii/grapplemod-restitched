package com.yyon.grapplinghook.customization;

public class CustomizationProperty<T> {

    //TODO:
    // 3 components requires:
    // - Type: A handler of a given property type (such as double or boolean currently)
    //    - Handles rendering of elements in UI
    //     - Handles verification of
    // - Key: An overview of the defaults for a property, so that a value can be mapped to it.
    //     - Can store the min, max for a double for example
    //     - Stores name and config status
    // - Category: A reference with an associated upgrade, linked to a range of keys.

    private String identifier;
    private CustomizationType<T> valueType;

    private T type;
    private T value;

    private CustomizationAvailability status;

}
