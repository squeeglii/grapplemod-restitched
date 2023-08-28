package com.yyon.grapplinghook.client.gui.widget;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.client.GameNarrator;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BackgroundWidget extends AbstractWidget {

    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(
            GrappleMod.MOD_ID,
            "textures/gui/guimodifier_bg.png"
    );

    public BackgroundWidget(int posX, int posY, int sizeVertical, int sizeHorizontal, Component text) {
        super(posX, posY, sizeVertical, sizeHorizontal, text);
        this.active = false;
    }

    public BackgroundWidget(int x, int y, int w, int h) {
        this(x, y, w, h, GameNarrator.NO_TITLE);
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 9 slice:  slice-border, size-x, size-y, start-x, start-y
        graphics.blitNineSliced(BG_TEXTURE, this.getX(), this.getY(), this.width, this.height, 10, 37, 37, 0, 0);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, this.createNarrationMessage());
    }
}
