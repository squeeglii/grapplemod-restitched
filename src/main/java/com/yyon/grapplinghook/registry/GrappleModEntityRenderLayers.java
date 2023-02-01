package com.yyon.grapplinghook.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.util.BiParamFunction;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import java.util.HashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public class GrappleModEntityRenderLayers {

    private static HashMap<ResourceLocation, RenderLayerEntry<?, ?, ?>> renderLayers;

    static {
        GrappleModEntityRenderLayers.renderLayers = new HashMap<>();
    }

    public static <T extends Entity, M extends EntityModel<T>, E extends RenderLayer<T, M>> RenderLayerEntry<T, M, E> layer(String path, String modelLayerName, BiParamFunction<RenderLayerParent<T, M>, EntityModelSet, E> layerFactory, Supplier<LayerDefinition> def) {
        ResourceLocation qualId = GrappleMod.id(path);
        RenderLayerEntry<T, M, E> entry = new RenderLayerEntry<>(qualId, modelLayerName, def, layerFactory);

        entry.registerModelLocation();
        GrappleModEntityRenderLayers.renderLayers.put(qualId, entry);
        return entry;
    }

    public static <T extends Entity, M extends EntityModel<T>, E extends RenderLayer<T, M>> RenderLayerEntry<T, M, E> layer(String id, BiParamFunction<RenderLayerParent<T, M>, EntityModelSet, E> layerFactory, Supplier<LayerDefinition> def) {
        return layer(id, "main", layerFactory, def);
    }


    private static <T extends Entity, M extends EntityModel<T>, E extends RenderLayer<T, M>> BiParamFunction<RenderLayerParent<T, M>, EntityModelSet, E> noModelIncluded(Function<RenderLayerParent<?, ?>, E> layerFactory) {
        return (parent, model) -> layerFactory.apply(parent);
    }






    public static class RenderLayerEntry<T extends Entity, M extends EntityModel<T>, E extends RenderLayer<T, M>> extends AbstractRegistryReference<LayerDefinition> {

        private final ModelLayerLocation location;
        private final BiParamFunction<RenderLayerParent<T, M>, EntityModelSet, E> layerFactory;

        protected RenderLayerEntry(ResourceLocation path, String modelLayerName, Supplier<LayerDefinition> def, BiParamFunction<RenderLayerParent<T, M>, EntityModelSet, E> layerFactory) {
            super(path, def);
            this.location = new ModelLayerLocation(path, modelLayerName);
            this.layerFactory = layerFactory;
        }

        public ModelLayerLocation getLocation() {
            return this.location;
        }

        public E getLayer(RenderLayerParent<T, M> parent, EntityModelSet modelSet) {
            return this.getLayerFactory().apply(parent, modelSet);
        }

        public BiParamFunction<RenderLayerParent<T, M>, EntityModelSet, E> getLayerFactory() {
            return this.layerFactory;
        }

        private void registerModelLocation() {
            ModelLayerLocation loc = this.getLocation();
            ModelLayers.register(loc.getModel().getPath(), loc.getLayer());
        }
    }

}
