package com.yyon.grapplinghook.mixin.client;

import com.yyon.grapplinghook.client.ClientPhysicsContextTracker;
import com.yyon.grapplinghook.physics.context.AirFrictionPhysicsContext;
import com.yyon.grapplinghook.physics.context.ForcefieldPhysicsContext;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsContext;
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

        int id = player.getId();
        if (ClientPhysicsContextTracker.controllers.containsKey(id)) {
            Input input = this.input;
            GrapplingHookPhysicsContext control = ClientPhysicsContextTracker.controllers.get(id);
            control.receivePlayerMovementMessage(input.leftImpulse, input.forwardImpulse, input.jumping, input.shiftKeyDown);

            boolean overrideMovement = true;
            if (Minecraft.getInstance().player.onGround()) {
                if (!(control instanceof AirFrictionPhysicsContext) && !(control instanceof ForcefieldPhysicsContext)) {
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
//				input.sneak = false; // fix alternate throw angles
            }
        }
    }
}
