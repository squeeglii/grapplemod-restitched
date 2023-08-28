package com.yyon.grapplinghook.client.gui.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

public class TextWidget extends AbstractWidget {

    private ChatFormatting[] formatting;

    public TextWidget(int posX, int posY, int sizeY, int sizeX, Component text, ChatFormatting... globalFormatting) {
        super(posX, posY, sizeY, sizeX, text);
        this.formatting = globalFormatting;
    }

    public TextWidget(int x, int y, Component text, ChatFormatting... globalFormatting) {
        this(x, y, 50, 15 * text.getString().split("\n").length + 5, text);
        this.formatting = globalFormatting;
    }

    @Override
    public void renderWidget(GuiGraphics gui, int mouseX, int mouseY, float partialTick) {
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
            gui.drawString(
                    fontRenderer, Component.literal(s).withStyle(formatting),
                    this.getX(), this.getY() + lineno * 15,
                    colour | Mth.ceil(this.alpha * 255.0F) << 24
            );

            lineno++;
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput) { }
}
