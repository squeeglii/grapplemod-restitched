package com.yyon.grapplinghook.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.client.gui.GrappleModifierBlockGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.network.chat.Component;

public class CustomizationCheckbox extends Checkbox implements CustomTooltipHandler {

    private final GrappleModifierBlockGUI context;

    private final String option;
    private Component tooltipText;

    public CustomizationCheckbox(GrappleModifierBlockGUI context, int x, int y, int w, int h, Component text, boolean val, String option, Component tooltip) {
        super(x, y, w, h, text, val);
        this.context = context;
        this.option = option;
        this.tooltipText = tooltip;
    }

    @Override
    public void onPress() {
        super.onPress();

        this.context.getCurrentCustomizations().setBoolean(option, this.selected());

        this.context.markConfigurationsDirty();
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
