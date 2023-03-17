package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.customization.CustomizationCategory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GrappleModCustomizationCategories {

    private static final HashMap<ResourceLocation, Entry<?>> categories;

    static {
        categories = new HashMap<>();
    }

    public static <P extends CustomizationCategory> Entry<P> property(String id, Supplier<P> type) {
        ResourceLocation qualId = GrappleMod.id(id);
        Entry<P> entry = new Entry<>(qualId, type);
        categories.put(qualId, entry);
        return entry;
    }


    public static void registerAll() {
        for(Map.Entry<ResourceLocation, Entry<?>> def: categories.entrySet()) {
            ResourceLocation id = def.getKey();
            Entry<?> data = def.getValue();
            CustomizationCategory it = data.getFactory().get();

            data.finalize(Registry.register(GrappleModMetaRegistry.CUSTOMIZATION_CATEGORIES, id, it));
        }
    }





    public static class Entry<T extends CustomizationCategory> extends AbstractRegistryReference<T> {

        protected Entry(ResourceLocation id, Supplier<T> factory) {
            super(id, factory);
        }
    }


}
