package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.CustomizationProperty;
import com.yyon.grapplinghook.customization.CustomizationRendererProvider;

public class EnumProperty<E extends Enum<E>> extends CustomizationProperty<E> {

    public EnumProperty(E defaultValue) {
        super(defaultValue);
    }

    @Override
    public CustomizationRendererProvider<E, CustomizationProperty<E>> getRenderer() {
        return null;
    }
}
