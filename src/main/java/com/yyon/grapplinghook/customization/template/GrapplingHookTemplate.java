package com.yyon.grapplinghook.customization.template;

import com.yyon.grapplinghook.content.registry.GrappleModItems;
import com.yyon.grapplinghook.customization.CustomizationAvailability;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.CrouchToggle;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.*;

// These mimic the old recipes, automatically checking if a given template is valid.
public class GrapplingHookTemplate {

    private static final Map<String, GrapplingHookTemplate> defaultTemplates = new LinkedHashMap<>();

    private static GrapplingHookTemplate register(GrapplingHookTemplate template) {
        GrapplingHookTemplate.defaultTemplates.put(template.getId().toLowerCase(), template);
        return template;
    }

    private static <T> PropertyOverride<T> property(CustomizationProperty<T> id, T value) {
        return new PropertyOverride<>(id, value);
    }

    public static Collection<GrapplingHookTemplate> getTemplates() {
        return Collections.unmodifiableCollection(defaultTemplates.values());
    }


    public static final GrapplingHookTemplate ENDER_HOOK = register(new GrapplingHookTemplate(
            "ender_hook",
            property(HOOK_THROW_SPEED.get(), 3.5d),
            property(MAX_ROPE_LENGTH.get(), 60.0d),

            property(ENDER_STAFF_ATTACHED.get(), true)
    ));

    public static final GrapplingHookTemplate MOTOR_HOOK = register(new GrapplingHookTemplate(
            "motor_hook",
            property(HOOK_THROW_SPEED.get(), 3.5d),
            property(MAX_ROPE_LENGTH.get(), 60.0d),

            property(MOTOR_ATTACHED.get(), true),
            property(MOVE_SPEED_MULTIPLIER.get(), 2.0d)
    ));

    public static final GrapplingHookTemplate SMART_HOOK = register(new GrapplingHookTemplate(
            "smart_hook",
            property(HOOK_THROW_SPEED.get(), 3.5d),
            property(MAX_ROPE_LENGTH.get(), 60.0d),

            property(MOTOR_ATTACHED.get(), true),
            property(SMART_MOTOR.get(), true),
            property(MOVE_SPEED_MULTIPLIER.get(), 2.0d)
    ));

    public static final GrapplingHookTemplate MAGNET_HOOK = register(new GrapplingHookTemplate(
            "magnet_hook",
            property(HOOK_THROW_SPEED.get(), 3.5d),
            property(MAX_ROPE_LENGTH.get(), 60.0d),

            property(MAGNET_ATTACHED.get(), true),
            property(FORCEFIELD_ATTACHED.get(), true)
    ));

    public static final GrapplingHookTemplate ROCKET_HOOK = register(new GrapplingHookTemplate(
            "rocket_hook",
            property(HOOK_THROW_SPEED.get(), 3.5d),
            property(MAX_ROPE_LENGTH.get(), 60.0d),

            property(ROCKET_ATTACHED.get(), true)
    ));

    public static final GrapplingHookTemplate DOUBLE_MOTOR_HOOK = register(new GrapplingHookTemplate(
            "double_motor_hook",
            property(HOOK_THROW_SPEED.get(), 20.0d),
            property(MAX_ROPE_LENGTH.get(), 60.0d),

            property(DOUBLE_HOOK_ATTACHED.get(), true),
            property(MOTOR_ATTACHED.get(), true),
            property(MAX_MOTOR_SPEED.get(), 10.0d),
            property(STICKY_ROPE.get(), true),

            property(HOOK_GRAVITY_MULTIPLIER.get(), 50.0d),
            property(DOUBLE_HOOK_ANGLE.get(), 30.0d),
            property(DOUBLE_HOOK_ANGLE_ON_SNEAK.get(), 25.0d),
            property(HOOK_REEL_IN_ON_SNEAK.get(), false),

            property(MOTOR_ACTIVATION.get(), CrouchToggle.WHEN_NOT_CROUCHING),
            property(DOUBLE_SMART_MOTOR.get(), true),

            property(HOOK_THROW_ANGLE.get(), 25.0d),
            property(HOOK_THROW_ANGLE_ON_SNEAK.get(), 0.0d),

            property(MOVE_SPEED_MULTIPLIER.get(), 2.0d)
    ));

    public static final GrapplingHookTemplate DOUBLE_ROCKET_MOTOR_HOOK = register(new GrapplingHookTemplate(
            "double_rocket_motor_hook",
            property(HOOK_THROW_SPEED.get(), 20.0d),
            property(MAX_ROPE_LENGTH.get(), 60.0d),

            property(DOUBLE_HOOK_ATTACHED.get(), true),
            property(MOTOR_ATTACHED.get(), true),
            property(MAX_MOTOR_SPEED.get(), 10.0d),
            property(STICKY_ROPE.get(), true),

            property(HOOK_GRAVITY_MULTIPLIER.get(), 50.0d),
            property(DOUBLE_HOOK_ANGLE.get(), 30.0d),
            property(DOUBLE_HOOK_ANGLE_ON_SNEAK.get(), 25.0d),
            property(HOOK_REEL_IN_ON_SNEAK.get(), false),

            property(MOTOR_ACTIVATION.get(), CrouchToggle.WHEN_NOT_CROUCHING),
            property(DOUBLE_SMART_MOTOR.get(), true),

            property(HOOK_THROW_ANGLE.get(), 25.0d),
            property(HOOK_THROW_ANGLE_ON_SNEAK.get(), 0.0d),

            property(ROCKET_ATTACHED.get(), true),
            property(ROCKET_ANGLE.get(), 30.0d),

            property(MOVE_SPEED_MULTIPLIER.get(), 2.0d)
    ));


    private final String identifier;
    private final Set<PropertyOverride<?>> properties;

    private GrapplingHookTemplate(String identifier, PropertyOverride<?>... properties) {
        this.identifier = identifier;
        this.properties = Set.of(properties);
    }

    public String getId() {
        return identifier;
    }

    public boolean isEnabled() {
        return properties.stream()
                .map(PropertyOverride::property)
                .noneMatch(p -> p.getAvailability() == CustomizationAvailability.BLOCKED); // 2 = Disabled Fully.
    }

    public CustomizationVolume getCustomizations() {
        CustomizationVolume customization = new CustomizationVolume();
        properties.forEach(customization::set);
        return customization;
    }

    public ItemStack getAsStack() {
        ItemStack itemStack = GrappleModItems.GRAPPLING_HOOK.get().getDefaultInstance();
        GrappleModItems.GRAPPLING_HOOK.get().setCustomOnServer(itemStack, this.getCustomizations());

        return itemStack;
    }
}
