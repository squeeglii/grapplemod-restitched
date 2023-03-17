package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.CustomizationProperty;
import com.yyon.grapplinghook.customization.CustomizationRendererProvider;

public class BooleanProperty extends CustomizationProperty<Boolean> {

    public BooleanProperty(Boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public CustomizationRendererProvider<Boolean, CustomizationProperty<Boolean>> getRenderer() {
        return null;
    }
}
