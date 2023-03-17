package com.yyon.grapplinghook.mixin.client;

import com.yyon.grapplinghook.client.ClientPhysicsContextTracker;
import com.yyon.grapplinghook.physics.context.AirFrictionPhysicsContext;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsContext;
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
        if(pWindowPointer != Minecraft.getInstance().getWindow().getWindow()) return;
        Player player = Minecraft.getInstance().player;
        if (!Minecraft.getInstance().isRunning() || player == null) return;


        GrapplingHookPhysicsContext controller = null;
        if (ClientPhysicsContextTracker.controllers.containsKey(player.getId())) {
            controller = ClientPhysicsContextTracker.controllers.get(player.getId());
        }

        if (Minecraft.getInstance().options.keyJump.isDown()) {
            if (controller != null) {
                if (controller instanceof AirFrictionPhysicsContext && ((AirFrictionPhysicsContext) controller).wasSliding) {
                    controller.slidingJump();
                }
            }
        }

        ClientPhysicsContextTracker.instance.checkSlide(Minecraft.getInstance().player);
    }

}
