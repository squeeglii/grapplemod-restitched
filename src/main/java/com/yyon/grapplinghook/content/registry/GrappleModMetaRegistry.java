package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.customization.CustomizationCategory;
import com.yyon.grapplinghook.customization.CustomizationProperty;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class GrappleModMetaRegistry {

    public static final ResourceKey<Registry<CustomizationProperty<?>>> CUSTOMIZATION_PROPERTIES_KEY = ResourceKey.createRegistryKey(GrappleMod.id("customization_property"));
    public static final ResourceKey<Registry<?>> CUSTOMIZATION_TYPES_KEY = ResourceKey.createRegistryKey(GrappleMod.id("customization_type"));
    public static final ResourceKey<Registry<CustomizationCategory>> CUSTOMIZATION_CATEGORY_KEY = ResourceKey.createRegistryKey(GrappleMod.id("customization_category"));


    public static final MappedRegistry<CustomizationProperty<?>> CUSTOMIZATION_PROPERTIES = FabricRegistryBuilder
            .createSimple(CUSTOMIZATION_PROPERTIES_KEY)
            .buildAndRegister();

    public static final MappedRegistry<?> CUSTOMIZATION_TYPES = FabricRegistryBuilder
            .createSimple(CUSTOMIZATION_TYPES_KEY)
            .buildAndRegister();

    public static final MappedRegistry<CustomizationCategory> CUSTOMIZATION_CATEGORIES = FabricRegistryBuilder
            .createSimple(CUSTOMIZATION_CATEGORY_KEY)
            .buildAndRegister();

}
