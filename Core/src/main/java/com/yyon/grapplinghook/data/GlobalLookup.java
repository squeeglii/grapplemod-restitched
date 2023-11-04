package com.yyon.grapplinghook.data;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Optional;

// Item names are hard to version -- they just apply across all versions.
public class GlobalLookup {

    private static HashMap<ResourceLocation, ResourceLocation> mappedItemIds = new HashMap<>();

    private static void mapId(String original, String newName) {
        mappedItemIds.put(
                GrappleMod.id(original),
                GrappleMod.id(newName)
        );
    }

    static {
        // Enchantments
        mapId("wallrunenchantment", "wall_running");
        mapId("doublejumpenchantment", "double_jump");
        mapId("slidingenchantment", "sliding");

        // Blocks / Block Entities
        mapId("block_grapple_modifier", "modification_table");

        // Items
        mapId("grapplinghook", "grappling_hook");
        mapId("launcheritem", "ender_staff");
        mapId("repeller", "forcefield");
        mapId("rocket", "rocket");
        mapId("baseupgradeitem", "base_upgrade");
        mapId("doubleupgradeitem", "double_hook_upgrade");
        mapId("forcefieldupgradeitem", "forcefield_upgrade");
        mapId("magnetupgradeitem", "magnet_upgrade");
        mapId("motorupgradeitem", "motor_upgrade");
        mapId("ropeupgradeitem", "rope_upgrade");
        mapId("staffupgradeitem", "ender_staff_upgrade");
        mapId("swingupgradeitem", "swing_upgrade");
        mapId("throwupgradeitem", "hook_thrower_upgrade");
        mapId("limitsupgradeitem", "limits_upgrade");
        mapId("rocketupgradeitem", "rocket_upgrade");
        mapId("longfallboots", "long_fall_boots");
    }



    public static Optional<ResourceLocation> getMappingFor(ResourceLocation originalItem) {
        if(!mappedItemIds.containsKey(originalItem))
            return Optional.empty();

        return Optional.of(mappedItemIds.get(originalItem));
    }

}
