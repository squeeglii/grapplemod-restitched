package com.yyon.grapplinghook;

import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.registry.GrappleModItems;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.ResourceLocation;
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
    public static final String MODID = "grapplemod";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        CommonSetup.BLOCKS.register(bus);
        GrappleModItems.registerAllItems();
        CommonSetup.ENTITY_TYPES.register(bus);
        CommonSetup.ENCHANTMENTS.register(bus);
        CommonSetup.BLOCK_ENTITY_TYPES.register(bus);
    }

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }
}
