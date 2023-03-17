package com.yyon.grapplinghook.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yyon.grapplinghook.client.ClientPhysicsContextTracker;
import com.yyon.grapplinghook.config.GrappleModConfig;
import com.yyon.grapplinghook.physics.context.AirFrictionPhysicsContext;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsContext;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class CameraSetupHookMixin {

    @Final
    @Shadow
    private Camera mainCamera;

    protected float currentCameraTilt = 0;
    @Inject(method = "renderLevel(FJLcom/mojang/blaze3d/vertex/PoseStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Camera;setup(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/world/entity/Entity;ZZF)V",
                    shift = At.Shift.AFTER
            ))
    public void postCameraSetup(float partialTicks, long finishTimeNano, PoseStack matrixStack, CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (!Minecraft.getInstance().isRunning() || player == null) {
            return;
        }

        int id = player.getId();
        int targetCameraTilt = 0;
        if (ClientPhysicsContextTracker.controllers.containsKey(id)) {
            GrapplingHookPhysicsContext controller = ClientPhysicsContextTracker.controllers.get(id);
            if (controller instanceof AirFrictionPhysicsContext afcontroller) {
                if (afcontroller.wasWallrunning) {
                    Vec walldirection = afcontroller.getWallDirection();
                    if (walldirection != null) {
                        Vec lookdirection = Vec.lookVec(player);
                        int dir = lookdirection.cross(walldirection).y > 0 ? 1 : -1;
                        targetCameraTilt = dir;
                    }
                }
            }
        }

        if (currentCameraTilt != targetCameraTilt) {
            float cameraDiff = targetCameraTilt - currentCameraTilt;
            if (cameraDiff != 0) {
                float anim_s = GrappleModConfig.getClientConf().camera.wallrun_camera_animation_s;
                float speed = (anim_s == 0) ? 9999 :  1.0f / (anim_s * 20.0f);
                if (speed > Math.abs(cameraDiff)) {
                    currentCameraTilt = targetCameraTilt;
                } else {
                    currentCameraTilt += speed * (cameraDiff > 0 ? 1 : -1);
                }
            }
        }

        if (currentCameraTilt != 0) {
            // Observing the forge hook, roll just isn't used.
            // Fix this.
            //mainCamera.(0 + currentCameraTilt*GrappleConfig.getClientConf().camera.wallrun_camera_tilt_degrees);
        }
    }

}
