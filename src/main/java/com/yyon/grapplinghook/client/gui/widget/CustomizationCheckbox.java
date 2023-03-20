package com.yyon.grapplinghook.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.client.gui.GrappleModifierBlockGUI;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class CustomizationCheckbox extends Checkbox implements CustomTooltipHandler {

    private final Screen context;
    private final Supplier<CustomizationVolume> customizations;

    private final String option;
    private Component tooltipText;
    private Runnable onValueUpdated;

    public CustomizationCheckbox(Screen context, Supplier<CustomizationVolume> customizations, int x, int y, int w, int h, Component text, boolean val, String option, Component tooltip, Runnable onValueUpdate) {
        super(x, y, w, h, text, val);
        this.context = context;
        this.customizations = customizations;
        this.option = option;
        this.tooltipText = tooltip;
        this.onValueUpdated = onValueUpdate;
    }

    @Override
    public void onPress() {
        super.onPress();

        this.customizations.get().setBoolean(option, this.selected());
        this.onValueUpdated.run();
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
