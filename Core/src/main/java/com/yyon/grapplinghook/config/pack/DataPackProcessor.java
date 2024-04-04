package com.yyon.grapplinghook.config.pack;

import com.yyon.grapplinghook.GrappleMod;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.List;

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
        List<Resource> enchantmentConfigs = resourceManager.getResourceStack(ENCHANTMENTS);

        try {
            EnchantmentConfiguration.processStack(enchantmentConfigs);
        } catch (Exception err) {
            GrappleMod.LOGGER.error("Error while processing the Enchantment Configuration stack", err);
        }

    }



}
