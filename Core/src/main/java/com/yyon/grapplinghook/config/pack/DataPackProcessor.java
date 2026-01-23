package com.yyon.grapplinghook.config.pack;

import com.yyon.grapplinghook.GrappleMod;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;

/**
 * Handles extensions of data packs - content types
 * that are not handled by vanilla.
 */
public class DataPackProcessor implements SimpleSynchronousResourceReloadListener {

    private static final ResourceLocation ID = GrappleMod.id("mod_data_configuration");

    private final List<SimpleResourceProcessor> subProcessors;


    //todo: does this need the ServerFeatureProcessor?
    public DataPackProcessor() {
        this.subProcessors = List.of(

        );
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        for(SimpleResourceProcessor processor: this.subProcessors) {
            try {
                processor.process(resourceManager);
            } catch (Exception err) {
                GrappleMod.LOGGER.error("Error while processing the '%s' resource".formatted(processor.getResourcePath()), err);
            }
        }
    }
}
