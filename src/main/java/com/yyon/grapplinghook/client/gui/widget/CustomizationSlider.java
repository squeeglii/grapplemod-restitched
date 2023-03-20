package com.yyon.grapplinghook.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.client.gui.GrappleModifierBlockGUI;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class CustomizationSlider extends AbstractSliderButton implements CustomTooltipHandler {
    private final double min, max;
    private final String text, option;

    private Component tooltipText;
    private double val;

    private final Screen context;
    private final Supplier<CustomizationVolume> customizations;
    private final Runnable onValueUpdate;

    public CustomizationSlider(Screen context, Supplier<CustomizationVolume> customizations, int x, int y, int w, int h, Component text, double min, double max, double val, String option, Component tooltip, Runnable onValueUpdate) {
        super(x, y, w, h, text, (val - min) / (max - min));
        this.context = context;

        this.min = min;
        this.max = max;
        this.val = val;
        this.text = text.getString();
        this.option = option;
        this.tooltipText = tooltip;
        this.customizations = customizations;
        this.onValueUpdate = onValueUpdate;

        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.literal(text + ": " + String.format("%.1f", this.val)));
    }

    @Override
    protected void applyValue() {
        this.val = (this.value * (this.max - this.min)) + this.min;
        this.customizations.get().setDouble(option, this.val);
        this.onValueUpdate.run();
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(gui, mouseX, mouseY, partialTicks);
        if (this.isHovered) this.displayTooltip(Minecraft.getInstance().font, gui, mouseX, mouseY);
    }

    public Component getTooltipText() {
        return this.tooltipText;
    }

    public void setTooltip(Component tooltipText) {
        this.tooltipText = tooltipText;
    }
}
