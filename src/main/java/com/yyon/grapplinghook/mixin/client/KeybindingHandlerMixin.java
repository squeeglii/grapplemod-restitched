package com.yyon.grapplinghook.mixin.client;

import com.yyon.grapplinghook.client.ClientControllerManager;
import com.yyon.grapplinghook.controller.AirfrictionController;
import com.yyon.grapplinghook.controller.GrappleController;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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


        GrappleController controller = null;
        if (ClientControllerManager.controllers.containsKey(player.getId())) {
            controller = ClientControllerManager.controllers.get(player.getId());
        }

        if (Minecraft.getInstance().options.keyJump.isDown()) {
            if (controller != null) {
                if (controller instanceof AirfrictionController && ((AirfrictionController) controller).wasSliding) {
                    controller.slidingJump();
                }
            }
        }

        ClientControllerManager.instance.checkSlide(Minecraft.getInstance().player);
    }

}
