package com.yyon.grapplinghook.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.DoubleProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class CustomizationSlider extends AbstractSliderButton implements CustomTooltipHandler {
    private final DoubleProperty option;
    private final Screen context;
    private final Supplier<CustomizationVolume> customizations;
    private final Runnable onValueUpdate;

    private Component tooltipOverride;

    public CustomizationSlider(Screen context, Supplier<CustomizationVolume> customizations, int x, int y, int w, int h, DoubleProperty option, Runnable onValueUpdate) {
        super(x, y, w, h, option.getDisplayName(), (convertDouble(customizations.get(), option) - option.getMin()) / (option.getMax() - option.getMin()));
        this.context = context;
        this.option = option;
        this.customizations = customizations;
        this.onValueUpdate = onValueUpdate;

        this.tooltipOverride = null;

        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        MutableComponent message = this.option.getDisplayName().copy();

        double value = this.customizations.get().get(this.option);
        Component valueComp = Component.literal(": " + String.format("%.1f", value));

        this.setMessage(message.append(valueComp));
    }

    @Override
    protected void applyValue() {
        double newVal = (this.value * (this.option.getMax() - this.option.getMin())) + this.option.getMin();
        this.customizations.get().set(this.option, newVal);
        this.onValueUpdate.run();
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(gui, mouseX, mouseY, partialTicks);
        if (this.isHovered) this.displayTooltip(Minecraft.getInstance().font, gui, mouseX, mouseY);
    }

    @Override
    public Component getTooltip() {
        return this.tooltipOverride == null
                ? this.option.getDescription()
                : this.tooltipOverride;
    }

    @Override
    public void setTooltipOverride(Component tooltipText) {
        this.tooltipOverride = tooltipText;
    }

    private static double convertDouble(CustomizationVolume volume, DoubleProperty optionString) {
        double d = volume.get(optionString);
        return Math.floor(d * 10 + 0.5) / 10;
    }
}
