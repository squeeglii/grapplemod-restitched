package com.yyon.grapplinghook.data.v2.property;

import com.yyon.grapplinghook.customization.data.HookCustomization;
import net.minecraft.nbt.CompoundTag;

import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.FAILED_DATA_UPGRADE;

public class MissingUpgrader extends PropertyUpgrader<Integer> {

    public MissingUpgrader() {
        super("???", FAILED_DATA_UPGRADE.get());
    }

    @Override
    public void upgrade(CompoundTag source, HookCustomization destination) {
        int counter = destination.get(FAILED_DATA_UPGRADE.get());
        destination.set(FAILED_DATA_UPGRADE.get(), counter);
    }

}
