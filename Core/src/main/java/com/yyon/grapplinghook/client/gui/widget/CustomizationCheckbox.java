package com.yyon.grapplinghook.client.gui.widget;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.BooleanProperty;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class CustomizationCheckbox extends Checkbox implements CustomTooltipHandler {

    private final BooleanProperty option;
    private final Supplier<CustomizationVolume> customizations;

    private final Runnable onValueUpdated;
    private Component tooltipOverride;

    public CustomizationCheckbox(Supplier<CustomizationVolume> customizations, int x, int y, int w, int h, BooleanProperty option, Runnable onValueUpdate) {
        super(x, y, w, h, option.getDisplayName(), customizations.get().get(option));

        this.customizations = customizations;
        this.option = option;
        this.onValueUpdated = onValueUpdate;

        this.tooltipOverride = null;
    }

    @Override
    public void onPress() {
        super.onPress();

        this.customizations.get().set(this.option, this.selected());
        this.onValueUpdated.run();
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
}
