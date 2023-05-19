package com.yyon.grapplinghook.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.client.ClientControllerManager;
import com.yyon.grapplinghook.config.GrappleConfig;
import com.yyon.grapplinghook.controller.AirfrictionController;
import com.yyon.grapplinghook.controller.GrappleController;
import com.mojang.math.Vector3f;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class CameraSetupHookMixin {

    private float currentCameraTilt = 0;

    @Inject(method = "renderLevel(FJLcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
                    shift = At.Shift.AFTER
            ))
    public void postCameraSetup(float partialTicks, long finishTimeNano, PoseStack matrixStack, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (!Minecraft.getInstance().isRunning() || player == null) return;

        int id = player.getId();
        int targetCameraTilt = 0;

        if (ClientControllerManager.controllers.containsKey(id)) {
            GrappleController controller = ClientControllerManager.controllers.get(id);

            if (controller instanceof AirfrictionController physicsContext && physicsContext.wasWallrunning) {

                Vec wallDirection = physicsContext.getWallDirection();
                if (wallDirection != null) {
                    Vec lookDirection = Vec.lookVec(player);
                    targetCameraTilt = lookDirection.cross(wallDirection).y > 0 ? 1 : -1;
                }
            }
        }

        if (currentCameraTilt != targetCameraTilt) {
            float cameraDiff = targetCameraTilt - currentCameraTilt;
            if (cameraDiff != 0) {
                float anim_s = GrappleConfig.getClientConf().camera.wallrun_camera_animation_s;
                float speed = (anim_s == 0)
                        ? 9999
                        :  1.0f / (anim_s * 20.0f);

                currentCameraTilt = speed > Math.abs(cameraDiff)
                        ? targetCameraTilt
                        : currentCameraTilt + speed * (cameraDiff > 0 ? 1 : -1);
            }
        }

        if (this.currentCameraTilt == 0) return;

        float angle = this.currentCameraTilt * GrappleConfig.getClientConf().camera.wallrun_camera_tilt_degrees;
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(angle));
    }

}
