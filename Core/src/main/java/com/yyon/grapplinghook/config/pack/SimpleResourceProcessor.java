package com.yyon.grapplinghook.config.pack;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public abstract class SimpleResourceProcessor {

    private final String loggingName;
    private final ResourceLocation resourcePath;

    public SimpleResourceProcessor(String loggingName, ResourceLocation resourcePath) {
        this.loggingName = loggingName;
        this.resourcePath = resourcePath;
    }


    /** Processes a stack of files that this processor should handle, in order. */
    public void process(ResourceManager source) {
        List<Resource> resourceStack = source.getResourceStack(this.resourcePath);

        // Possibly overkill? Seems like a good way to maintain consistency though.
        // Ensure everything's in the default state before applying a configuration.
        this.resetGameToDefaults();

        for(Resource resource: resourceStack) {
            try(BufferedReader reader = resource.openAsReader()) {
                this.processStackedResource(reader, resource);

            } catch (IOException err) {
                GrappleMod.LOGGER.error("Unable to read '%s' processing pack '%s': %s".formatted(
                        this.getResourcePath(),
                        resource.sourcePackId(),
                        err.getMessage()
                )); // full stack trace just bloats the log - print the message only.

            } catch (Exception err) {
                GrappleMod.LOGGER.error("An error occurred processing the resource at '%s' in pack '%s':".formatted(
                        this.getResourcePath(),
                        resource.sourcePackId()
                ), err);
            }
        }
    }

    /**
     * Applies a 'default' version of the resource handled
     * by this resource processor.
     *
     * i.e. the enchantment configuration re-enabled all enchantments
     *      in-game.
     */
    public abstract void resetGameToDefaults();

    /**
     * Processes a variation of the file this processor is
     * handling, from the lowest priority to the highest priority.
     */
    protected abstract void processStackedResource(BufferedReader read, Resource resource) throws IOException;

    public final String getLoggingName() {
        return this.loggingName;
    }

    public final ResourceLocation getResourcePath() {
        return this.resourcePath;
    }
}
