package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.render.CustomizationRendererProvider;

public class BooleanProperty extends CustomizationProperty<Boolean> {

    public BooleanProperty(boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public CustomizationRendererProvider<Boolean, CustomizationProperty<Boolean>> getRenderer() {
        throw new UnsupportedOperationException("Unimplemented");
    }
}
