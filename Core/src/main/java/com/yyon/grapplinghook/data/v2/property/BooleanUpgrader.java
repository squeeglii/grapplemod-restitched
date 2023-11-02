package com.yyon.grapplinghook.data.v2.property;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class BooleanUpgrader extends PropertyUpgrader<Boolean> {

    public BooleanUpgrader(String oldId, CustomizationProperty<Boolean> newProperty) {
        super(oldId, newProperty);
    }

    @Override
    public void upgrade(CompoundTag source, CustomizationVolume destination) {
        if(!source.contains(this.oldId, Tag.TAG_BYTE)) return;

        boolean val = source.getBoolean(this.oldId);
        destination.set(this.newProperty, val);
    }
}
