package com.yyon.grapplinghook.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.MultiLineTextWidget;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class FadingMultiLineTextWidget extends MultiLineTextWidget {

    private final int maxFadeTicks;
    private float ticksAlive;

    public FadingMultiLineTextWidget(int x, int y, Component component, int maxFadeTicks, Font font) {
        super(x, y, component, font);
        this.maxFadeTicks = maxFadeTicks;
        this.ticksAlive = 0.0f;
    }


    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.ticksAlive += partialTicks;

        float visibilityLifecycle = 1.3f - (this.ticksAlive / this.maxFadeTicks);
        float opacity = Mth.clamp(visibilityLifecycle, 0.0f, 1.0f);
        int fullOpacity = ((int) Math.floor(opacity * 255)) & 0xFF;

        // For some reason, an opacity of less than 5 gets displayed as if it's opacity is 255.
        // Just skip rendering instead.
        if(fullOpacity < 5)
            return;

        int initialColour = this.getColor();
        int rgbOnly = 0x00FFFFFF & initialColour;
        int composedColour = rgbOnly | (fullOpacity << 24);

        this.setColor(composedColour);
        super.renderWidget(guiGraphics, mouseX, mouseY, partialTicks);
        this.setColor(initialColour);
    }


    public int getMaxLifespan() {
        return this.maxFadeTicks;
    }
}
