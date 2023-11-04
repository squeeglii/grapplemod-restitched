package com.yyon.grapplinghook.data;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Optional;

// Item names are hard to version -- they just apply across all versions.
public class GlobalLookup {

    private static HashMap<ResourceLocation, ResourceLocation> mappedItemIds = new HashMap<>();

    private static void mapItemId(String original, String newName) {
        mappedItemIds.put(
                GrappleMod.id(original),
                GrappleMod.id(newName)
        );
    }

    static {

    }



    public static Optional<ResourceLocation> getMappingFor(ResourceLocation originalItem) {
        if(!mappedItemIds.containsKey(originalItem))
            return Optional.empty();

        return Optional.of(mappedItemIds.get(originalItem));
    }

}
