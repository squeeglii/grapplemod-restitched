package com.yyon.grapplinghook.content.customization.display;

import com.yyon.grapplinghook.client.gui.widget.SteppedCustomizationSlider;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.content.customization.type.IntegerProperty;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class IntegerPropertyDisplay extends AbstractPropertyDisplay<Integer, IntegerProperty> {

    public IntegerPropertyDisplay(IntegerProperty property) {
        super(property);
    }

    @Override
    public Component getValueHint(Integer value) {
        if(value == null) return null;
        return Component.literal(String.valueOf(value));
    }

    @Override
    public AbstractWidget getConfigurationUIElement(Supplier<HookCustomization> source, Screen context, Runnable onUpdate, int x, int y, int advisedWidth, int advisedHeight) {
        return new SteppedCustomizationSlider(source, x, y, advisedWidth, advisedHeight, this.getProperty(), onUpdate);
    }
}
