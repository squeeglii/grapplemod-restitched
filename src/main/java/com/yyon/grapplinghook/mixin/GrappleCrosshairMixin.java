package com.yyon.grapplinghook.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.yyon.grapplinghook.client.ClientControllerManager;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.item.GrapplehookItem;
import com.yyon.grapplinghook.util.GrappleCustomization;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GrappleCrosshairMixin {

    private static final double Z_LEVEL = -90.0D;

    @Final @Shadow
    private Minecraft minecraft;

    @Inject(method = "renderCrosshair(Lcom/mojang/blaze3d/vertex/PoseStack;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;blit(Lcom/mojang/blaze3d/vertex/PoseStack;IIIIII)V", shift = At.Shift.AFTER, ordinal = 0))
    public void renderModCrosshair(PoseStack matrices, CallbackInfo ci) {

        LocalPlayer player = this.minecraft.player;
        ItemStack grapplehookItemStack = null;

        if (player == null) throw new IllegalStateException("Player should not be null when rendering crosshair");

        if (player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof GrapplehookItem) {
            grapplehookItemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        } else if (player.getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof GrapplehookItem) {
            grapplehookItemStack = player.getItemInHand(InteractionHand.OFF_HAND);
        }

        if (grapplehookItemStack != null) {
            GrappleCustomization custom = ((GrapplehookItem) CommonSetup.grapplingHookItem.get()).getCustomization(grapplehookItemStack);
            double angle = Math.toRadians(custom.angle);
            double verticalAngle = Math.toRadians(custom.verticalthrowangle);

            if (player.isCrouching()) {
                angle = Math.toRadians(custom.sneakingangle);
                verticalAngle = Math.toRadians(custom.sneakingverticalthrowangle);
            }

            if (!custom.doublehook) angle = 0;

            Window resolution = this.minecraft.getWindow();
            int w = resolution.getGuiScaledWidth();
            int h = resolution.getGuiScaledHeight();

            double fov = Math.toRadians(this.minecraft.options.fov().get());
            fov *= player.getFieldOfViewModifier();
            double l = ((double) h/2) / Math.tan(fov/2);

            if (!((verticalAngle == 0) && (!custom.doublehook || angle == 0))) {
                int offset = (int) (Math.tan(angle) * l);
                int verticalOffset = (int) (-Math.tan(verticalAngle) * l);

                this.drawCrosshair(matrices, w / 2 + offset, h / 2 + verticalOffset);
                if (angle != 0) {
                    this.drawCrosshair(matrices, w / 2 - offset, h / 2 + verticalOffset);
                }
            }

            if (custom.rocket && custom.rocket_vertical_angle != 0) {
                int verticalOffset = (int) (-Math.tan(Math.toRadians(custom.rocket_vertical_angle)) * l);
                this.drawCrosshair(matrices, w / 2, h / 2 + verticalOffset);
            }
        }

        double rocketFuel = ClientControllerManager.instance.rocketFuel;

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


    private void drawCrosshair(PoseStack mStack, int x, int y) {
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        Minecraft.getInstance().gui.blit(mStack, (int) (x - (15.0F/2)), (int) (y - (15.0F/2)), 0, 0, 15, 15);
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
