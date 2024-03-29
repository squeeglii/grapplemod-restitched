package com.yyon.grapplinghook.data.v2;

import com.yyon.grapplinghook.customization.CustomizationCategory;
import com.yyon.grapplinghook.data.v2.property.*;

import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.*;
import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationCategories.*;

import java.util.HashMap;
import java.util.Optional;

public class V2Lookups {

    private static final MissingUpgrader MISSING_UPGRADER = new MissingUpgrader();


    private static final HashMap<String, PropertyUpgrader<?>> customizationUpgraders = new HashMap<>();
    private static final CustomizationCategory[] orderedCategories = new CustomizationCategory[] {
            ROPE.get(), HOOK_THROWER.get(), MOTOR.get(),
            SWING.get(), ENDER_STAFF.get(), FORCEFIELD.get(),
            MAGNET.get(), DOUBLE_HOOK.get(), LIMITS.get(),
            ROCKET.get()
    };

    private static void mapCustomization(PropertyUpgrader<?> upgrader) {
        customizationUpgraders.put(upgrader.getLegacyId(), upgrader);
    }


    static {
        mapCustomization(new BooleanUpgrader("phaserope", BLOCK_PHASE_ROPE.get()));
        mapCustomization(new BooleanUpgrader("motor", MOTOR_ATTACHED.get()));
        mapCustomization(new BooleanUpgrader("smartmotor", SMART_MOTOR.get()));
        mapCustomization(new BooleanUpgrader("enderstaff", ENDER_STAFF_ATTACHED.get()));
        mapCustomization(new BooleanUpgrader("repel", FORCEFIELD_ATTACHED.get()));
        mapCustomization(new BooleanUpgrader("attract", MAGNET_ATTACHED.get()));
        mapCustomization(new BooleanUpgrader("doublehook", DOUBLE_HOOK_ATTACHED.get()));
        mapCustomization(new BooleanUpgrader("smartdoublemotor", DOUBLE_SMART_MOTOR.get()));
        mapCustomization(new BooleanUpgrader("motordampener", MOTOR_DAMPENER.get()));
        mapCustomization(new BooleanUpgrader("reelin", HOOK_REEL_IN_ON_SNEAK.get()));
        mapCustomization(new BooleanUpgrader("pullbackwards", MOTOR_WORKS_BACKWARDS.get()));
        mapCustomization(new BooleanUpgrader("oneropepull", SINGLE_ROPE_PULL.get()));
        mapCustomization(new BooleanUpgrader("sticky", STICKY_ROPE.get()));
        mapCustomization(new BooleanUpgrader("detachonkeyrelease", DETACH_HOOK_ON_KEY_UP.get()));
        mapCustomization(new BooleanUpgrader("rocket", ROCKET_ATTACHED.get()));

        mapCustomization(new DoubleUpgrader("maxlen", MAX_ROPE_LENGTH.get()));
        mapCustomization(new DoubleUpgrader("hookgravity", HOOK_GRAVITY_MULTIPLIER.get()));
        mapCustomization(new DoubleUpgrader("throwspeed", HOOK_THROW_SPEED.get()));
        mapCustomization(new DoubleUpgrader("motormaxspeed", MAX_MOTOR_SPEED.get()));
        mapCustomization(new DoubleUpgrader("motoracceleration", MOTOR_ACCELERATION.get()));
        mapCustomization(new DoubleUpgrader("playermovementmult", MOVE_SPEED_MULTIPLIER.get()));
        mapCustomization(new DoubleUpgrader("repelforce", FORCEFIELD_FORCE.get()));
        mapCustomization(new DoubleUpgrader("attractradius", MAGNET_RADIUS.get()));
        mapCustomization(new DoubleUpgrader("angle", DOUBLE_HOOK_ANGLE.get()));
        mapCustomization(new DoubleUpgrader("sneakingangle", DOUBLE_HOOK_ANGLE_ON_SNEAK.get()));
        mapCustomization(new DoubleUpgrader("verticalthrowangle", HOOK_THROW_ANGLE.get()));
        mapCustomization(new DoubleUpgrader("sneakingverticalthrowangle", HOOK_THROW_ANGLE_ON_SNEAK.get()));
        mapCustomization(new DoubleUpgrader("rocket_force", ROCKET_FORCE.get()));
        mapCustomization(new DoubleUpgrader("rocket_active_time", ROCKET_FUEL_DEPLETION_RATIO.get()));
        mapCustomization(new DoubleUpgrader("rocket_refuel_ratio", ROCKET_REFUEL_RATIO.get()));
        mapCustomization(new DoubleUpgrader("rocket_vertical_angle", ROCKET_ANGLE.get()));

        // Special upgraders - have to merge two properties into one.
        mapCustomization(MotorActivationUpgrader.whenCrouching());
        mapCustomization(MotorActivationUpgrader.whenNotCrouching());
    }



    public static PropertyUpgrader<?> customizationUpgraderFor(String id) {
        return customizationUpgraders.getOrDefault(id.toLowerCase().trim(), MISSING_UPGRADER);
    }

    public static Optional<CustomizationCategory> categoryKnownAs(int oldId) {
        return oldId < orderedCategories.length
                ? Optional.of(orderedCategories[oldId])
                : Optional.empty();
    }

}
