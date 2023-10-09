package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public abstract class ServerStartMixin {

    @Shadow public abstract void setFlightAllowed(boolean set);

    @Inject(method = "runServer()V",
            at = @At(
                    value = "INVOKE_ASSIGN",
                    target = "Lnet/minecraft/Util;getMillis()J",
                    shift = At.Shift.BEFORE
            ))
    public void onServerStart(CallbackInfo ci) {
        if (GrappleModLegacyConfig.getConf().other.override_allowflight) {
            this.setFlightAllowed(true);
        }
    }
}
