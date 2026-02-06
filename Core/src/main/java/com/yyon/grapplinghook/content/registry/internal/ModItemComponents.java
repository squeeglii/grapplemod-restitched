package com.yyon.grapplinghook.content.registry.internal;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.content.customization.data.TemplateAuthor;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;

public class ModItemComponents {

    public static final DataComponentType<HookCustomization> CUSTOMIZABLE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            GrappleMod.id("customizable"),
            DataComponentType.<HookCustomization>builder().persistent(HookCustomization.CODEC).networkSynchronized(HookCustomization.STREAM_CODEC).cacheEncoding().build()
    );

    // Should never be saved - exclusively for UI previews.
    public static final DataComponentType<HookCustomization> MODIFICATION_TABLE_DELTA = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            GrappleMod.id("previous_customization"),
            DataComponentType.<HookCustomization>builder().build()
    );

    public static final DataComponentType<Unit> FORCE_HOOK_DISPLAY = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            GrappleMod.id("force_hook_display"),
            DataComponentType.<Unit>builder().persistent(Unit.CODEC).networkSynchronized(StreamCodec.unit(Unit.INSTANCE)).cacheEncoding().build()
    );

    public static final DataComponentType<TemplateAuthor> AUTHORED = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            GrappleMod.id("authored"),
            DataComponentType.<TemplateAuthor>builder().persistent(TemplateAuthor.CODEC).networkSynchronized(TemplateAuthor.STREAM_CODEC).cacheEncoding().build()
    );

    public static void bump() {
        GrappleMod.LOGGER.info("Registering item data components");
    }

}
