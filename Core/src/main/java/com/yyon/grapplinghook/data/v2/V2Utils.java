package com.yyon.grapplinghook.data.v2;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.customization.CustomizationCategory;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Optional;

public class V2Utils {


    // custom { "...": abc, "...": 123 }  ->
    // customization { properties: { "grapplemod:...": abc, "grapplemod:...": 123 }, crc32: 12345 }
    public static CompoundTag upgradeCustomizations(CompoundTag oldCustomizations) {
        if(oldCustomizations == null) return null;

        CustomizationVolume volume = new CustomizationVolume();

        oldCustomizations.getAllKeys().stream()
                .map(V2Lookups::customizationUpgraderFor)
                .forEach(propertyUpgrader -> {
                    propertyUpgrader.upgrade(oldCustomizations, volume);
                });

        return volume.writeToNBT();
    }

    public static CompoundTag upgradeCategoryUnlocks(CompoundTag oldUnlocks) {
        if(oldUnlocks == null) return null;

        CompoundTag newUnlocks = new CompoundTag();

        for(String key: oldUnlocks.getAllKeys()) {

            if(!oldUnlocks.contains(key, Tag.TAG_BYTE)) {
                GrappleMod.LOGGER.warn("Non-boolean value found in v0 Modification Table category unlocks!");
                continue;
            }

            boolean isUnlocked = oldUnlocks.getBoolean(key);

            // New behaviour skips categories that aren't unlocked.
            if(!isUnlocked)
                continue;

            int id;

            try {
                id = Integer.parseInt(key);
            } catch (NumberFormatException err) {
                GrappleMod.LOGGER.warn("Non-Integer key found in v0 Modification Table category unlocks!");
                continue;
            }

            Optional<CustomizationCategory> category = V2Lookups.categoryKnownAs(id);

            if(category.isEmpty()) {
                GrappleMod.LOGGER.warn(
                        "Unknown category id found in v0 Modification Table unlocks (what is %s?)".formatted(
                                id
                ));
                continue;
            }

            String identifier = category.get().getIdentifier().toString();
            newUnlocks.putBoolean(identifier, true);
        }

        return newUnlocks;
    }

}
