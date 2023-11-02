package com.yyon.grapplinghook.data.v2.property;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class DoubleUpgrader extends PropertyUpgrader<Double> {

    public DoubleUpgrader(String oldId, CustomizationProperty<Double> newProperty) {
        super(oldId, newProperty);
    }

    @Override
    public void upgrade(CompoundTag source, CustomizationVolume destination) {
        if(!source.contains(this.oldId, Tag.TAG_DOUBLE)) return;

        double val = source.getDouble(this.oldId);
        destination.set(this.newProperty, val);
    }

}
