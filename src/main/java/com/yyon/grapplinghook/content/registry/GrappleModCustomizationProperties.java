package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import com.yyon.grapplinghook.customization.type.BooleanProperty;
import com.yyon.grapplinghook.customization.type.CrouchToggle;
import com.yyon.grapplinghook.customization.type.DoubleProperty;
import com.yyon.grapplinghook.customization.type.EnumProperty;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GrappleModCustomizationProperties {

    private static final HashMap<ResourceLocation, Entry<?>> properties;

    static {
        properties = new HashMap<>();
    }

    public static <P extends CustomizationProperty<?>> Entry<P> property(String id, Supplier<P> type) {
        ResourceLocation qualId = GrappleMod.id(id);
        Entry<P> entry = new Entry<>(qualId, type);
        properties.put(qualId, entry);
        return entry;
    }


    public static void registerAll() {
        for(Map.Entry<ResourceLocation, Entry<?>> def: properties.entrySet()) {
            ResourceLocation id = def.getKey();
            Entry<?> data = def.getValue();
            CustomizationProperty<?> it = data.getFactory().get();

            data.finalize(Registry.register(GrappleModMetaRegistry.CUSTOMIZATION_PROPERTIES, id, it));
        }
    }

    //TODO: Figure out what to do with categories and items.


    public static final Entry<DoubleProperty> MAX_ROPE_LENGTH = property("max_rope_length", () -> new DoubleProperty(30.0D, 0.0D, 60.0D));
    public static final Entry<BooleanProperty> BLOCK_PHASE_ROPE = property("block_phasing_rope", () -> new BooleanProperty(false));
    public static final Entry<BooleanProperty> STICKY_ROPE = property("sticky_rope", () -> new BooleanProperty(false));

    public static final Entry<DoubleProperty> HOOK_GRAVITY_MULTIPLIER = property("hook_gravity_multiplier", () -> new DoubleProperty(1.0D, 1.0D, 100.0D));
    public static final Entry<DoubleProperty> HOOK_THROW_SPEED = property("hook_throw_speed", () -> new DoubleProperty(2.0D, 0.0D, 5.0D));
    public static final Entry<BooleanProperty> HOOK_REEL_IN_ON_SNEAK = property("hook_reel_in_on_sneak", () -> new BooleanProperty(true));
    public static final Entry<DoubleProperty> HOOK_THROW_ANGLE = property("hook_throw_angle", () -> new DoubleProperty(0.0D, 0.0D, 45.0D));
    public static final Entry<DoubleProperty> HOOK_THROW_ANGLE_ON_SNEAK = property("hook_throw_angle_on_sneak", () -> new DoubleProperty(0.0D, 0.0D, 45.0D));
    public static final Entry<BooleanProperty> DETACH_HOOK_ON_KEY_UP = property("detach_hook_on_key_up", () -> new BooleanProperty(false));

    public static final Entry<BooleanProperty> MOTOR_ATTACHED = property("motor", () -> new BooleanProperty(false));
    public static final Entry<DoubleProperty> MOTOR_ACCELERATION = property("motor_acceleration", () -> new DoubleProperty(0.2D, 0.0D, 0.2D));
    public static final Entry<DoubleProperty> MAX_MOTOR_SPEED = property("max_motor_speed", () -> new DoubleProperty(4.0D, 0.0D, 4.0D));
    public static final Entry<EnumProperty<CrouchToggle>> MOTOR_ACTIVATION = property("motor_activation", () -> new EnumProperty<>(CrouchToggle.WHEN_NOT_CROUCHING, CrouchToggle.values()));
    public static final Entry<BooleanProperty> SMART_MOTOR = property("smart_motor", () -> new BooleanProperty(false));
    public static final Entry<BooleanProperty> MOTOR_DAMPENER = property("motor_dampener", () -> new BooleanProperty(false)); //TODO: Only available with limits.
    public static final Entry<BooleanProperty> MOTOR_WORKS_BACKWARDS = property("motor_works_backwards", () -> new BooleanProperty(true));

    public static final Entry<DoubleProperty> MOVE_SPEED_MULTIPLIER = property("holder_move_speed_multiplier", () -> new DoubleProperty(1.0D, 0.0D, 2.0D));

    public static final Entry<BooleanProperty> ENDER_STAFF_ATTACHED = property("ender_staff", () -> new BooleanProperty(false));

    public static final Entry<BooleanProperty> FORCEFIELD_ATTACHED = property("forcefield", () -> new BooleanProperty(false));
    public static final Entry<DoubleProperty> FORCEFIELD_FORCE = property("forcefield_repel_force", () -> new DoubleProperty(1.0D, 0.0D, 1.0D));

    public static final Entry<BooleanProperty> MAGNET_ATTACHED = property("magnet", () -> new BooleanProperty(false));
    public static final Entry<DoubleProperty> MAGNET_RADIUS = property("magnet_attract_radius", () -> new DoubleProperty(3.0D, 0.0D, 3.0D));

    public static final Entry<BooleanProperty> DOUBLE_HOOK_ATTACHED = property("double_hook", () -> new BooleanProperty(false));
    public static final Entry<BooleanProperty> DOUBLE_SMART_MOTOR = property("double_smart_motor", () -> new BooleanProperty(true));
    public static final Entry<DoubleProperty> DOUBLE_HOOK_ANGLE = property("double_hook_angle", () -> new DoubleProperty(20.0D, 0.0D, 45.0D));
    public static final Entry<DoubleProperty> DOUBLE_HOOK_ANGLE_ON_SNEAK = property("double_hook_angle_on_sneak", () -> new DoubleProperty(10.0D, 0.0D, 45.0D));
    public static final Entry<BooleanProperty> SINGLE_ROPE_PULL = property("single_rope_pull", () -> new BooleanProperty(false));

    public static final Entry<BooleanProperty> ROCKET_ATTACHED = property("rocket", () -> new BooleanProperty(false));
    public static final Entry<DoubleProperty> ROCKET_FORCE = property("rocket_force", () -> new DoubleProperty(1.0D, 0.0D, 1.0D));
    public static final Entry<DoubleProperty> ROCKET_FUEL_DEPLETION_RATIO = property("rocket_depletion_ratio", () -> new DoubleProperty(0.5D, 0.0D, 0.5D));
    public static final Entry<DoubleProperty> ROCKET_REFUEL_RATIO = property("rocket_refuel_ratio", () -> new DoubleProperty(15.0D, 15.0D, 30.0D));
    public static final Entry<DoubleProperty> ROCKET_ANGLE = property("double_hook_angle", () -> new DoubleProperty(0.0D, 0.0D, 90.0D));


    public static class Entry<T extends CustomizationProperty<?>> extends AbstractRegistryReference<T> {

        protected Entry(ResourceLocation id, Supplier<T> factory) {
            super(id, factory);
        }
    }


}
