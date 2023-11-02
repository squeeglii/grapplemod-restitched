package com.yyon.grapplinghook.customization.render;

import com.yyon.grapplinghook.client.gui.widget.PreciseCustomizationSlider;
import com.yyon.grapplinghook.client.gui.widget.SteppedCustomizationSlider;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.IntegerProperty;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class IntegerCustomizationDisplay extends AbstractCustomizationDisplay<Integer, IntegerProperty> {

    public IntegerCustomizationDisplay(IntegerProperty property) {
        super(property);
    }

    @Override
    public Component getModificationHint(Integer value) {
        if(value == null) return null;
        return this.getProperty().getDisplayName()
                .copy()
                .append(": %s".formatted(value));
    }

    @Override
    public AbstractWidget getConfigurationUIElement(Supplier<CustomizationVolume> source, Screen context, Runnable onUpdate, int x, int y, int advisedWidth, int advisedHeight) {
        return new SteppedCustomizationSlider(source, x, y, advisedWidth, advisedHeight, this.getProperty(), onUpdate);
    }
}
