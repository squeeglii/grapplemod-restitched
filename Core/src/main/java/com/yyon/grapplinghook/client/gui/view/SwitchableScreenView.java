package com.yyon.grapplinghook.client.gui.view;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.narration.NarratableEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class SwitchableScreenView extends ContainerObjectSelectionList.Entry<SwitchableScreenView> {

    private final List<AbstractWidget> widgets;

    public SwitchableScreenView() {
        this.widgets = new LinkedList<>();
    }

    protected void addWidget(AbstractWidget widget) {
        this.widgets.add(widget);
    }

    // todo: make the creation process more sensible. Stop calls to addWidget after create.
    // add widgets
    /** @return total height of contents */
    public abstract int create();

    //destroy those widgets if necessary, idk. They get removed anyway.
    public abstract void destroy(List<AbstractWidget> widgets);

    @Override
    public void render(GuiGraphics guiGraphics, int index, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovering, float partialTick) {

    }

    @NotNull
    @Override
    public final List<AbstractWidget> children() {
        return Collections.unmodifiableList(this.widgets);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return Collections.unmodifiableList(this.widgets);
    }


    public static SwitchableScreenView newBlankView() {
        return new BlankView();
    }

}
