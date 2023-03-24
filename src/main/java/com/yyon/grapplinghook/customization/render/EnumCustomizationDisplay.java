package com.yyon.grapplinghook.customization.render;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.EnumProperty;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class EnumCustomizationDisplay<E extends Enum<E>> extends AbstractCustomizationDisplay<E, EnumProperty<E>> {

    public EnumCustomizationDisplay(EnumProperty<E> property) {
        super(property);
    }

    @Override
    public Component getModificationHint(E value) {
        if(value == null) return null;

        Component translation = Component.translatable("enum.%s.%s".formatted(
                this.getProperty().getIdentifier().toLanguageKey(),
                value.name().toLowerCase())
        );

        return this.getProperty().getDisplayName().copy().append(": ").append(translation);
    }

    @Override
    public AbstractWidget getConfigurationUIElement(Supplier<CustomizationVolume> source, Screen context, Runnable onUpdate, int x, int y, int advisedWidth, int advisedHeight) {
        return null;
    }
}
