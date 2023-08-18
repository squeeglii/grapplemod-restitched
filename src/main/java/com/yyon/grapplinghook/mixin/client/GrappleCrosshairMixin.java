package com.yyon.grapplinghook.mixin.client;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.yyon.grapplinghook.client.ClientPhysicsContextTracker;
import com.yyon.grapplinghook.content.item.GrapplehookItem;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.*;

@Mixin(Gui.class)
public abstract class GrappleCrosshairMixin {

    private static final double Z_LEVEL = -90.0D;

    @Final @Shadow
    private Minecraft minecraft;

    @Final @Shadow
    private static ResourceLocation GUI_ICONS_LOCATION;

    @Inject(method = "renderCrosshair(Lnet/minecraft/client/gui/GuiGraphics;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;blit(Lnet/minecraft/resources/ResourceLocation;IIIIII)V", shift = At.Shift.AFTER, ordinal = 0))
    public void renderModCrosshair(GuiGraphics guiGraphics, CallbackInfo ci) {

        LocalPlayer player = this.minecraft.player;
        ItemStack grapplehookItemStack = null;

        if (player == null) throw new IllegalStateException("Player should not be null when rendering crosshair");

        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GrapplehookItem) {
            grapplehookItemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        } else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof GrapplehookItem) {
            grapplehookItemStack = player.getItemInHand(InteractionHand.OFF_HAND);
        }

        if (grapplehookItemStack != null) {
            CustomizationVolume custom = GrappleModItems.GRAPPLING_HOOK.get().getCustomization(grapplehookItemStack);
            double angle = Math.toRadians(custom.get(DOUBLE_HOOK_ANGLE.get()));
            double verticalAngle = Math.toRadians(custom.get(HOOK_THROW_ANGLE.get()));

            if (player.isCrouching()) {
                angle = Math.toRadians(custom.get(DOUBLE_HOOK_ANGLE_ON_SNEAK.get()));
                verticalAngle = Math.toRadians(custom.get(HOOK_THROW_ANGLE_ON_SNEAK.get()));
            }

            if (!custom.get(DOUBLE_HOOK_ATTACHED.get()))
                angle = 0;

            Window resolution = this.minecraft.getWindow();
            int w = resolution.getGuiScaledWidth();
            int h = resolution.getGuiScaledHeight();

            double fov = Math.toRadians(this.minecraft.options.fov().get());
            fov *= player.getFieldOfViewModifier();
            double l = ((double) h/2) / Math.tan(fov/2);

            if (!((verticalAngle == 0) && (!custom.get(DOUBLE_HOOK_ATTACHED.get()) || angle == 0))) {
                int offset = (int) (Math.tan(angle) * l);
                int verticalOffset = (int) (-Math.tan(verticalAngle) * l);

                this.drawCrosshair(guiGraphics, w / 2 + offset, h / 2 + verticalOffset);
                if (angle != 0) {
                    this.drawCrosshair(guiGraphics, w / 2 - offset, h / 2 + verticalOffset);
                }
            }

            if (custom.get(ROCKET_ATTACHED.get()) && custom.get(ROCKET_ANGLE.get()) != 0) {
                int verticalOffset = (int) (-Math.tan(Math.toRadians(custom.get(ROCKET_ANGLE.get()))) * l);
                this.drawCrosshair(guiGraphics, w / 2, h / 2 + verticalOffset);
            }
        }

        double rocketFuel = ClientPhysicsContextTracker.instance.rocketFuel;

        if (rocketFuel < 1) {
            Window resolution = this.minecraft.getWindow();
            int w = resolution.getGuiScaledWidth();
            int h = resolution.getGuiScaledHeight();

            int totalbarLength = w / 8;

            RenderSystem.getModelViewStack().pushPose();

            this.drawRect(w / 2 - totalbarLength / 2, h * 3 / 4, totalbarLength, 2, 50, 100);
            this.drawRect(w / 2 - totalbarLength / 2, h * 3 / 4, (int) (totalbarLength * rocketFuel), 2, 200, 255);

            RenderSystem.getModelViewStack().popPose();
        }
    }


    private void drawCrosshair(GuiGraphics guiGraphics, int x, int y) {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        guiGraphics.blit(GUI_ICONS_LOCATION, (int) (x - (15.0F/2)), (int) (y - (15.0F/2)), 0, 0, 15, 15);
        RenderSystem.defaultBlendFunc();
    }

    public void drawRect(int x, int y, int width, int height, int g, int a)
    {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();

        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bufferbuilder.vertex(x, y + height, Z_LEVEL).color(g, g, g, a).endVertex();
        bufferbuilder.vertex(x + width, y + height, Z_LEVEL).color(g, g, g, a).endVertex();
        bufferbuilder.vertex(x + width, y, Z_LEVEL).color(g, g, g, a).endVertex();
        bufferbuilder.vertex(x, y, Z_LEVEL).color(g, g, g, a).endVertex();

        BufferUploader.drawWithShader(bufferbuilder.end());
    }
}
