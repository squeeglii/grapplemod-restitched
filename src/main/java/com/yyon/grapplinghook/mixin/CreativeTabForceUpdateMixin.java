package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.registry.GrappleModItems;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTabs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeTabs.class)
public class CreativeTabForceUpdateMixin {

    @Inject(method = "wouldRebuildSameContents(Lnet/minecraft/world/flag/FeatureFlagSet;Z)Z", at = @At("RETURN"), cancellable = true)
    private static void checkGrappleTabCondition(FeatureFlagSet enabledFeatures, boolean displayOperatorCreativeTab, CallbackInfoReturnable<Boolean> cir) {
        if(GrappleModItems.isCreativeCacheInvalid())
            cir.setReturnValue(false);
    }

}
