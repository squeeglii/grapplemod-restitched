package com.yyon.grapplinghook.customization.template;

import com.yyon.grapplinghook.GrappleMod;
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

    private static <T> PropertyOverride<T> property(Entry<? extends CustomizationProperty<T>> id, T value) {
        if(id == null) throw new IllegalArgumentException("Identifier property entry cannot be null.");
        return property(id.get(), value);
    }

    private static <T> PropertyOverride<T> property(CustomizationProperty<T> id, T value) {
        if(id == null) throw new IllegalArgumentException("Identifier property cannot be null.");
        return new PropertyOverride<>(id, value);
    }

    public static Collection<GrapplingHookTemplate> getTemplates() {
        return Collections.unmodifiableCollection(defaultTemplates.values());
    }


    public static final GrapplingHookTemplate ENDER_HOOK = register(new GrapplingHookTemplate(
            "ender_hook",
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(ENDER_STAFF_ATTACHED, true)
    ));

    public static final GrapplingHookTemplate MOTOR_HOOK = register(new GrapplingHookTemplate(
            "motor_hook",
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MOTOR_ATTACHED, true),
            property(MOVE_SPEED_MULTIPLIER, 2.0d)
    ));

    public static final GrapplingHookTemplate SMART_HOOK = register(new GrapplingHookTemplate(
            "smart_hook",
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MOTOR_ATTACHED, true),
            property(SMART_MOTOR, true),
            property(MOVE_SPEED_MULTIPLIER, 2.0d)
    ));

    public static final GrapplingHookTemplate MAGNET_HOOK = register(new GrapplingHookTemplate(
            "magnet_hook",
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MAGNET_ATTACHED, true),
            property(FORCEFIELD_ATTACHED, true)
    ));

    public static final GrapplingHookTemplate ROCKET_HOOK = register(new GrapplingHookTemplate(
            "rocket_hook",
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(ROCKET_ATTACHED, true)
    ));

    public static final GrapplingHookTemplate DOUBLE_MOTOR_HOOK = register(new GrapplingHookTemplate(
            "double_motor_hook",
            property(HOOK_THROW_SPEED, 20.0d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(DOUBLE_HOOK_ATTACHED, true),
            property(MOTOR_ATTACHED, true),
            property(MAX_MOTOR_SPEED, 10.0d),
            property(STICKY_ROPE, true),

            property(HOOK_GRAVITY_MULTIPLIER, 50.0d),
            property(DOUBLE_HOOK_ANGLE, 30.0d),
            property(DOUBLE_HOOK_ANGLE_ON_SNEAK, 25.0d),
            property(HOOK_REEL_IN_ON_SNEAK, false),

            property(MOTOR_ACTIVATION, CrouchToggle.WHEN_NOT_CROUCHING),
            property(DOUBLE_SMART_MOTOR, true),

            property(HOOK_THROW_ANGLE, 25.0d),
            property(HOOK_THROW_ANGLE_ON_SNEAK, 0.0d),

            property(MOVE_SPEED_MULTIPLIER, 2.0d)
    ));

    public static final GrapplingHookTemplate DOUBLE_ROCKET_MOTOR_HOOK = register(new GrapplingHookTemplate(
            "double_rocket_motor_hook",
            property(HOOK_THROW_SPEED, 20.0d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(DOUBLE_HOOK_ATTACHED, true),
            property(MOTOR_ATTACHED, true),
            property(MAX_MOTOR_SPEED, 10.0d),
            property(STICKY_ROPE, true),

            property(HOOK_GRAVITY_MULTIPLIER, 50.0d),
            property(DOUBLE_HOOK_ANGLE, 30.0d),
            property(DOUBLE_HOOK_ANGLE_ON_SNEAK, 25.0d),
            property(HOOK_REEL_IN_ON_SNEAK, false),

            property(MOTOR_ACTIVATION, CrouchToggle.WHEN_NOT_CROUCHING),
            property(DOUBLE_SMART_MOTOR, true),

            property(HOOK_THROW_ANGLE, 25.0d),
            property(HOOK_THROW_ANGLE_ON_SNEAK, 0.0d),

            property(ROCKET_ATTACHED, true),
            property(ROCKET_ANGLE, 30.0d),

            property(MOVE_SPEED_MULTIPLIER, 2.0d)
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
