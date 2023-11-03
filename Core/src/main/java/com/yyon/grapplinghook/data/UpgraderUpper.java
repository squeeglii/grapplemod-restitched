package com.yyon.grapplinghook.data;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.data.v2.V2Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Optional;

// Replace with data-fixer-upper when that's figured out.
// 0 = original version, no data version marked. Tread with caution there.
public class UpgraderUpper {

    public static final int CURRENT_DATA_VERSION = 2;

    public static String MOD_DATA_VER_LOCATION = "mod_data_model";
    public static String GRAPPLEMOD_VERSION_NAME = "grapplemod";

    public static int findVersionInTag(CompoundTag tag) {
        if(!tag.contains(MOD_DATA_VER_LOCATION, Tag.TAG_COMPOUND))
            return 0;

        CompoundTag versionsTag = tag.getCompound(MOD_DATA_VER_LOCATION);

        if(!versionsTag.contains(GRAPPLEMOD_VERSION_NAME, Tag.TAG_INT))
            return 0;

        return versionsTag.getInt(GRAPPLEMOD_VERSION_NAME);
    }


    public static void setLatestVersionInTag(CompoundTag tag) {
        UpgraderUpper.setVersionInTag(tag, CURRENT_DATA_VERSION);
    }


    public static void setVersionInTag(CompoundTag tag, int version) {
        if(!tag.contains(MOD_DATA_VER_LOCATION, Tag.TAG_COMPOUND)) {
            tag.put(MOD_DATA_VER_LOCATION, new CompoundTag());
        }

        CompoundTag versionsTag = tag.getCompound(MOD_DATA_VER_LOCATION);
        versionsTag.putInt(GRAPPLEMOD_VERSION_NAME, version);
    }


    public static Optional<CompoundTag> upgradeGrapplingHook(CompoundTag tagIn) {
        int verIn = UpgraderUpper.findVersionInTag(tagIn);

        // Hooks without customizations don't really have a data version
        // If the data version is missing (0) and it doesn't match the format of the forge / fabric 1.x hooks, skip.
        if(verIn <= 0 && !tagIn.contains("custom"))
            return Optional.empty();

        // Move it to 'upgrade paths' if you actually add more data versions.
        // v0 -> v2
        if(verIn == 0) {
            GrappleMod.LOGGER.info("Upgrading Grappling Hook item from v0 (forge/1.x fabric) --> v2");

            if(tagIn.contains("custom", Tag.TAG_COMPOUND)) {
                CompoundTag oldCustom = tagIn.getCompound("custom");
                CompoundTag newCustom = V2Utils.upgradeCustomizations(oldCustom);
                tagIn.remove("custom");
                tagIn.put("customization", newCustom);
            }

            UpgraderUpper.setVersionInTag(tagIn, 2);
            return Optional.of(tagIn);
        }

        return Optional.empty();
    }

}
