package com.yyon.grapplinghook.client.gui.widget;

import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.content.customization.type.BooleanProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

import java.util.function.Supplier;

public class CustomizationCheckbox extends Checkbox implements CustomTooltipHandler {

    private final BooleanProperty option;
    private final Supplier<HookCustomization> customizations;

    private final Runnable onValueUpdated;
    private Component tooltipOverride;

    public CustomizationCheckbox(Supplier<HookCustomization> customizations, int x, int y, BooleanProperty option, Runnable onValueUpdate) {
        super(x, y, 0, option.getDisplayName(), Minecraft.getInstance().font, customizations.get().get(option), (checkbox, bl) -> onValueUpdate.run());

        //todo: check maxWidth (0 in constructor ^) is correct here.

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
