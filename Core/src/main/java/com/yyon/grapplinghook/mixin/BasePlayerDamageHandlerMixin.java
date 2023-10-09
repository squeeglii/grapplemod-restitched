package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.util.SharedDamageHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class BasePlayerDamageHandlerMixin {

    @Shadow public abstract boolean isInvulnerableTo(DamageSource source);

    @Inject(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At("HEAD"), cancellable = true)
    public void handleDamage(DamageSource source, float damage, CallbackInfo ci){
        if(this.isInvulnerableTo(source)) return;
        if(SharedDamageHandler.handleDamage((Entity) (Object) this, source)) ci.cancel();
    }

}
