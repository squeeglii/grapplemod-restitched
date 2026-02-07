package com.yyon.grapplinghook.content.customization.display;

import com.yyon.grapplinghook.client.gui.widget.CustomizationPicker;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.content.customization.type.EnumProperty;
import com.yyon.grapplinghook.util.IFriendlyNameProvider;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class EnumPropertyDisplay<E extends Enum<E>> extends AbstractPropertyDisplay<E, EnumProperty<E>> {

    public EnumPropertyDisplay(EnumProperty<E> property) {
        super(property);
    }

    @Override
    public Component getValueHint(E value) {
        if(value == null) return null;
        return this.getValueTranslationKey(value);
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
    public AbstractWidget getConfigurationUIElement(Supplier<HookCustomization> source, Screen context, Runnable onUpdate, int x, int y, int advisedWidth, int advisedHeight) {
        return new CustomizationPicker<>(source, x, y, advisedWidth, advisedHeight, this.getProperty(), onUpdate);
    }
}
