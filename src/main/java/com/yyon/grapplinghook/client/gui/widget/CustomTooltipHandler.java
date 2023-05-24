package com.yyon.grapplinghook.client.gui.widget;

import net.minecraft.network.chat.Component;

public interface CustomTooltipHandler {

    Component getTooltipText();

    void setTooltipOverride(Component tooltipText);

    default void resetTooltipOverride() {
        this.setTooltipOverride(null);
    }
}
