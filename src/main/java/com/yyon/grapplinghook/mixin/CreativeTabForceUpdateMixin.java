package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.content.registry.GrappleModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CreativeModeTab.ItemDisplayParameters.class)
public class CreativeTabForceUpdateMixin {

    @Inject(method = "needsUpdate(Lnet/minecraft/world/flag/FeatureFlagSet;ZLnet/minecraft/core/HolderLookup$Provider;)Z", at = @At("RETURN"), cancellable = true)
    private void checkGrappleTabCondition(FeatureFlagSet featureFlagSet, boolean bl, HolderLookup.Provider provider, CallbackInfoReturnable<Boolean> cir) {
        if(GrappleModItems.isCreativeCacheInvalid())
            cir.setReturnValue(true);
    }

}
