package com.yyon.grapplinghook.content.customization.display;

import com.yyon.grapplinghook.client.gui.widget.CustomizationCheckbox;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.content.customization.type.BooleanProperty;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class BooleanPropertyDisplay extends AbstractPropertyDisplay<Boolean, BooleanProperty> {

    public BooleanPropertyDisplay(BooleanProperty property) {
        super(property);
    }

    @Override
    public Component getValueHint(Boolean value) {
        if(value == null) return null;
        String checkboxString = value
                ? "[✓]"
                : "[ ]";
        return Component.literal(checkboxString);
    }

    @Override
    public AbstractWidget getConfigurationUIElement(Supplier<HookCustomization> source, Screen context, Runnable onUpdate, int x, int y, int advisedWidth, int advisedHeight) {
        return new CustomizationCheckbox(source, x, y, advisedWidth, this.getProperty(), onUpdate);
    }

}
