package com.yyon.grapplinghook.mixin.client;

import com.yyon.grapplinghook.client.ClientPhysicsControllerTracker;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public abstract class DebugMenuMixin {


    @Inject(method = "getGameInformation()Ljava/util/List;",
            at = @At("RETURN"))
    private void getGrappleModDebugInformation(CallbackInfoReturnable<List<String>> cir) {
        List<String> list = cir.getReturnValue();
        list.add("-- GrappleMod Debug");


        Player player = Minecraft.getInstance().player;
        int playerId = player != null
                ? player.getId()
                : -1;
        int controllerCount = ClientPhysicsControllerTracker.controllers.size();

        GrapplingHookPhysicsController ctx = ClientPhysicsControllerTracker.controllers.get(playerId);


        if(ctx == null) {
            list.add("Controllers: %s#".formatted(controllerCount));
            return;
        }



        String controllerID = String.valueOf(ctx.getControllerTypeId());
        String inactiveNotice = ctx.isControllerActive()
                ? ""
                : " (inactive)";

        list.add("Controller: c%s, t%s%s".formatted(
                controllerCount, controllerID, inactiveNotice
        ));
        list.add("Duplicates: %s".formatted(ctx.getDuplicates()));
        list.add("Motion: %s".formatted(ctx.getCopyOfMotion()));
    }

}
