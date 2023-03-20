package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.render.CustomizationRendererProvider;

public class DoubleProperty extends CustomizationProperty<Double> {

    protected double min;
    protected double max;

    public DoubleProperty(double defaultValue, double min, double max) {
        super(defaultValue);
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
    }

    @Override
    public CustomizationRendererProvider<Double, CustomizationProperty<Double>> getRenderer() {
        throw new UnsupportedOperationException("Unimplemented");
    }
}
