package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.attachable.LongFallBootsLayer;
import com.yyon.grapplinghook.client.attachable.model.LongFallBootsModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

// This doesn't use a Built-In registry but follows a style similar to one as
// model locations need registering.
@SuppressWarnings("rawtypes")
public class GrappleModEntityRenderLayers {

    private static HashMap<ResourceLocation, RenderLayerEntry> renderLayers;

    static {
        GrappleModEntityRenderLayers.renderLayers = new HashMap<>();
    }

    public static void registerAll() { }

    public static RenderLayerEntry layer(String path, String modelLayerName, BiFunction<RenderLayerParent, EntityModelSet, RenderLayer> layerFactory, Supplier<LayerDefinition> def) {
        ResourceLocation qualId = GrappleMod.fakeId(path);
        RenderLayerEntry entry = new RenderLayerEntry(qualId, modelLayerName, def, layerFactory);

        entry.registerModelLocation();
        GrappleModEntityRenderLayers.renderLayers.put(qualId, entry);
        entry.finalize(def.get());
        return entry;
    }

    public static RenderLayerEntry layer(String id, BiFunction<RenderLayerParent, EntityModelSet, RenderLayer> layerFactory, Supplier<LayerDefinition> def) {
        return layer(id, "main", layerFactory, def);
    }


    private static BiFunction<RenderLayerParent<?, ?>, EntityModelSet, RenderLayer<?, ?>> noModelIncluded(Function<RenderLayerParent<?, ?>, RenderLayer<?, ?>> layerFactory) {
        return (parent, model) -> layerFactory.apply(parent);
    }


    // Registry Entries:
    public static final RenderLayerEntry LONG_FALL_BOOTS = GrappleModEntityRenderLayers.layer("long_fall_boots", LongFallBootsLayer::new, LongFallBootsModel::generateLayer);


    public static Map<ResourceLocation, RenderLayerEntry> getRenderLayers() {
        return Collections.unmodifiableMap(renderLayers);
    }

    public static class RenderLayerEntry extends AbstractRegistryReference<LayerDefinition> {

        private final ModelLayerLocation location;

        private final BiFunction<RenderLayerParent, EntityModelSet, RenderLayer> layerFactory;


        protected RenderLayerEntry(ResourceLocation path, String modelLayerName, Supplier<LayerDefinition> def, BiFunction<RenderLayerParent, EntityModelSet, RenderLayer> layerFactory) {
            super(path, def);
            this.location = new ModelLayerLocation(path, modelLayerName);
            this.layerFactory = layerFactory;
        }

        public ModelLayerLocation getLocation() {
            return this.location;
        }

        //@SuppressWarnings("unchecked")
        public RenderLayer getLayer(RenderLayerParent<? extends LivingEntity, ? extends EntityModel<? extends LivingEntity>> parent, EntityModelSet modelSet) {
            return this.getLayerFactory().apply(parent, modelSet);
        }

        public BiFunction<RenderLayerParent, EntityModelSet, RenderLayer> getLayerFactory() {
            return layerFactory;
        }

        private void registerModelLocation() {
            ModelLayerLocation loc = this.getLocation();
            ModelLayers.register(loc.getModel().getPath(), loc.getLayer());
        }
    }

}
