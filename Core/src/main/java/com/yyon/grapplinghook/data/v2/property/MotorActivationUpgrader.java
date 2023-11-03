package com.yyon.grapplinghook.data.v2.property;

import com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.CrouchToggle;
import com.yyon.grapplinghook.customization.type.EnumProperty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class MotorActivationUpgrader extends PropertyUpgrader<CrouchToggle> {

    private final CrouchToggle valueToSet;

    private MotorActivationUpgrader(String oldId, CrouchToggle toggle, EnumProperty<CrouchToggle> newProperty) {
        super(oldId, newProperty);
        this.valueToSet = toggle;
    }

    // This whole upgrade is awkward as two booleans need to be merged into one 3-state enum, all
    // while dealing with a default value provided by the new implementation.
    @Override
    public void upgrade(CompoundTag source, CustomizationVolume destination) {
        boolean hasValSet = destination.has(this.newProperty);

        // The convenience of get returning a default is annoying here - set it to null if it's not explicitly
        // been assigned.
        CrouchToggle currentVal = hasValSet
                ? destination.get(this.newProperty)
                : null;

        if(!source.contains(this.oldId, Tag.TAG_BYTE))
            return;

        boolean isOldTrue = source.getBoolean(this.oldId);
        if(!isOldTrue)
            return;

        if(currentVal == null) {
            destination.set(this.newProperty, this.valueToSet);
            return;
        }

        // Being lazy and not checking if it's the opposite (crouching vs not_crouching)
        // If the property is already set, that probably means it was the opposite that was set anyway,
        // so they can be merged into "always".
        destination.set(this.newProperty, CrouchToggle.ALWAYS);
    }


    public static MotorActivationUpgrader whenCrouching() {
        return new MotorActivationUpgrader(
                "motorwhencrouching",
                CrouchToggle.WHEN_CROUCHING,
                GrappleModCustomizationProperties.MOTOR_ACTIVATION.get()
        );
    }

    public static MotorActivationUpgrader whenNotCrouching() {
        return new MotorActivationUpgrader(
                "motorwhennotcrouching",
                CrouchToggle.WHEN_NOT_CROUCHING,
                GrappleModCustomizationProperties.MOTOR_ACTIVATION.get()
        );
    }

}
