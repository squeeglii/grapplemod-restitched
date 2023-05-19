package com.yyon.grapplinghook.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.client.gui.GrappleModifierBlockGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.Component;

public class CustomizationSlider extends AbstractSliderButton implements CustomTooltipHandler {
    private final double min, max;
    private final String text, option;

    private Component tooltipText;
    private double val;

    private final GrappleModifierBlockGUI context;

    public CustomizationSlider(GrappleModifierBlockGUI context, int x, int y, int w, int h, Component text, double min, double max, double val, String option, Component tooltip) {
        super(x, y, w, h, text, (val - min) / (max - min));
        this.context = context;

        this.min = min;
        this.max = max;
        this.val = val;
        this.text = text.getString();
        this.option = option;
        this.tooltipText = tooltip;

        this.updateMessage();
    }

    @Override
    protected void updateMessage() {
        this.setMessage(Component.literal(text + ": " + String.format("%.1f", this.val)));
    }

    @Override
    protected void applyValue() {
        this.val = (this.value * (this.max - this.min)) + this.min;
        this.context.getCurrentCustomizations().setDouble(option, this.val);
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
