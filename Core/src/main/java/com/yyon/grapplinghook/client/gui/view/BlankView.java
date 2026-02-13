package com.yyon.grapplinghook.client.gui.view;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

public class BlankView extends SwitchableScreenView {

    @Override
    public void create() {
    }

    @Override
    public void destroy(List<AbstractWidget> widgets) {
    }

    @Override
    public int getContentsHeight() {
        return 0;
    }
}
