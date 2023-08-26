package com.yyon.grapplinghook.mixin.client;

import com.yyon.grapplinghook.client.ClientPhysicsControllerTracker;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.physics.context.AirFrictionPhysicsController;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsController;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class KeybindingHandlerMixin {


    @Inject(method = "keyPress(JIIII)V", at = @At("TAIL"))
    public void handleModKeybindings(long pWindowPointer, int pKey, int pScanCode, int pAction, int pModifiers, CallbackInfo ci) {
        if(pWindowPointer != Minecraft.getInstance().getWindow().getWindow())
            return;

        Player player = Minecraft.getInstance().player;

        if (!Minecraft.getInstance().isRunning() || player == null)
            return;

        ClientPhysicsControllerTracker physManager = GrappleModClient.get().getClientControllerManager();
        GrapplingHookPhysicsController controller = physManager.controllers.get(player.getId());

        if (Minecraft.getInstance().options.keyJump.isDown()) {
            if (controller instanceof AirFrictionPhysicsController ctrl && ctrl.wasSliding())
                controller.doSlidingJump();
        }

        physManager.checkSlide(Minecraft.getInstance().player);
    }

}
