package com.yyon.grapplinghook.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.yyon.grapplinghook.client.ClientPhysicsControllerTracker;
import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.physics.context.AirFrictionPhysicsController;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsController;
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

        if (ClientPhysicsControllerTracker.controllers.containsKey(id)) {
            GrapplingHookPhysicsController controller = ClientPhysicsControllerTracker.controllers.get(id);

            if (controller instanceof AirFrictionPhysicsController physicsContext && physicsContext.wasWallRunning()) {

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
                float anim_s = GrappleModLegacyConfig.getClientConf().camera.wallrun_camera_animation_s;
                float speed = (anim_s == 0)
                        ? 9999
                        :  1.0f / (anim_s * 20.0f);

                currentCameraTilt = speed > Math.abs(cameraDiff)
                        ? targetCameraTilt
                        : currentCameraTilt + speed * (cameraDiff > 0 ? 1 : -1);
            }
        }

        if (this.currentCameraTilt == 0) return;

        float angle = this.currentCameraTilt * GrappleModLegacyConfig.getClientConf().camera.wallrun_camera_tilt_degrees;
        matrixStack.mulPose(Axis.ZP.rotationDegrees(angle));
    }

}
