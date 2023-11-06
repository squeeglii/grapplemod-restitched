package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.attachable.model.LongFallBootsModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// This doesn't use a Built-In registry but follows a style similar to one as
// model locations need registering.
public class GrappleModEntityRenderLayerIdentifiers {

    private static final HashMap<ResourceLocation, RenderLayerEntry> renderLayers;

    static {
        renderLayers = new HashMap<>();
    }

    public static void registerAll() { }

    public static RenderLayerEntry layer(String path, String modelLayerName, Supplier<MeshDefinition> def) {
        ResourceLocation qualId = GrappleMod.fakeId(path);
        RenderLayerEntry entry = new RenderLayerEntry(qualId, modelLayerName, def);

        entry.registerModelLocation();
        renderLayers.put(qualId, entry);
        entry.finalize(def.get());
        return entry;
    }

    public static RenderLayerEntry layer(String id, Supplier<MeshDefinition> def) {
        return layer(id, "armor", def);
    }


    // Registry Entries:
    public static final RenderLayerEntry LONG_FALL_BOOTS = layer(
            "long_fall_boots",
            LongFallBootsModel::createBodyLayer
    );


    public static Map<ResourceLocation, RenderLayerEntry> getRenderLayers() {
        return Collections.unmodifiableMap(renderLayers);
    }


    public static class RenderLayerEntry extends AbstractRegistryReference<MeshDefinition> {

        private final ModelLayerLocation location;


        protected RenderLayerEntry(ResourceLocation path, String modelLayerName, Supplier<MeshDefinition> def) {
            super(path, def);
            this.location = new ModelLayerLocation(path, modelLayerName);
        }

        public ModelLayerLocation getLocation() {
            return this.location;
        }


        private void registerModelLocation() {
            ModelLayerLocation loc = this.getLocation();
            ModelLayers.register(loc.getModel().getPath(), loc.getLayer());
        }
    }

}
