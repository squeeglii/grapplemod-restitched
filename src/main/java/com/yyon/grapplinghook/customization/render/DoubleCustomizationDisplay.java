package com.yyon.grapplinghook.customization.render;

import com.yyon.grapplinghook.client.gui.widget.CustomizationSlider;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.DoubleProperty;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class DoubleCustomizationDisplay extends AbstractCustomizationDisplay<Double, DoubleProperty> {

    public DoubleCustomizationDisplay(DoubleProperty property) {
        super(property);
    }

    @Override
    public Component getModificationHint(Double value) {
        if(value == null) return null;
        double v = Math.floor(value * 100) / 100;
        return this.getProperty().getDisplayName().copy().append(": %.3f".formatted(v));
    }

    @Override
    public AbstractWidget getConfigurationUIElement(Supplier<CustomizationVolume> source, Screen context, Runnable onUpdate, int x, int y, int advisedWidth, int advisedHeight) {
        return new CustomizationSlider(context, source, x, y, advisedWidth, advisedHeight, this.getProperty(), onUpdate);
    }
}
