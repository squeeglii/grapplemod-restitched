package com.yyon.grapplinghook.mixin.client;

import com.yyon.grapplinghook.client.physics.ClientPhysicsControllerTracker;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.physics.context.AirFrictionPhysicsController;
import com.yyon.grapplinghook.client.physics.context.ForcefieldPhysicsController;
import com.yyon.grapplinghook.client.physics.context.GrapplingHookPhysicsController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.Input;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public class MovementInputHandlerMixin {

    @Shadow
    public Input input;

    @Inject(method = "aiStep()V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/Input;tick(ZF)V", shift = At.Shift.AFTER))
    public void inputHandle(CallbackInfo ci) {
        Player player = Minecraft.getInstance().player;
        if (!Minecraft.getInstance().isRunning() || player == null) return;

        ClientPhysicsControllerTracker physManager = GrappleModClient.get().getClientControllerManager();

        int id = player.getId();
        if (!physManager.controllers.containsKey(id))
            return;

        Input input = this.input;
        GrapplingHookPhysicsController control = physManager.controllers.get(id);
        control.receivePlayerMovementMessage(input.leftImpulse, input.forwardImpulse, input.shiftKeyDown);

        boolean overrideMovement = true;
        if (player.onGround()) {
            if (!(control instanceof AirFrictionPhysicsController) && !(control instanceof ForcefieldPhysicsController)) {
                overrideMovement = false;
            }
        }

        if (overrideMovement) {
            input.jumping = false;
            input.down = false;
            input.up = false;
            input.left = false;
            input.right = false;
            input.forwardImpulse = 0;
            input.leftImpulse = 0;
			//input.sneak = false; // fix alternate throw angles
        }
    }
}
