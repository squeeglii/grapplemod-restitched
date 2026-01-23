package com.yyon.grapplinghook.content.registry.internal;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.customization.data.DeployState;
import com.yyon.grapplinghook.customization.data.HookCustomization;
import com.yyon.grapplinghook.customization.data.TemplateAuthor;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

public class ModItemComponents {

    public static final DataComponentType<HookCustomization> CUSTOMIZABLE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            GrappleMod.id("customizable"),
            DataComponentType.<HookCustomization>builder().persistent(HookCustomization.CODEC).networkSynchronized(HookCustomization.STREAM_CODEC).cacheEncoding().build()
    );

    public static final DataComponentType<DeployState> DEPLOYABLE = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            GrappleMod.id("deployable"),
            DataComponentType.<DeployState>builder().persistent(DeployState.CODEC).networkSynchronized(DeployState.STREAM_CODEC).cacheEncoding().build()
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
