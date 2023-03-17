package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.customization.CustomizationProperty;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GrappleModCustomizationProperties {

    private static final HashMap<ResourceLocation, Entry<?>> properties;

    static {
        properties = new HashMap<>();
    }

    public static <P extends CustomizationProperty<?>> Entry<P> property(String id, Supplier<P> type) {
        ResourceLocation qualId = GrappleMod.id(id);
        Entry<P> entry = new Entry<>(qualId, type);
        properties.put(qualId, entry);
        return entry;
    }


    public static void registerAll() {
        for(Map.Entry<ResourceLocation, Entry<?>> def: properties.entrySet()) {
            ResourceLocation id = def.getKey();
            Entry<?> data = def.getValue();
            CustomizationProperty<?> it = data.getFactory().get();

            data.finalize(Registry.register(GrappleModMetaRegistry.CUSTOMIZATION_PROPERTIES, id, it));
        }
    }





    public static class Entry<T extends CustomizationProperty<?>> extends AbstractRegistryReference<T> {

        protected Entry(ResourceLocation id, Supplier<T> factory) {
            super(id, factory);
        }
    }


}
