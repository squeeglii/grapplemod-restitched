package com.yyon.grapplinghook.client.gui.screen.blueprint;

import com.yyon.grapplinghook.client.gui.view.SwitchableScreenView;

public abstract class AbstractBlueprintView extends SwitchableScreenView {

    private final CurrentModifierView viewType;

    public AbstractBlueprintView(CurrentModifierView viewType) {
        this.viewType = viewType;
    }


    public final CurrentModifierView getViewType() {
        return this.viewType;
    }
}
