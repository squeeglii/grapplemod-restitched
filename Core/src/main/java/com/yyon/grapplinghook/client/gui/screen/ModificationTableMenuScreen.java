package com.yyon.grapplinghook.client.gui.screen;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.gui.screen.blueprint.AbstractBlueprintView;
import com.yyon.grapplinghook.client.gui.screen.blueprint.CurrentModifierView;
import com.yyon.grapplinghook.client.gui.menu.ModificationTableMenu;
import com.yyon.grapplinghook.client.gui.screen.blueprint.HookOverviewView;
import com.yyon.grapplinghook.client.gui.view.ScrollableViewHolder;
import com.yyon.grapplinghook.client.gui.view.SwitchableScreenView;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ModificationTableMenuScreen extends AbstractContainerScreen<ModificationTableMenu> {

    // Constants
    public static final ResourceLocation TEX_BACKGROUND = GrappleMod.id("textures/gui/container/modification_table_mangrove.png");

    public static final int VANILLA_TEXT_COLOUR = 4210752;
    public static final int TEXT_COLOUR = 0xFFEEEEDD; // ARGB

    // Layout
    private ScrollableViewHolder<AbstractBlueprintView> blueprintViewHolder;

    public ModificationTableMenuScreen(ModificationTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title); // todo: skip title rendering - I have not designed it well for that.

        GrappleMod.LOGGER.info("Creating ModificationTable screen.");

        this.imageWidth = ModificationTableMenu.MENU_SIZE.x();
        this.imageHeight = ModificationTableMenu.MENU_SIZE.y();
    }

    // todo: when customization is changed by player, ensure menu.saveState() is run.

    @Override
    protected void init() {
        super.init();

        this.inventoryLabelX = ModificationTableMenu.INVENTORY_TOP_LEFT.x();
        this.inventoryLabelY = ModificationTableMenu.INVENTORY_TOP_LEFT.y() - (this.font.lineHeight + 3);

        this.blueprintViewHolder = new ScrollableViewHolder<>(
                this::addRenderableWidget,
                this::removeWidget,
                this.leftPos + ModificationTableMenu.BLUEPRINT_SCROLLABLE_TOP_LEFT.x(),
                this.topPos + ModificationTableMenu.BLUEPRINT_SCROLLABLE_TOP_LEFT.y(),
                ModificationTableMenu.BLUEPRINT_SCROLLABLE_SIZE.x(),
                ModificationTableMenu.BLUEPRINT_SCROLLABLE_SIZE.y(),
                () -> new HookOverviewView(this, ModificationTableMenu.BLUEPRINT_SCROLLABLE_SIZE.x())
        );

        this.addRenderableWidget(this.blueprintViewHolder);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEX_BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 512, 256);
        //todo: render all
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        //guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, VANILLA_TEXT_COLOUR, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, TEXT_COLOUR, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    public ScrollableViewHolder<AbstractBlueprintView> getBlueprint() {
        return this.blueprintViewHolder;
    }
}
