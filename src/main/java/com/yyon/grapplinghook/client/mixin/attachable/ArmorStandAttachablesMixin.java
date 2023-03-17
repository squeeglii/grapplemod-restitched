package com.yyon.grapplinghook.client.mixin.attachable;

import com.yyon.grapplinghook.content.registry.GrappleModEntityRenderLayers;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandRenderer.class)
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class ArmorStandAttachablesMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)V", at = @At("TAIL"))
    public void appendRenderLayers(EntityRendererProvider.Context context, CallbackInfo ci) {
        RenderLayer longFallBootsLayer = GrappleModEntityRenderLayers.LONG_FALL_BOOTS.getLayer(((ArmorStandRenderer) (Object) this), context.getModelSet());
        ((ArmorStandRenderer) (Object) this).addLayer(longFallBootsLayer);
    }

}
