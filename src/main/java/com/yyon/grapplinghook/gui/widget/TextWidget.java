package com.yyon.grapplinghook.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TextWidget extends AbstractWidget {

    public TextWidget(int posX, int posY, int sizeVertical, int sizeHorizontal, Component text) {
        super(posX, posY, sizeVertical, sizeHorizontal, text);
    }

    public TextWidget(Component text, int x, int y) {
        this(x, y, 50, 15 * text.getString().split("\n").length + 5, text);
    }

    public void renderWidget(PoseStack stack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        Font fontRenderer = minecraft.font;
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        int colour = this.active ? 16777215 : 10526880; // Drop-in for forge's this.getFGColor()
        int lineno = 0;
        for (String s : this.getMessage().getString().split("\n")) {
            drawString(stack, fontRenderer, Component.literal(s), this.getX(), this.getY() + lineno*15, colour | Mth.ceil(this.alpha * 255.0F) << 24);
            lineno++;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) { }
}
