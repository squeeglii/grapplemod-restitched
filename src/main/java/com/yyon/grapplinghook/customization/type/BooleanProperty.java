package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.render.AbstractCustomizationRenderer;

public class BooleanProperty extends CustomizationProperty<Boolean> {

    public BooleanProperty(boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public AbstractCustomizationRenderer<Boolean, CustomizationProperty<Boolean>> getRenderer() {
        throw new UnsupportedOperationException("Unimplemented");
    }
}
