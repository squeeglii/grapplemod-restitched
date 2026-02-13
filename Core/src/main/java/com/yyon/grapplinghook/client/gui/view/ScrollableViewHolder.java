package com.yyon.grapplinghook.client.gui.view;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

// Modelled off of AbstractSelectionList
public class ScrollableViewHolder<T extends SwitchableScreenView> extends AbstractContainerWidget {

    private final Supplier<T> defaultView;

    private T currentView;
    private List<AbstractWidget> currentChildren;
    private int currentContentsHeight;
    private double scrollFrac;

    private AbstractWidget hovered = null;
    private AbstractWidget selected = null;

    public ScrollableViewHolder(Minecraft client, int x, int y, int width, int height, Supplier<T> defaultView) {
        super(x, y, width, height, Component.empty());

        this.defaultView = defaultView;
        this.currentView = defaultView.get();
        this.currentView.create();
        this.currentChildren = this.currentView.children();
    }

    public void switchView(T newView) {
        T newViewOrDefault = newView == null
                ? this.defaultView.get()
                : newView;

        List<AbstractWidget> prevWidgets = this.currentView.children();
        this.currentView.destroy(prevWidgets);
        this.currentChildren.clear();

        this.currentView = newViewOrDefault;
        this.currentContentsHeight = this.currentView.create();  // reset nav and height.
        this.scrollFrac = 0.0d;
        this.currentChildren = this.currentView.children();

        this.hovered = null;
        this.selected = null;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    @NotNull
    public List<? extends GuiEventListener> children() {
        return this.currentChildren;
    }



}
