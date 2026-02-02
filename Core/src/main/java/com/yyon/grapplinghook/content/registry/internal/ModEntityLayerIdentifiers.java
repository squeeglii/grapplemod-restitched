package com.yyon.grapplinghook.content.registry.internal;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.attachable.model.LongFallBootsModel;
import com.yyon.grapplinghook.content.registry.helper.AbstractRegistryReference;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

// This doesn't use a Built-In registry but follows a style similar to one as
// model locations need registering.
public class ModEntityLayerIdentifiers {

    private static final HashMap<ResourceLocation, RenderLayerEntry> renderLayers;

    static {
        renderLayers = new HashMap<>();
    }

    public static void registerAll() {
        GrappleMod.LOGGER.info("Known Layers: {}", Arrays.toString(ModelLayers.getKnownLocations().toArray()));
    }

    public static RenderLayerEntry layer(String path, String modelLayerName, Supplier<MeshDefinition> def) {
        ResourceLocation qualId = GrappleMod.vanillaId(path);
        // Hours sunk into fixing this, round 2:  4
        // Despite ModelLayerLocation taking a ResourceLocation in, somewhere in the internals, it seems to lose
        // the namespace and fallback to the default Minecraft one.
        // Using any kind of modded namespace results in a full game crash.

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
            GrappleMod.LOGGER.info("Getting Model Layer Location: {}", this.location);
            return this.location;
        }


        private void registerModelLocation() {
            ModelLayerLocation loc = this.getLocation();
            ModelLayers.register(loc.getModel().getPath(), loc.getLayer());
        }
    }

}
