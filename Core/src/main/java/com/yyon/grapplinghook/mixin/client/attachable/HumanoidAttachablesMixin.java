package com.yyon.grapplinghook.mixin.client.attachable;

import com.yyon.grapplinghook.content.registry.GrappleModEntityRenderLayers;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidMobRenderer.class)
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class HumanoidAttachablesMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Lnet/minecraft/client/model/HumanoidModel;FFFF)V", at = @At("TAIL"))
    public void appendRenderLayers(EntityRendererProvider.Context context, HumanoidModel humanoidModel, float f, float g, float h, float i, CallbackInfo ci) {
        RenderLayer longFallBootsLayer = GrappleModEntityRenderLayers.LONG_FALL_BOOTS.getLayer(((HumanoidMobRenderer) (Object) this), context.getModelSet());
        ((HumanoidMobRenderer) (Object) this).addLayer(longFallBootsLayer);
    }

}
