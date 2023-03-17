package com.yyon.grapplinghook.mixin.client;

import com.yyon.grapplinghook.client.ClientPhysicsContextTracker;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsContext;
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
        if (pos != null) {
            if (ClientPhysicsContextTracker.controllerPos.containsKey(pos)) {
                GrapplingHookPhysicsContext control = ClientPhysicsContextTracker.controllerPos.get(pos);

                control.unattach();

                ClientPhysicsContextTracker.controllerPos.remove(pos);
            }
        }
    }

}
