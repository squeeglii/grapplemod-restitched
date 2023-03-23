package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.customization.CustomizationCategory;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.*;

public class GrappleModCustomizationCategories {

    private static final HashMap<ResourceLocation, Entry<?>> categories;

    static {
        categories = new HashMap<>();
    }

    public static <P extends CustomizationCategory> Entry<P> category(String id, Supplier<P> type) {
        ResourceLocation qualId = GrappleMod.id(id);
        Entry<P> entry = new Entry<>(qualId, type);
        categories.put(qualId, entry);
        return entry;
    }


    public static void registerAll() {
        for(Map.Entry<ResourceLocation, Entry<?>> def: categories.entrySet()) {
            ResourceLocation id = def.getKey();
            Entry<?> data = def.getValue();
            CustomizationCategory it = data.getFactory().get();

            data.finalize(Registry.register(GrappleModMetaRegistry.CUSTOMIZATION_CATEGORIES, id, it));
        }
    }

    // rename to reinforcement?
    public static final Entry<CustomizationCategory> LIMITS = category("limits", () -> new CustomizationCategory(
            GrappleModItems.LIMITS_UPGRADE.get()
    ));

    public static final Entry<CustomizationCategory> ROPE = category("rope", () -> new CustomizationCategory(
            GrappleModItems.ROPE_UPGRADE.get(),
            MAX_ROPE_LENGTH.get(), BLOCK_PHASE_ROPE.get(), STICKY_ROPE.get()
    ));

    // TODO: Group gravity with playermovementmult in a physics tab maybe?
    public static final Entry<CustomizationCategory> HOOK_THROWER = category("hook_thrower", () -> new CustomizationCategory(
            GrappleModItems.THROW_UPGRADE.get(),
            HOOK_GRAVITY_MULTIPLIER.get(), HOOK_THROW_SPEED.get(), HOOK_THROW_ANGLE.get(), HOOK_THROW_ANGLE_ON_SNEAK.get(),
            HOOK_REEL_IN_ON_SNEAK.get(), DETACH_HOOK_ON_KEY_UP.get()
    ));

    public static final Entry<CustomizationCategory> MOTOR = category("motor", () -> new CustomizationCategory(
            GrappleModItems.MOTOR_UPGRADE.get(),
            MOTOR_ATTACHED.get(), MOTOR_ACCELERATION.get(), MAX_MOTOR_SPEED.get(), MOTOR_ACTIVATION.get(),
            SMART_MOTOR.get(), MOTOR_DAMPENER.get(), MOTOR_WORKS_BACKWARDS.get()
    ));

    public static final Entry<CustomizationCategory> SWING = category("swing", () -> new CustomizationCategory(
            GrappleModItems.SWING_UPGRADE.get(),
            MOVE_SPEED_MULTIPLIER.get()
    ));

    public static final Entry<CustomizationCategory> ENDER_STAFF = category("ender_staff", () -> new CustomizationCategory(
            GrappleModItems.ENDER_STAFF_UPGRADE.get(),
            ENDER_STAFF_ATTACHED.get()
    ));

    public static final Entry<CustomizationCategory> FORCEFIELD = category("forcefield", () -> new CustomizationCategory(
            GrappleModItems.FORCE_FIELD_UPGRADE.get(),
            FORCEFIELD_ATTACHED.get(), FORCEFIELD_FORCE.get()
    ));

    public static final Entry<CustomizationCategory> MAGNET = category("magnet", () -> new CustomizationCategory(
            GrappleModItems.MAGNET_UPGRADE.get(),
            MAGNET_ATTACHED.get(), MAGNET_RADIUS.get()
    ));

    public static final Entry<CustomizationCategory> DOUBLE_HOOK = category("double_hook", () -> new CustomizationCategory(
            GrappleModItems.DOUBLE_UPGRADE.get(),
            DOUBLE_HOOK_ATTACHED.get(), DOUBLE_SMART_MOTOR.get(), SINGLE_ROPE_PULL.get(),
            DOUBLE_HOOK_ANGLE.get(), DOUBLE_HOOK_ANGLE_ON_SNEAK.get()
    ));

    public static final Entry<CustomizationCategory> ROCKET = category("rocket", () -> new CustomizationCategory(
            GrappleModItems.ROCKET_UPGRADE.get(),
            ROCKET_ATTACHED.get(), ROCKET_FORCE.get(), ROCKET_ANGLE.get(),
            ROCKET_FUEL_DEPLETION_RATIO.get(), ROCKET_REFUEL_RATIO.get()
    ));


    public static class Entry<T extends CustomizationCategory> extends AbstractRegistryReference<T> {

        protected Entry(ResourceLocation id, Supplier<T> factory) {
            super(id, factory);
        }
    }


}
