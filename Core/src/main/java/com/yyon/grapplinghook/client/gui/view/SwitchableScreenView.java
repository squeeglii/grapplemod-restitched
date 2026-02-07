package com.yyon.grapplinghook.client.gui.view;

import net.minecraft.client.gui.components.AbstractWidget;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class SwitchableScreenView {

    private final List<AbstractWidget> widgets;

    public SwitchableScreenView() {
        this.widgets = new LinkedList<>();
    }

    protected void addWidget(AbstractWidget widget) {
        this.widgets.add(widget);
    }

    // add widgets
    public abstract void create();

    //destroy those widgets
    public abstract void destroy(List<AbstractWidget> widgets);

    public abstract int getHeight();

    public final List<AbstractWidget> getWidgets() {
        return Collections.unmodifiableList(this.widgets);
    }

    public static SwitchableScreenView newBlankView() {
        return new BlankView();
    }


    public static class BlankView extends SwitchableScreenView {

        @Override
        public void create() { }

        @Override
        public void destroy(List<AbstractWidget> widgets) { }

        @Override
        public int getHeight() {
            return 0;
        }
    }

}
