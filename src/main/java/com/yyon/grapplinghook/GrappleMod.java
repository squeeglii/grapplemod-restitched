package com.yyon.grapplinghook;

import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.content.registry.*;
import com.yyon.grapplinghook.physics.ServerPhysicsObserver;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

//TODO
// Pull mobs
// Attach 2 things together
// wallrun on diagonal walls
// smart motor acts erratically when aiming above hook
// key events

public class GrappleMod implements ModInitializer {

    public static final String MOD_ID = "grapplemod";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {

        try {
            this.initConfig();
        } catch (Exception e) {
            LOGGER.info(e);
        }

        GrappleModBlocks.registerAllBlocks();
        GrappleModItems.registerAllItems();  // Items must always be registered after blocks.
        GrappleModEntities.registerAllEntities();
        GrappleModEnchantments.registerAllEnchantments();
        GrappleModBlockEntities.registerAllBlockEntities();
        GrappleModAdvancementTriggers.registerAllTriggers();

        GrappleModCustomizationProperties.registerAll();
        GrappleModCustomizationCategories.registerAll(); // Categories must always go after items + properties.

        NetworkManager.registerPacketListeners();
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


    public static ResourceLocation id(String id) {
        return new ResourceLocation(MOD_ID, id);
    }

    public static ResourceLocation fakeId(String id) {
        return new ResourceLocation("minecraft", id);
    }
}
