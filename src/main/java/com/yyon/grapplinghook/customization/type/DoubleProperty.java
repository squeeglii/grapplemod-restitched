package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.CustomizationProperty;
import com.yyon.grapplinghook.customization.CustomizationRendererProvider;

public class DoubleProperty extends CustomizationProperty<Double> {

    public DoubleProperty(double defaultValue, double max, double min) {
        super(defaultValue);
    }

    @Override
    public CustomizationRendererProvider<Double, CustomizationProperty<Double>> getRenderer() {
        return null;
    }
}
