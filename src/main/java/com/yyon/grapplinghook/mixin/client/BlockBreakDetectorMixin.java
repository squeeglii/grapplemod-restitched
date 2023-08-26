package com.yyon.grapplinghook.mixin.client;

import com.yyon.grapplinghook.client.ClientPhysicsControllerTracker;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class BlockBreakDetectorMixin {

    // Was in client events but uses a server class????

    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z", at = @At(value = "HEAD", shift = At.Shift.BY, by = 1))
    public void handleBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (pos == null) return;

        ClientPhysicsControllerTracker physManager = GrappleModClient.get().getClientControllerManager();

        if (physManager.controllerPos.containsKey(pos)) {
            GrapplingHookPhysicsController control = physManager.controllerPos.get(pos);
            control.disable();
            physManager.controllerPos.remove(pos);
        }
    }

}
