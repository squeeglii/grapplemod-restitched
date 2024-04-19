package com.yyon.grapplinghook.config.pack;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.enchantment.ConfigurableEnchantment;
import com.yyon.grapplinghook.content.registry.GrappleModEnchantments;
import com.yyon.grapplinghook.exception.InvalidDataException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentProcessor extends SimpleJsonResourceProcessor {

    public static EnchantmentProcessor instance;

    public EnchantmentProcessor() {
        super("Enchantment Config", 1, GrappleMod.id("content/available_enchantments.json"));
    }

    /**
     * Sets all GrappleMod enchantments to their default
     * configuration.
     */
    @Override
    public void resetGameToDefaults() {
        GrappleModEnchantments.streamEntries()
                .map(GrappleModEnchantments.EnchantmentEntry::get)
                .forEach(enchantment -> {
                    enchantment.setDiscoverable(true);
                    enchantment.setTradeable(true);
                });
    }

    /**
     * Reads all the appropriate sections of the enchantment config and
     * applies it to the configurable enchantments.
     * Currently, those sections include the two boolean fields:
     * 'discoverable' and 'tradeable'
     */
    @Override
    protected void processStackedResourceBody(JsonObject root, Resource resource, boolean replace) {
        Map<ResourceLocation, Boolean> discover = this.parseIdSwitches(root, "discoverable");
        Map<ResourceLocation, Boolean> trade = this.parseIdSwitches(root, "tradeable");

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

    private Map<ResourceLocation, Boolean> parseIdSwitches(JsonObject root, String elementName) {
        JsonElement switchEl = root.get(elementName);

        if(switchEl == null)
            return new HashMap<>();

        if(!switchEl.isJsonObject())
            throw new InvalidDataException("Enchantment Config field '%s' must be a object holding Resource Locations to Booleans!".formatted(elementName));

        JsonObject switchObj = switchEl.getAsJsonObject();

        HashMap<ResourceLocation, Boolean> mappings = new HashMap<>();
        List<ResourceLocation> validIds = GrappleModEnchantments.getEnchantmentIds();

        for(String key: switchObj.keySet()) {
            if(!ResourceLocation.isValidResourceLocation(key))
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

    public static EnchantmentProcessor get() {
        if(instance == null) instance = new EnchantmentProcessor();
        return instance;
    }
}
