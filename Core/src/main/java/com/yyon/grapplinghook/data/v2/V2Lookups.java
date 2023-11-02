package com.yyon.grapplinghook.data.v2;

import com.yyon.grapplinghook.data.v2.property.BooleanUpgrader;
import com.yyon.grapplinghook.data.v2.property.DoubleUpgrader;
import com.yyon.grapplinghook.data.v2.property.MissingUpgrader;
import com.yyon.grapplinghook.data.v2.property.PropertyUpgrader;
import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.*;

import java.util.HashMap;

public class V2Lookups {

    private static final MissingUpgrader MISSING_UPGRADER = new MissingUpgrader();


    private static HashMap<String, PropertyUpgrader<?>> customizationUpgraders = new HashMap<>();

    private static void mapCustomization(PropertyUpgrader<?> upgrader) {
        customizationUpgraders.put(upgrader.getLegacyId(), upgrader);
    }


    static {
        //todo: upgrades for: motorwhencrouching & motorwhennotcrouching

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

    }



    public static PropertyUpgrader<?> customizationUpgraderFor(String id) {
        return customizationUpgraders.getOrDefault(id.toLowerCase().trim(), MISSING_UPGRADER);
    }

}
