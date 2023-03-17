package com.yyon.grapplinghook.mixin.client.attachable;

import com.google.common.collect.ImmutableMap;
import com.yyon.grapplinghook.content.registry.GrappleModEntityRenderLayers;
import net.minecraft.client.model.geom.LayerDefinitions;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LayerDefinitions.class)
public class LayerDefinitionsMixin {

    private static ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builderRef = null;

    @ModifyVariable(method = "createRoots()Ljava/util/Map;", at = @At("STORE"))
    private static ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder(ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder) {
        builderRef = builder;
        return builder;
    }

    //@Inject(method = "createRoots()Ljava/util/Map;", at = @At(value = "HEAD"), locals = LocalCapture.CAPTURE_FAILHARD)
    //private static void builder(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir, ImmutableMap.Builder<ModelLayerLocation, LayerDefinition> builder) {
    //    builderRef = builder;
    //}

    @Inject(method = "createRoots()Ljava/util/Map;",
            remap = false,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;",
                    ordinal = 1
            ))
    private static void insertLayers(CallbackInfoReturnable<Map<ModelLayerLocation, LayerDefinition>> cir) {
        GrappleModEntityRenderLayers.RenderLayerEntry longFallBoots = GrappleModEntityRenderLayers.LONG_FALL_BOOTS;

        builderRef.put(longFallBoots.getLocation(), longFallBoots.get());
    }

}
