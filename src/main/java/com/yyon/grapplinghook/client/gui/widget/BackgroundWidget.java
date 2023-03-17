package com.yyon.grapplinghook.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BackgroundWidget extends AbstractWidget {

    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(
            GrappleMod.MODID,
            "textures/gui/guimodifier_bg.png"
    );

    public BackgroundWidget(int posX, int posY, int sizeVertical, int sizeHorizontal, Component text) {
        super(posX, posY, sizeVertical, sizeHorizontal, text);
        this.active = false;
    }

    public BackgroundWidget(int x, int y, int w, int h) {
        this(x, y, w, h, Component.literal(""));
    }

    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, BG_TEXTURE);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        GuiComponent.blit(stack, this.getX(), this.getY(), 0, 0, this.width, this.height);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) { }
}
