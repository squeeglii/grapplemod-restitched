package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.physics.ServerHookEntityTracker;
import com.yyon.grapplinghook.util.SharedDamageHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin {

    @Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V",
            at = @At("HEAD"))
    public void handleDeath(DamageSource source, CallbackInfo ci){
        SharedDamageHandler.handleDeath((Entity) (Object) this);
    }


    @Inject(method = "addAdditionalSaveData(Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At("TAIL"))
    public void saveLastGrappleState(CompoundTag compound, CallbackInfo ci) {
        ServerHookEntityTracker.savePlayerHookState((ServerPlayer) (Object) this, compound);
    }

}
