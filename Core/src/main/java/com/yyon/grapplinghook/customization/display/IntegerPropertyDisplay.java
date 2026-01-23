package com.yyon.grapplinghook.customization.display;

import com.yyon.grapplinghook.client.gui.widget.SteppedCustomizationSlider;
import com.yyon.grapplinghook.customization.data.HookCustomization;
import com.yyon.grapplinghook.customization.type.IntegerProperty;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class IntegerPropertyDisplay extends AbstractPropertyDisplay<Integer, IntegerProperty> {

    public IntegerPropertyDisplay(IntegerProperty property) {
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
    public AbstractWidget getConfigurationUIElement(Supplier<HookCustomization> source, Screen context, Runnable onUpdate, int x, int y, int advisedWidth, int advisedHeight) {
        return new SteppedCustomizationSlider(source, x, y, advisedWidth, advisedHeight, this.getProperty(), onUpdate);
    }
}
