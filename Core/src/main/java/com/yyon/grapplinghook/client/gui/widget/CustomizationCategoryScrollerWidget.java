package com.yyon.grapplinghook.client.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public class CustomizationCategoryScrollerWidget extends AbstractWidget {

    //TODO: Aggregate button elements.
    // Make it scrollable

    public CustomizationCategoryScrollerWidget(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int j, float f) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
