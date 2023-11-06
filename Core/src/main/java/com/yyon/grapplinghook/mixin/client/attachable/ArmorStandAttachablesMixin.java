package com.yyon.grapplinghook.mixin.client.attachable;

import com.yyon.grapplinghook.client.attachable.LongFallBootsLayer;
import com.yyon.grapplinghook.client.attachable.model.LongFallBootsModel;
import com.yyon.grapplinghook.content.registry.GrappleModEntityRenderLayerIdentifiers;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandRenderer.class)
@SuppressWarnings({"rawtypes", "unchecked"})
public abstract class ArmorStandAttachablesMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/renderer/entity/EntityRendererProvider$Context;)V", at = @At("TAIL"))
    public void appendRenderLayers(EntityRendererProvider.Context context, CallbackInfo ci) {
        ArmorStandRenderer self = (ArmorStandRenderer) (Object) this;
        ModelLayerLocation loc = GrappleModEntityRenderLayerIdentifiers.LONG_FALL_BOOTS.getLocation();
        LongFallBootsModel model = new LongFallBootsModel(context.bakeLayer(loc));
        LongFallBootsLayer layer = new LongFallBootsLayer(self, model, context.getModelManager());

        self.addLayer(layer);
    }

}
