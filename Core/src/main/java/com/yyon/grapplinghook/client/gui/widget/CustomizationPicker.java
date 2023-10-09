package com.yyon.grapplinghook.client.gui.widget;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.EnumProperty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.function.Supplier;

public class CustomizationPicker<E extends Enum<E>> extends AbstractButton implements CustomTooltipHandler {

    private final EnumProperty<E> option;
    private final Supplier<CustomizationVolume> customizations;

    private final Runnable onValueUpdated;
    private Component tooltipOverride;

    public CustomizationPicker(Supplier<CustomizationVolume> customizations, int x, int y, int width, int height, EnumProperty<E> option, Runnable onValueUpdate) {
        super(x, y, width / 2, height, currentValue(customizations.get(), option));

        this.customizations = customizations;
        this.option = option;
        this.onValueUpdated = onValueUpdate;

        this.tooltipOverride = null;
    }

    @Override
    public void onPress() {
        CustomizationVolume volume = this.customizations.get();
        E currentValue = volume.get(this.option);
        E[] validValues = this.option.getOrdinalReversal();

        int nextValueOrdinal = (currentValue.ordinal() + 1) % validValues.length;
        E nextValue = validValues[nextValueOrdinal];
        Component nextValueLabel = this.option.getDisplay().getValueTranslationKey(nextValue);

        volume.set(this.option, nextValue);
        this.setMessage(nextValueLabel);
        this.onValueUpdated.run();
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTicks) {
        super.renderWidget(gui, mouseX, mouseY, partialTicks);
        gui.drawString(
                Minecraft.getInstance().font,
                this.option.getDisplayName(),
                this.getX() + this.getWidth() + 4,
                this.getY() + (this.height - 8) / 2,
                14737632 | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public static <E extends Enum<E>> Component currentValue(CustomizationVolume volume, EnumProperty<E> property) {
        E value = volume.get(property);
        return property.getDisplay().getValueTranslationKey(value);
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
