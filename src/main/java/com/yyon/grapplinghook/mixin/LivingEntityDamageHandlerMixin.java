package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.content.item.LongFallBootsItem;
import com.yyon.grapplinghook.util.SharedDamageHandler;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityDamageHandlerMixin {

    @Inject(method = "die(Lnet/minecraft/world/damagesource/DamageSource;)V", at = @At("HEAD"), cancellable = true)
    public void handleDeath(DamageSource source, CallbackInfo ci){
        if(SharedDamageHandler.handleDeath((Entity) (Object) this)) ci.cancel();
    }

    @Inject(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At("HEAD"), cancellable = true)
    public void handleDamage(DamageSource source, float damage, CallbackInfo ci){
        Entity thiss = (Entity) (Object) this;

        if(thiss.isInvulnerableTo(source)) return;
        if(SharedDamageHandler.handleDamage((Entity) (Object) this, source)) ci.cancel();
    }

    @Inject(method = "causeFallDamage(FFLnet/minecraft/world/damagesource/DamageSource;)Z", at = @At("HEAD"), cancellable = true)
    public void handleFall(float fallDistance, float multiplier, DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        Entity thiss = (Entity) (Object) this;
        if (thiss instanceof Player player) {

            for (ItemStack armorStack : player.getArmorSlots()) {
                if(armorStack == null) continue;
                if(armorStack.getItem() instanceof LongFallBootsItem)
                    cir.setReturnValue(false);
            }
        }
    }
}
