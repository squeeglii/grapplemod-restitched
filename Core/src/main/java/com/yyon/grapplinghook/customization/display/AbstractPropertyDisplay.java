package com.yyon.grapplinghook.customization.display;

import com.yyon.grapplinghook.client.gui.GrappleModifierBlockGUI;
import com.yyon.grapplinghook.customization.data.HookCustomization;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public abstract class AbstractPropertyDisplay<T, P extends CustomizationProperty<T>> {

    private final P property;

    public AbstractPropertyDisplay(P property) {
        this.property = property;
    }

    public Component getModificationHint(HookCustomization volume) {
        if(!volume.has(this.property)) return null;
        return this.getModificationHint(volume.get(this.property));
    }

    public abstract Component getModificationHint(T value);


    public abstract AbstractWidget getConfigurationUIElement(Supplier<HookCustomization> source, Screen context, Runnable onUpdate, int x, int y, int advisedWidth, int advisedHeight);

    public final AbstractWidget getModifierBlockUI(GrappleModifierBlockGUI gui, int x, int y) {
        return this.getConfigurationUIElement(gui::getCurrentCustomizations, gui, gui::markConfigurationsDirty, x, y, GrappleModifierBlockGUI.FULL_SIZE_X - (2 * GrappleModifierBlockGUI.OUTER_PADDING_X), 20);
    }

    public final P getProperty() {
        return this.property;
    }
}
