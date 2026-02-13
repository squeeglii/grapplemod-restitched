package com.yyon.grapplinghook.client.gui.screen.blueprint;

import com.yyon.grapplinghook.client.gui.screen.ModificationTableMenuScreen;
import com.yyon.grapplinghook.content.customization.CustomizationCategory;
import net.minecraft.client.gui.components.AbstractWidget;

import java.util.List;

public class CategoryLockedView extends AbstractBlueprintView {

    public CategoryLockedView(ModificationTableMenuScreen parent, CustomizationCategory category, int width) {
        super(CurrentModifierView.CATEGORY_LOCKED);
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
