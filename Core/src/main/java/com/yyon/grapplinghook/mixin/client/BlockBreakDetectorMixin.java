package com.yyon.grapplinghook.mixin.client;

import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.api.GrappleModClientEvents;
import com.yyon.grapplinghook.client.physics.ClientPhysicsControllerTracker;
import com.yyon.grapplinghook.client.physics.context.GrapplingHookPhysicsController;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MultiPlayerGameMode.class)
public class BlockBreakDetectorMixin {

    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z", at = @At("RETURN"))
    public void handleBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(!cir.getReturnValue()) return;
        if (pos == null) return;

        ClientPhysicsControllerTracker physManager = GrappleModClient.get().getClientControllerManager();

        if (physManager.controllerPos.containsKey(pos)) {
            GrapplingHookPhysicsController control = physManager.controllerPos.get(pos);
            control.disable();
            physManager.controllerPos.remove(pos);

            GrappleModClientEvents.HOOK_DETACH.invoker().onHookDetach(control.entity);
        }
    }

}
