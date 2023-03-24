package com.yyon.grapplinghook.customization.render;

import com.yyon.grapplinghook.client.gui.widget.CustomizationCheckbox;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.BooleanProperty;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class BooleanCustomizationDisplay extends AbstractCustomizationDisplay<Boolean, BooleanProperty> {

    public BooleanCustomizationDisplay(BooleanProperty property) {
        super(property);
    }

    @Override
    public Component getModificationHint(Boolean value) {
        if(value == null) return null;
        String checkboxString = value
                ? "[x]"
                : "[ ]";
        return this.getProperty().getDisplayName().copy().append(": " + checkboxString);
    }

    @Override
    public AbstractWidget getConfigurationUIElement(Supplier<CustomizationVolume> source, Screen context, Runnable onUpdate, int x, int y, int advisedWidth, int advisedHeight) {
        return new CustomizationCheckbox(context, source, x, y, advisedWidth, advisedHeight, this.getProperty(), onUpdate);
    }

}
