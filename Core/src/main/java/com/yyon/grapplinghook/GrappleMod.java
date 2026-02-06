package com.yyon.grapplinghook;

import com.yyon.grapplinghook.content.command.GrappleModCommand;
import com.yyon.grapplinghook.config.GrappleModCommonConfig;
import com.yyon.grapplinghook.config.ServerFeatures;
import com.yyon.grapplinghook.config.pack.DataPackProcessor;
import com.yyon.grapplinghook.content.registry.CustomizationCategories;
import com.yyon.grapplinghook.content.registry.CustomizationProperties;
import com.yyon.grapplinghook.content.registry.internal.*;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.physics.ServerPhysicsObserver;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.scheduling.Ticker;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
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

    private static MinecraftServer currentServerInstance = null;
    private static GrappleMod instance = null;

    private ServerFeatures serverFeatures;
    private Ticker ticker;
    private ServerPhysicsObserver serverPhysicsObserver;

    @Override
    public void onInitialize() {
        instance = this;

        this.ticker = new Ticker();

        try {
            this.initConfig();
        } catch (Exception e) {
            LOGGER.info(e);
        }

        this.serverFeatures = new ServerFeatures();

        // I assume this is needed before items.
        ModDataComponents.bump();

        ModArmourMaterials.registerAllMaterials();
        ModEnchantments.registerImmutable();

        ModBlocks.registerAllBlocks();
        ModItems.registerAllItems();  // Items must always be registered after blocks.
        ModEntities.registerAllEntities();
        ModBlockEntities.registerAllBlockEntities();
        ModAdvancementTriggers.registerAllTriggers();

        CustomizationProperties.registerAll();
        CustomizationCategories.registerAll(); // Categories must always go after items + properties.

        // Some stuff should be re-registered when a world loads I think? Dynamic registries are strange.
        ModEnchantments.registerRuntime();

        // Some things don't need "registering" but are static so they still
        // need loading. Load them now for reliability.
        ModMenus.bump();
        ModTags.bump();
        ModGamerules.bump();

        this.queueCommandRegistration();

        NetworkManager.registerAll();

        this.serverPhysicsObserver = new ServerPhysicsObserver();

        this.registerDataPacks();

        ServerTickEvents.START_SERVER_TICK.register(this.ticker::tick);

        ServerLifecycleEvents.SERVER_STARTING.register(server -> currentServerInstance = server);
        ServerLifecycleEvents.SERVER_STOPPED.register(server -> currentServerInstance = null);
    }

    private void initConfig() {
        GrappleModCommonConfig.HANDLER.defaults().saveDefaults();
        GrappleModCommonConfig.HANDLER.load();

        GrappleModCommonConfig.resetConfigFromServer(); // ensure that the config being used is the client-side one.
        //todo: ModItems.invalidateCreativeTabCache(); on save / reload.
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

        //todo: update simplified & no-enchants to use new vanilla enchant definitions.

        ModContainer container = cont.get();
        GrappleModUtils.registerPack("simplified", Component.translatable("pack.grapplemod.simplified"), container, ResourcePackActivationType.NORMAL);
        GrappleModUtils.registerPack("no_enchants", Component.translatable("pack.grapplemod.no_enchants"), container, ResourcePackActivationType.NORMAL);
        GrappleModUtils.registerPack("classic_recipes", Component.translatable("pack.grapplemod.classic_recipes"), container, ResourcePackActivationType.NORMAL);

        GrappleMod.LOGGER.info("All done with datapacks!");
    }

    public ServerPhysicsObserver getServerPhysicsObserver() {
        return this.serverPhysicsObserver;
    }

    public Ticker getTicker() {
        return this.ticker;
    }

    public ServerFeatures getServerFeatures() {
        return this.serverFeatures;
    }

    public static GrappleMod get() {
        return instance;
    }

    public static MinecraftServer getServer() {
        return currentServerInstance;
    }

    public static ResourceLocation id(String id) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, id);
    }

    /** @deprecated This just seems like a bad idea & a hack. */
    @Deprecated(since = "mc 1.21.1")
    public static ResourceLocation vanillaId(String id) {
        return ResourceLocation.fromNamespaceAndPath("minecraft", id);
    }

    public static Path getDefaultConfigPath() {
        return YACLPlatform.getConfigDir();
    }
}
