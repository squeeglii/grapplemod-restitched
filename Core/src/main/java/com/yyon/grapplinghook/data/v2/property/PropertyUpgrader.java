package com.yyon.grapplinghook.data.v2.property;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.nbt.CompoundTag;

public abstract class PropertyUpgrader<T> {

    protected final String oldId;
    protected final CustomizationProperty<T> newProperty;


    public PropertyUpgrader(String oldId, CustomizationProperty<T> newProperty) {
        this.oldId = oldId;
        this.newProperty = newProperty;
    }


    public abstract void upgrade(CompoundTag source, CustomizationVolume destination);

    public String getLegacyId() {
        return this.oldId;
    }

    public CustomizationProperty<T> getNewProperty() {
        return this.newProperty;
    }
}
