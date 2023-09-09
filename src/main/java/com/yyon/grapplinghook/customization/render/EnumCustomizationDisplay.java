package com.yyon.grapplinghook.customization.render;

import com.yyon.grapplinghook.client.gui.widget.CustomizationPicker;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.EnumProperty;
import com.yyon.grapplinghook.util.IFriendlyNameProvider;
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
        Component valTranslation = this.getValueTranslationKey(value);
        return this.getProperty().getDisplayName().copy().append(": ").append(valTranslation);
    }

    public Component getValueTranslationKey(E value) {
        String type = value instanceof IFriendlyNameProvider friendly
                ? friendly.getFriendlyName()
                : this.getProperty().getIdentifier().toLanguageKey();

        return Component.translatable("enum.%s.%s".formatted(
                type,
                value == null
                        ? "null"
                        : value.name().toLowerCase()
        ));
    }

    @Override
    public AbstractWidget getConfigurationUIElement(Supplier<CustomizationVolume> source, Screen context, Runnable onUpdate, int x, int y, int advisedWidth, int advisedHeight) {
        return new CustomizationPicker<>(source, x, y, advisedWidth, advisedHeight, this.getProperty(), onUpdate);
    }
}
