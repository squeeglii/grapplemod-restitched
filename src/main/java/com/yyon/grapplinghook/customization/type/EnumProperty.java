package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.render.AbstractCustomizationRenderer;

public class EnumProperty<E extends Enum<E>> extends CustomizationProperty<E> {

    public EnumProperty(E defaultValue) {
        super(defaultValue);

        if(defaultValue == null) throw new IllegalStateException("Default enum value cannot be null.");
    }

    @Override
    public AbstractCustomizationRenderer<E, CustomizationProperty<E>> getRenderer() {
        throw new UnsupportedOperationException("Unimplemented");
    }
}
