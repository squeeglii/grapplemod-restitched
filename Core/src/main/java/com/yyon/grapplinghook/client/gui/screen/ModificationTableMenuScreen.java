package com.yyon.grapplinghook.client.gui.screen;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.gui.ModifierGUILayoutView;
import com.yyon.grapplinghook.client.gui.menu.ModificationTableMenu;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ModificationTableMenuScreen extends AbstractContainerScreen<ModificationTableMenu> {

    // Constants
    public static final ResourceLocation TEX_BACKGROUND = GrappleMod.id("textures/gui/container/modification_table.png")

    // Context
    private final GrappleModifierBlockEntity blockEntity;

    // State
    private ModifierGUILayoutView currentMainContentView;

    // Tweaks


    public ModificationTableMenuScreen(ModificationTableMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);

        this.blockEntity = blockEntity;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(TEX_BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        //todo:  render all
    }
}
