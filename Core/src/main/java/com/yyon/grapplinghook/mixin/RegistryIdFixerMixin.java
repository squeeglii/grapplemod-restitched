package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.data.UpgraderUpper;
import net.minecraft.core.Holder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.WritableRegistry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Optional;

@Mixin(MappedRegistry.class)
public abstract class RegistryIdFixerMixin<T> implements WritableRegistry<T> {

    @Shadow @Final private Map<ResourceLocation, Holder.Reference<T>> byLocation;

    @Inject(method = "get(Lnet/minecraft/resources/ResourceLocation;)Ljava/lang/Object;",
            at = @At("RETURN"),
            cancellable = true)
    private void grapplemod$interceptResourceLocationId(@Nullable ResourceLocation name, CallbackInfoReturnable<@Nullable T> cir) {
        grapplemod$handleCommon(name, cir);
    }

    @Inject(method = "get(Lnet/minecraft/resources/ResourceKey;)Ljava/lang/Object;",
            at = @At("RETURN"),
            cancellable = true)
    private void grapplemod$interceptResourceKeyId(@Nullable ResourceKey<T> key, CallbackInfoReturnable<@Nullable T> cir) {
        if(key == null) return;

        ResourceLocation originalId = key.location();

       grapplemod$handleCommon(originalId, cir);
    }

    @Unique
    private void grapplemod$handleCommon(ResourceLocation id, CallbackInfoReturnable<@Nullable T> cir) {
        Optional<ResourceLocation> replacement = UpgraderUpper.processItemName(id);

        if(replacement.isEmpty())
            return;

        Holder.Reference<T> reference = this.byLocation.get(replacement.get());
        T value = MappedRegistry.getValueFromNullable(reference);

        cir.setReturnValue(value);
        cir.cancel();
    }



}
