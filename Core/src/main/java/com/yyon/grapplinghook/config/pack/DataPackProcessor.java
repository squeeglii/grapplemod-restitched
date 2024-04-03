package com.yyon.grapplinghook.config.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.exception.InvalidDataException;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

/**
 * Handles extensions of data packs - content types
 * that are not handled by vanilla.
 */
public class DataPackProcessor implements SimpleSynchronousResourceReloadListener {

    private static final ResourceLocation ID = GrappleMod.id("mod_data_configuration");

    private static final ResourceLocation ENCHANTMENTS = GrappleMod.id("content/available_enchantments.json");

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        Optional<Resource> optEnchantmentConfig = resourceManager.getResource(ENCHANTMENTS);

        if(optEnchantmentConfig.isPresent()) {
            Resource enchantmentConfig = optEnchantmentConfig.get();
            this.loadEnchantments(enchantmentConfig);
        }
    }

    private void loadEnchantments(Resource enchantmentConfig) {
        try (InputStream read = enchantmentConfig.open()) {

            JsonElement enchantmentConfigIn = JsonParser.parseReader(new InputStreamReader(read));

            if(!enchantmentConfigIn.isJsonObject())
                throw new InvalidDataException("Enchantment config (from pack '%s') requires the root to be an object.".formatted(enchantmentConfig.sourcePackId()));

            //TODO: For each enchantment id, configure whether they're enabled or not. Can be a simple boolean for now.
            // Also add a damn version number.

        } catch (IOException e) {
            GrappleMod.LOGGER.error("Skipping resource due to error: ", e);
        }
    }

}
