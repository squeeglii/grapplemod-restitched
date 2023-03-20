package com.yyon.grapplinghook.customization.render;

import com.yyon.grapplinghook.client.gui.GrappleModifierBlockGUI;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public abstract class AbstractCustomizationRenderer<T, P extends CustomizationProperty<T>> {

    private final P property;

    public AbstractCustomizationRenderer(P property) {
        this.property = property;
    }

    public Component getDisplayName() {
        ResourceLocation id = this.getProperty().getIdentifier();
        return id == null
                ? Component.literal("grapple_property.invalid.name")
                : Component.translatable("grapple_property."+id.toLanguageKey()+".name");
    }


    public abstract AbstractWidget getConfigurationUIElement(Supplier<CustomizationVolume> source, Screen context, Runnable onUpdate, int x, int y, int advisedWidth, int advisedHeight);

    public final AbstractWidget getModifierBlockUI(GrappleModifierBlockGUI gui, int x, int y) {
        return this.getConfigurationUIElement(gui::getCurrentCustomizations, gui, gui::markConfigurationsDirty, x, y, GrappleModifierBlockGUI.FULL_SIZE_X - 20, 20);
    }

    public P getProperty() {
        return this.property;
    }
}
