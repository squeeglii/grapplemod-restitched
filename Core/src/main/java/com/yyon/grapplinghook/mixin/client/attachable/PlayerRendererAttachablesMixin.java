package com.yyon.grapplinghook.mixin.client.attachable;

import com.yyon.grapplinghook.client.attachable.LongFallBootsLayer;
import com.yyon.grapplinghook.client.attachable.model.LongFallBootsModel;
import com.yyon.grapplinghook.content.registry.GrappleModEntityRenderLayerIdentifiers;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererAttachablesMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;Z)V", at = @At("TAIL"))
    public void appendRenderLayers(EntityRendererProvider.Context context, boolean bl, CallbackInfo ci) {
        PlayerRenderer self = (PlayerRenderer) (Object) this;
        ModelLayerLocation loc = GrappleModEntityRenderLayerIdentifiers.LONG_FALL_BOOTS.getLocation();
        LongFallBootsModel model = new LongFallBootsModel(context.bakeLayer(loc));
        LongFallBootsLayer layer = new LongFallBootsLayer(self, model, context.getModelManager());

        self.addLayer(layer);
    }

}
