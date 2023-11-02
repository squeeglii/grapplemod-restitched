package com.yyon.grapplinghook.client.gui.widget;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.DoubleProperty;
import com.yyon.grapplinghook.customization.type.IntegerProperty;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class SteppedCustomizationSlider extends AbstractSliderButton implements CustomTooltipHandler {

    private final IntegerProperty option;
    private final Supplier<CustomizationVolume> customizations;
    private final Runnable onValueUpdate;

    private Component tooltipOverride;

    public SteppedCustomizationSlider(Supplier<CustomizationVolume> customizations, int x, int y, int w, int h, IntegerProperty option, Runnable onValueUpdate) {
        super(x, y, w, h, option.getDisplayName(), SteppedCustomizationSlider.scaleFromVolume(customizations.get(), option));

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
        int ceil = this.option.getMax() - this.option.getMin();
        int newVal = this.option.getMin() + (int) Math.floor(this.value * ceil);

        this.customizations.get().set(this.option, newVal);
        this.onValueUpdate.run();
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(gui, mouseX, mouseY, partialTicks);
    }

    @Override
    public Component getTooltipText() {
        return this.tooltipOverride == null
                ? this.option.getDescription()
                : this.tooltipOverride;
    }

    @Override
    public void setTooltipOverride(Component tooltipText) {
        this.tooltipOverride = tooltipText;
        this.setTooltip(Tooltip.create(this.getTooltipText()));
    }

    private static double scaleFromVolume(CustomizationVolume volume, IntegerProperty option) {
        int value = volume.get(option);

        int height = value - option.getMin();
        int ceil = option.getMax() - option.getMin();

        return (float) height / ceil;
    }
}
