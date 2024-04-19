package com.yyon.grapplinghook;

import com.yyon.grapplinghook.command.GrappleModCommand;
import com.yyon.grapplinghook.config.ServerFeatures;
import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.config.pack.DataPackProcessor;
import com.yyon.grapplinghook.content.registry.*;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.physics.ServerPhysicsObserver;
import com.yyon.grapplinghook.util.GrappleModUtils;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.InteractionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */
public class GrappleMod implements ModInitializer {

    public static final String MOD_ID = "grapplemod";
    public static final Logger LOGGER = LogManager.getLogger();

    private static GrappleMod instance;

    private ServerFeatures serverFeatures;

    private ServerPhysicsObserver serverPhysicsObserver;

    @Override
    public void onInitialize() {
        instance = this;

        try {
            this.initConfig();
        } catch (Exception e) {
            LOGGER.info(e);
        }

        this.serverFeatures = new ServerFeatures();

        GrappleModBlocks.registerAllBlocks();
        GrappleModItems.registerAllItems();  // Items must always be registered after blocks.
        GrappleModEntities.registerAllEntities();
        GrappleModEnchantments.registerAllEnchantments();
        GrappleModBlockEntities.registerAllBlockEntities();
        GrappleModAdvancementTriggers.registerAllTriggers();

        GrappleModCustomizationProperties.registerAll();
        GrappleModCustomizationCategories.registerAll(); // Categories must always go after items + properties.

        // Some things don't need "registering" but are static so they still
        // need loading. Load them now for reliability.
        GrappleModTags.bump();
        GrappleModGamerules.bump();

        this.queueCommandRegistration();

        NetworkManager.registerPacketListeners();

        this.serverPhysicsObserver = new ServerPhysicsObserver();

        this.registerDataPacks();
    }

    private void initConfig() {
        ConfigHolder<?> cfg = AutoConfig.register(GrappleModLegacyConfig.class, GsonConfigSerializer::new);

        cfg.registerSaveListener((holder, config) -> {
            GrappleModItems.invalidateCreativeTabCache();
            return InteractionResult.SUCCESS;
        });

        cfg.registerLoadListener((holder, config) -> {
            GrappleModItems.invalidateCreativeTabCache();
            return InteractionResult.SUCCESS;
        });
    }

    private void queueCommandRegistration() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            if(FabricLoader.getInstance().isDevelopmentEnvironment())
                dispatcher.register(GrappleModCommand.build());
        });
    }

    public void registerDataPacks() {
        GrappleMod.LOGGER.info("Re-assigning datapack reload listener...");

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new DataPackProcessor());

        GrappleMod.LOGGER.info("Loading default data packs...");
        Optional<ModContainer> cont = FabricLoader.getInstance().getModContainer(GrappleMod.MOD_ID);

        if(cont.isEmpty()) {
            GrappleMod.LOGGER.error("Unable to register datapacks! This mod technically doesn't exist!!");
            return;
        }

        ModContainer container = cont.get();
        GrappleModUtils.registerPack("simplified", Component.translatable("pack.grapplemod.simplified"), container, ResourcePackActivationType.NORMAL);
        GrappleModUtils.registerPack("no_enchants", Component.translatable("pack.grapplemod.no_enchants"), container, ResourcePackActivationType.NORMAL);
        GrappleModUtils.registerPack("classic_recipes", Component.translatable("pack.grapplemod.classic_recipes"), container, ResourcePackActivationType.NORMAL);

        GrappleMod.LOGGER.info("All done with datapacks!");
    }

    public ServerPhysicsObserver getServerPhysicsObserver() {
        return this.serverPhysicsObserver;
    }

    public ServerFeatures getServerFeatures() {
        return this.serverFeatures;
    }

    public static GrappleMod get() {
        return instance;
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MOD_ID, id);
    }

    public static ResourceLocation fakeId(String id) {
        return new ResourceLocation("minecraft", id);
    }
}
