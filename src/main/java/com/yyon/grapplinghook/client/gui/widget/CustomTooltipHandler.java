package com.yyon.grapplinghook.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Optional;

public interface CustomTooltipHandler {

    default void displayTooltip(Font font, GuiGraphics gui, int mouseX, int mouseY) {
        String tooltipText = this.getTooltipText().getString();
        ArrayList<Component> lines = new ArrayList<>();
        for (String line : tooltipText.split("\n")) {
            lines.add(Component.literal(line.trim()));
        }

        gui.renderTooltip(font, lines, Optional.empty(), mouseX, mouseY);
    }

    Component getTooltipText();

    void setTooltipOverride(Component tooltipText);

    default void resetTooltipOverride() {
        this.setTooltipOverride(null);
    }
}
