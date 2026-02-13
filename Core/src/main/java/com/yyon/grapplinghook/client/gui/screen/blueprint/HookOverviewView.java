package com.yyon.grapplinghook.client.gui.screen.blueprint;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.gui.screen.ModificationTableMenuScreen;
import com.yyon.grapplinghook.content.customization.CustomizationCategory;
import com.yyon.grapplinghook.content.registry.GrappleModRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.network.chat.Component;

import java.util.List;

public class HookOverviewView extends AbstractBlueprintView {

    private final ModificationTableMenuScreen parent;

    private final int width;

    private final GridLayout layout;
    private final GridLayout.RowHelper layoutRows;

    public HookOverviewView(ModificationTableMenuScreen parent, int width) {
        super(CurrentModifierView.OVERVIEW);
        this.parent = parent;
        this.width = width;
        this.layout = new GridLayout(width, 0);
        this.layoutRows = this.layout.createRowHelper(2);

        GrappleMod.LOGGER.info("HookOverviewView created");
    }

    @Override
    public void create() {
        this.layout.rowSpacing(4);
        this.layout.columnSpacing(6);
        this.layout.newCellSettings().alignVerticallyTop().alignHorizontallyCenter();

        StringWidget title = new StringWidget(Component.translatable("grapple_modifier.overview.title").withStyle(ChatFormatting.WHITE, ChatFormatting.UNDERLINE), Minecraft.getInstance().font);

        this.layoutRows.addChild(title, 2);

        GrappleModRegistries.CUSTOMIZATION_CATEGORIES.stream().forEach(category -> {
            GrappleMod.LOGGER.info("Adding customization category {}", category.getName());
            if (!category.shouldRender()) return;

            this.layoutRows.addChild(
                    Button.builder(category.getName(), this.onCategorySelect(category))
                            .pos(0, 0)
                            .tooltip(Tooltip.create(category.getEmbedContent()))
                            .size(this.width - 6 / 2, 20)
                            .build()
            );
        });

        this.layout.arrangeElements();
        this.layout.visitWidgets(this::addWidget);

        GrappleMod.LOGGER.info("HookOverviewView Widgets: {}", this.getWidgets().toArray());
    }

    @Override
    public void destroy(List<AbstractWidget> widgets) {

    }

    private Button.OnPress onCategorySelect(CustomizationCategory category) {
        return button -> {
            GrappleMod.LOGGER.info("Press!!!");
            boolean unlocked = this.parent.getMenu().isUnlocked(category) ||
                               this.parent.getMenu().getPlayer().isCreative();

            if (unlocked) {
                CategoryEditView newView = new CategoryEditView(this.parent, category, this.width);
                this.parent.getBlueprint().switchView(newView);
                return;
            }

            CategoryLockedView newView = new CategoryLockedView(this.parent, category, this.width);
            this.parent.getBlueprint().switchView(newView);
        };
    }



    @Override
    public int getContentsHeight() {
        return this.layout.getHeight();
    }
}
