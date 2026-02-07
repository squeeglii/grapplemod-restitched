package com.yyon.grapplinghook.client.gui.view;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Supplier;

public class ScrollableViewHolder<T extends SwitchableScreenView> extends AbstractScrollWidget {

    private final Supplier<T> defaultView;
    private T currentView;

    public ScrollableViewHolder(int x, int y, int width, int height, Supplier<T> defaultView) {
        super(x, y, width, height, Component.empty());

        this.defaultView = defaultView;
        this.currentView = defaultView.get();
        this.currentView.create();
    }

    public void switchView(T newView) {
        T view = newView == null
                ? this.defaultView.get()
                : newView;

        List<AbstractWidget> widgets = this.currentView.getWidgets();

        this.currentView.destroy(widgets);
        this.currentView = view;
        this.currentView.create();
    }

    @Override
    protected int getInnerHeight() {
        return this.currentView.getHeight();
    }

    @Override
    protected double scrollRate() {
        return 2.0f;
    }

    @Override
    protected void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.currentView.getWidgets().forEach(widget -> widget.render(guiGraphics, mouseX, mouseY, partialTick));
    }

    @Override
    protected void renderBackground(GuiGraphics guiGraphics) {
        //super.renderBackground(guiGraphics);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
