package com.yyon.grapplinghook.mixin.client.attachable;

import com.yyon.grapplinghook.content.registry.GrappleModEntityRenderLayers;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererAttachablesMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V", at = @At("TAIL"))
    public void appendRenderLayers(EntityRendererProvider.Context context, boolean bl, CallbackInfo ci) {
        RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> longFallBootsLayer = GrappleModEntityRenderLayers.LONG_FALL_BOOTS.getLayer(((PlayerRenderer) (Object) this), context.getModelSet());
        ((PlayerRenderer) (Object) this).addLayer(longFallBootsLayer);
    }

}
