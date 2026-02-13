package com.yyon.grapplinghook.client.gui.screen.blueprint;


import com.yyon.grapplinghook.client.gui.screen.ModificationTableMenuScreen;
import com.yyon.grapplinghook.content.customization.CustomizationCategory;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

public class CategoryEditView extends AbstractBlueprintView {

    private final ModificationTableMenuScreen parent;

    public CategoryEditView(ModificationTableMenuScreen parent, CustomizationCategory category, int width) {
        super(CurrentModifierView.CATEGORY_EDIT);
        this.parent = parent;
    }

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
