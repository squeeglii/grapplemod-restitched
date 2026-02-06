package com.yyon.grapplinghook.client.gui.screen;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.gui.ModifierGUILayoutView;
import com.yyon.grapplinghook.client.gui.menu.ModificationTableMenu;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import net.fabricmc.fabric.impl.client.indigo.renderer.helper.ColorHelper;
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


    // Context
    // this.menu

    // State
    private ModifierGUILayoutView currentMainContentView;

    // Tweaks


    public ModificationTableMenuScreen(ModificationTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title); // todo: skip title rendering - I have not designed it well for that.

        menu.onAnySlotChange(this::updateFromSlots);

        GrappleMod.LOGGER.info("Creating ModificationTable screen.");

        this.imageWidth = ModificationTableMenu.MENU_SIZE.x();
        this.imageHeight = ModificationTableMenu.MENU_SIZE.y();
    }

    @Override
    protected void init() {
        super.init();

        this.inventoryLabelX = ModificationTableMenu.INVENTORY_TOP_LEFT.x();
        this.inventoryLabelY = ModificationTableMenu.INVENTORY_TOP_LEFT.y() - (this.font.lineHeight + 3);
    }

    public void updateFromSlots() {

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEX_BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 512, 256);

        //todo:  render all
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        //guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, VANILLA_TEXT_COLOUR, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, TEXT_COLOUR, false);
    }
}
