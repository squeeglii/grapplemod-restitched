package com.yyon.grapplinghook.content.customization;

import com.yyon.grapplinghook.content.item.GrapplehookItem;
import com.yyon.grapplinghook.content.registry.internal.ModItems;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.content.customization.data.TemplateAuthor;
import com.yyon.grapplinghook.content.customization.helper.PropertyOverride;
import com.yyon.grapplinghook.content.customization.type.enums.CrouchToggle;
import com.yyon.grapplinghook.content.customization.type.CustomizationProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static com.yyon.grapplinghook.content.registry.CustomizationProperties.*;

// These mimic the old recipes, automatically checking if a given template is valid.
public class HookTemplates {

    public static final Component INTERNAL_AUTHOR = Component.translatable("grapple_template.author.default");

    private static final Map<String, Template> defaultTemplates = new LinkedHashMap<>();

    private static Template registerDefault(Template template) {
        HookTemplates.defaultTemplates.put(template.metadata.templateId().toLowerCase(), template);
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

    public static Collection<Template> getTemplates() {
        return Collections.unmodifiableCollection(defaultTemplates.values());
    }


    public static final Template ENDER_HOOK = registerDefault(new Template(
            "ender_hook", Component.translatable("hook_template.grapplemod.ender_hook"), INTERNAL_AUTHOR,
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(ENDER_STAFF_ATTACHED, true)
    ));

    public static final Template MOTOR_HOOK = registerDefault(new Template(
            "motor_hook", Component.translatable("hook_template.grapplemod.motor_hook"), INTERNAL_AUTHOR,
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MOTOR_ATTACHED, true),
            property(MOVE_SPEED_MULTIPLIER, 2.0d)
    ));

    public static final Template SMART_HOOK = registerDefault(new Template(
            "smart_hook", Component.translatable("hook_template.grapplemod.smart_hook"), INTERNAL_AUTHOR,
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MOTOR_ATTACHED, true),
            property(SMART_MOTOR, true),
            property(MOVE_SPEED_MULTIPLIER, 2.0d)
    ));

    public static final Template MAGNET_HOOK = registerDefault(new Template(
            "magnet_hook", Component.translatable("hook_template.grapplemod.magnet_hook"), INTERNAL_AUTHOR,
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MAGNET_ATTACHED, true),
            property(FORCEFIELD_ATTACHED, true)
    ));

    public static final Template ROCKET_HOOK = registerDefault(new Template(
            "rocket_hook", Component.translatable("hook_template.grapplemod.rocket_hook"), INTERNAL_AUTHOR,
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(ROCKET_ATTACHED, true)
    ));

    public static final Template DOUBLE_MOTOR_HOOK = registerDefault(new Template(
            "double_motor_hook", Component.translatable("hook_template.grapplemod.double_motor_hook"), INTERNAL_AUTHOR,
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

    public static final Template DOUBLE_ROCKET_MOTOR_HOOK = registerDefault(new Template(
            "double_rocket_motor_hook", Component.translatable("hook_template.grapplemod.double_rocket_motor_hook"), INTERNAL_AUTHOR,
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

    public static class Template {
        private final TemplateAuthor metadata;
        private final Set<PropertyOverride<?>> properties;


        private Template(PropertyOverride<?>... properties) {
            this(null, properties);
        }

        private Template(String identifier, PropertyOverride<?>... properties) {
            this(identifier, null, properties);
        }

        private Template(String identifier, Component displayName, PropertyOverride<?>... properties) {
            this(identifier, displayName, null, properties);
        }

        public Template(String identifier, Component displayName, Component author, PropertyOverride<?>... properties) {
            this.metadata = identifier == null && author == null && displayName == null
                    ? null
                    : new TemplateAuthor(identifier, displayName, author);
            this.properties = Set.of(properties);
        }


        public boolean isEnabled() {
            return properties.stream()
                    .map(PropertyOverride::property)
                    .noneMatch(p -> p.getAvailability() == PropertyAvailability.BLOCKED); // 2 = Disabled Fully.
        }

        public HookCustomization getCustomizations() {
            HookCustomization customization = new HookCustomization();
            this.properties.forEach(customization::set);
            return customization;
        }

        public ItemStack getAsStack() {
            ItemStack itemStack = ModItems.GRAPPLING_HOOK.get().getDefaultInstance();
            return this.saveToStackComponents(itemStack);
        }


        /**
         * Overwrites the NBT of an itemstack with the contents of the
         * template.
         */
        public ItemStack saveToStackComponents(ItemStack stack) {
            GrapplehookItem hook = ModItems.GRAPPLING_HOOK.get();

            hook.applyCustomizations(stack, this.getCustomizations());
            hook.applyTemplateMetadata(stack, this.metadata); // null meta == remove.

            return stack;
        }
    }
}
