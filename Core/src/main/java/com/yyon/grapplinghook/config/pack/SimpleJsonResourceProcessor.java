package com.yyon.grapplinghook.config.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.yyon.grapplinghook.exception.InvalidDataException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.BufferedReader;
import java.io.IOException;

public abstract class SimpleJsonResourceProcessor extends SimpleResourceProcessor {

    private final int supportedFormatVersion;

    public SimpleJsonResourceProcessor(String loggingName, int version, ResourceLocation resourcePath) {
        super(loggingName, resourcePath);
        this.supportedFormatVersion = version;
    }


    protected abstract void processStackedResourceBody(JsonObject root, Resource resource, boolean replace);


    @Override
    protected final void processStackedResource(BufferedReader read, Resource resource) throws IOException {
        JsonElement configIn = JsonParser.parseReader(read);

        if(!configIn.isJsonObject())
            throw new InvalidDataException("%s requires the root to be an object.".formatted(this.getLoggingName()));

        JsonObject root = configIn.getAsJsonObject();
        int version = this.getFileVersionOrThrow(root);

        if(version != this.getSupportedFormatVersion())
            throw new InvalidDataException(
                    "A provided %s has version '%s' isn't supported. Please use version '%s'!".formatted(
                            this.getLoggingName(),
                            version,
                            this.getSupportedFormatVersion()
                    )
            );

        boolean replaceLowerPacks = this.getShouldReplaceState(root);

        this.processStackedResourceBody(root, resource, replaceLowerPacks);
    }

    /**
     * Obtains the format version stated in the json file
     * @param root
     * @return
     * @throws InvalidDataException
     */
    private int getFileVersionOrThrow(JsonObject root) throws InvalidDataException {
        JsonElement versionNumberEl = root.get("version");

        if(versionNumberEl == null || !versionNumberEl.isJsonPrimitive())
            throw new InvalidDataException("%s field 'version' must be an int (and is required)".formatted(this.getLoggingName()));

        JsonPrimitive versionPrimitive = versionNumberEl.getAsJsonPrimitive();

        if(!versionPrimitive.isNumber())
            throw new InvalidDataException("%s field 'version' must be an int (and is required)".formatted(this.getLoggingName()));

        return versionPrimitive.getAsInt();
    }

    /**
     * Similar to how vanilla resources handle stacking packs - should
     * this configuration disregard all packs below it & use the default
     * value for a missing entry, rather than the lower pack?
     * See: <a href="https://minecraft.wiki/w/Tag#JSON_format">Tag Json Format</a> for a similar implementation
     *
     * @param root the root of the resource json
     * @return the value of replace - defaults to false if the value is missing from the config
     * @throws InvalidDataException thrown if the entry in the config is valid json but of an invalid type
     */
    private boolean getShouldReplaceState(JsonObject root) throws InvalidDataException {
        JsonElement versionNumberEl = root.get("replace");

        if(versionNumberEl == null)
            return false;

        if(!versionNumberEl.isJsonPrimitive())
            throw new InvalidDataException("%s field 'replace' must be an boolean!".formatted(this.getLoggingName()));

        JsonPrimitive versionPrimitive = versionNumberEl.getAsJsonPrimitive();

        if(!versionPrimitive.isBoolean())
            throw new InvalidDataException("%s field 'replace' must be a boolean!".formatted(this.getLoggingName()));

        return versionPrimitive.getAsBoolean();
    }


    public final int getSupportedFormatVersion() {
        return this.supportedFormatVersion;
    }
}
