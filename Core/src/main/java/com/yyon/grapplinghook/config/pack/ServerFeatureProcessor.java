package com.yyon.grapplinghook.config.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.config.ServerFeatures;
import com.yyon.grapplinghook.exception.InvalidDataException;
import net.minecraft.server.packs.resources.Resource;

import java.util.Optional;
import java.util.function.Consumer;

public class ServerFeatureProcessor extends SimpleJsonResourceProcessor {

    public ServerFeatureProcessor() {
        super("Features Config", 1, GrappleMod.id("content/enabled_server_features.json"));
    }

    @Override
    protected void processStackedResourceBody(JsonObject root, Resource resource, boolean replace) {
        if(replace) this.resetGameToDefaults();

        JsonElement featureElement = root.get("features");

        if(featureElement == null)
            return;

        if(!featureElement.isJsonObject())
            throw new InvalidDataException("Server Feature field 'features' must be a object holding a map of string keys to booleans!");

        JsonObject features = featureElement.getAsJsonObject();
        ServerFeatures newFeatureTarget = new ServerFeatures();

        this.tryApplyFeature(features, "block_old_long_fall_boots_recipe", newFeatureTarget::setBlockingOldLongFallBootsRecipe);

        // To make sure the server doesn't get stuck in a weird state due
        // to an error in parsing, only apply the new values  to the server
        // AFTER they have been parsed.
        ServerFeatures.get().copyFrom(newFeatureTarget);
    }

    private void tryApplyFeature(JsonObject root, String key, Consumer<Boolean> setter) {
        JsonElement valueEl = root.get(key);

        // Not defined - respect replacement settings (handled at the top of processStackedResourceBody already)
        if(valueEl == null)
            return;


        // Next 2 errors are if the entry is defined incorrectly, which should be marked as a problem. Throw.
        if(!valueEl.isJsonPrimitive())
            throw new InvalidDataException("Server Feature field 'features.%s' must be a boolean!".formatted(key));

        JsonPrimitive valuePrimitive = valueEl.getAsJsonPrimitive();
        if(!valuePrimitive.isBoolean())
            throw new InvalidDataException("Server Feature field 'features.%s' must be a boolean!".formatted(key));

        setter.accept(valuePrimitive.getAsBoolean());
    }

    @Override
    public void resetGameToDefaults() {
        ServerFeatures.get().reset();
    }
}
