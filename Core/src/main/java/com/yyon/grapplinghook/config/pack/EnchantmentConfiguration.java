package com.yyon.grapplinghook.config.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.enchantment.ConfigurableEnchantment;
import com.yyon.grapplinghook.content.registry.AbstractRegistryReference;
import com.yyon.grapplinghook.content.registry.GrappleModEnchantments;
import com.yyon.grapplinghook.exception.InvalidDataException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentConfiguration {

    public static int CURRENT_VERSION = 1;

    public static void processStack(List<Resource> resourceStack) {
        // Possibly overkill? Seems like a good way to maintain consistency though.
        // Ensure everything's in the default state before applying a configuration.
        EnchantmentConfiguration.reset();

        for(Resource resource: resourceStack) {
            try {
                EnchantmentConfiguration.processStackedResource(resource);
            } catch (Exception err) {
                GrappleMod.LOGGER.error("An error occurred processing pack '%s':".formatted(resource.sourcePackId()), err);
            }
        }
    }

    private static void processStackedResource(Resource enchantmentConfig) throws IOException {
        try (InputStream read = enchantmentConfig.open()) {

            JsonElement enchantmentConfigIn = JsonParser.parseReader(new InputStreamReader(read));

            if(!enchantmentConfigIn.isJsonObject())
                throw new InvalidDataException("Enchantment config requires the root to be an object.");

            JsonObject root = enchantmentConfigIn.getAsJsonObject();
            int version = EnchantmentConfiguration.getFileVersionOrThrow(root);

            if(version != CURRENT_VERSION)
                throw new InvalidDataException(
                        "A provided enchantment configuration has version '%s' isn't supported. Please use version '%s'!".formatted(
                                version, CURRENT_VERSION
                        )
                );

            boolean replaceLowerPacks = EnchantmentConfiguration.getShouldReplaceState(root);

            EnchantmentConfiguration.applyToEnchantments(root, replaceLowerPacks);
        }
    }

    /**
     * Sets all GrappleMod enchantments to their default
     * configuration.
     */
    public static void reset() {
        GrappleModEnchantments.streamEntries()
                .map(AbstractRegistryReference::get)
                .forEach(enchantment -> {
                    enchantment.setDiscoverable(true);
                    enchantment.setTradeable(true);
                });
    }


    private static int getFileVersionOrThrow(JsonObject root) throws InvalidDataException {
        JsonElement versionNumberEl = root.get("version");

        if(versionNumberEl == null || !versionNumberEl.isJsonPrimitive())
            throw new InvalidDataException("Enchantment config field 'version' must be an int (and is required)");

        JsonPrimitive versionPrimitive = versionNumberEl.getAsJsonPrimitive();

        if(!versionPrimitive.isNumber())
            throw new InvalidDataException("Enchantment config field 'version' must be an int (and is required)");

        return versionPrimitive.getAsInt();
    }

    /**
     * Similar to how vanilla resources handle stacking packs - should
     * this configuration disregard all packs below it & use the default
     * value for a missing entry, rather than the lower pack?
     * See: <a href="https://minecraft.wiki/w/Tag#JSON_format">Tag Json Format</a> for a similar implementation
     *
     * @param root the root of the enchantments config
     * @return the value of replace - defaults to false if the value is missing from the config
     * @throws InvalidDataException thrown if the entry in the enchantment config is valid json but of an invalid type
     */
    private static boolean getShouldReplaceState(JsonObject root) throws InvalidDataException {
        JsonElement versionNumberEl = root.get("replace");

        if(versionNumberEl == null)
            return false;

        if(!versionNumberEl.isJsonPrimitive())
            throw new InvalidDataException("Enchantment config field 'replace' must be an boolean!");

        JsonPrimitive versionPrimitive = versionNumberEl.getAsJsonPrimitive();

        if(!versionPrimitive.isBoolean())
            throw new InvalidDataException("Enchantment config field 'replace' must be a boolean!");

        return versionPrimitive.getAsBoolean();
    }

    /**
     * Reads all the appropriate sections of the enchantment config and
     * applies it to the configurable enchantments.
     * Currently, those sections include the two boolean fields:
     * 'discoverable' and 'tradeable'
     */
    private static void applyToEnchantments(JsonObject root, boolean replace) {
        Map<ResourceLocation, Boolean> discover = EnchantmentConfiguration.parseIdSwitches(root, "discoverable");
        Map<ResourceLocation, Boolean> trade = EnchantmentConfiguration.parseIdSwitches(root, "tradeable");

        GrappleModEnchantments.streamEntries().forEach(entry -> {
            ResourceLocation id = entry.getIdentifier();
            ConfigurableEnchantment enchantment = entry.get();

            // Set defaults for simplicity.
            if(replace) {
                enchantment.setDiscoverable(true);
                enchantment.setTradeable(true);
            }

            // And handle any defined overrides here.

            if(discover.containsKey(id)) {
                boolean val = discover.get(id);
                enchantment.setDiscoverable(val);
            }

            if(trade.containsKey(id)) {
                boolean val = trade.get(id);
                enchantment.setTradeable(val);
            }
        });
    }

    private static Map<ResourceLocation, Boolean> parseIdSwitches(JsonObject root, String elementName) {
        JsonElement switchEl = root.get(elementName);

        if(switchEl == null)
            return new HashMap<>();

        if(!switchEl.isJsonObject())
            throw new InvalidDataException("Enchantment Config field '%s' must be a object holding Resource Locations to Booleans!".formatted(elementName));

        JsonObject switchObj = switchEl.getAsJsonObject();

        HashMap<ResourceLocation, Boolean> mappings = new HashMap<>();
        List<ResourceLocation> validIds = GrappleModEnchantments.getEnchantmentIds();

        for(String key: switchObj.keySet()) {
            if(ResourceLocation.isValidResourceLocation(key))
                throw new InvalidDataException("Enchantment Config field '%s' must have Resource Locations for keys!".formatted(elementName));

            JsonElement valueEl = switchObj.get(key);

            if(valueEl == null || !valueEl.isJsonPrimitive())
                throw new InvalidDataException("Enchantment Config field '%s' must have Booleans for values!".formatted(elementName));

            JsonPrimitive valuePrimitive = valueEl.getAsJsonPrimitive();

            if(!valuePrimitive.isBoolean())
                throw new InvalidDataException("Enchantment Config field '%s' must have Booleans for values!".formatted(elementName));

            ResourceLocation resLocKey = new ResourceLocation(key);

            if(!validIds.contains(resLocKey))
                throw new InvalidDataException("Enchantment Config field '%s' contains key '%s' which isn't a recognised GrappleMod enchantment".formatted(elementName, resLocKey.toString()));

            boolean value = valuePrimitive.getAsBoolean();
            mappings.put(resLocKey, value);
        }

        return mappings;
    }

}
