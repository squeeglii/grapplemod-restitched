package com.yyon.grapplinghook.data.v2;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

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

}
