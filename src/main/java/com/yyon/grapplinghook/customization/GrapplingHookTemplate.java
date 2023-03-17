package com.yyon.grapplinghook.customization;

import com.yyon.grapplinghook.content.registry.GrappleModItems;
import net.minecraft.world.item.ItemStack;

import java.util.*;

// These mimic the old recipes, automatically checking if a given template is valid.
public class GrapplingHookTemplate {

    private static final Map<String, GrapplingHookTemplate> defaultTemplates = new LinkedHashMap<>();

    private static GrapplingHookTemplate register(GrapplingHookTemplate template) {
        GrapplingHookTemplate.defaultTemplates
                .put(template.getId().toLowerCase(), template);
        return template;
    }

    private static BooleanProperty property(String id, boolean value) {
        return new BooleanProperty(id, value);
    }

    private static DoubleProperty property(String id, double value) {
        return new DoubleProperty(id, value);
    }

    public static Collection<GrapplingHookTemplate> getTemplates() {
        return Collections.unmodifiableCollection(defaultTemplates.values());
    }


    public static final GrapplingHookTemplate ENDER_HOOK = register(new GrapplingHookTemplate(
            "ender_hook",
            property("throwspeed", 3.5d),
            property("maxlen", 60.0d),

            property("enderstaff", true)
    ));

    public static final GrapplingHookTemplate MOTOR_HOOK = register(new GrapplingHookTemplate(
            "motor_hook",
            property("throwspeed", 3.5d),
            property("maxlen", 60.0d),

            property("motor", true),
            property("playermovementmult", 2.0d)
    ));

    public static final GrapplingHookTemplate SMART_HOOK = register(new GrapplingHookTemplate(
            "smart_hook",
            property("throwspeed", 3.5d),
            property("maxlen", 60.0d),

            property("motor", true),
            property("smartmotor", true),
            property("playermovementmult", 2.0d)
    ));

    public static final GrapplingHookTemplate MAGNET_HOOK = register(new GrapplingHookTemplate(
            "magnet_hook",
            property("throwspeed", 3.5d),
            property("maxlen", 60.0d),

            property("attract", true),
            property("repel", true)
    ));

    public static final GrapplingHookTemplate ROCKET_HOOK = register(new GrapplingHookTemplate(
            "rocket_hook",
            property("throwspeed", 3.5d),
            property("maxlen", 60.0d),

            property("rocket", true)
    ));

    public static final GrapplingHookTemplate DOUBLE_MOTOR_HOOK = register(new GrapplingHookTemplate(
            "double_motor_hook",
            property("maxlen", 60.0d),

            property("doublehook", true),
            property("motor", true),
            property("motormaxspeed", 10.0d),
            property("sticky", true),

            property("hookgravity", 50.0d),
            property("verticalthrowangle", 30.0d),
            property("sneakingverticalthrowangle", 25.0d),
            property("reelin", false),

            property("motorwhencrouching", false),
            property("smartdoublemotor", true),

            property("angle", 25.0d),
            property("sneakingangle", 0.0d),

            property("throwspeed", 20.0d),

            property("playermovementmult", 2.0d)
    ));

    public static final GrapplingHookTemplate DOUBLE_ROCKET_MOTOR_HOOK = register(new GrapplingHookTemplate(
            "double_rocket_motor_hook",
            property("maxlen", 60.0d),

            property("doublehook", true),
            property("motor", true),
            property("motormaxspeed", 10.0d),
            property("sticky", true),

            property("hookgravity", 50.0d),
            property("verticalthrowangle", 30.0d),
            property("sneakingverticalthrowangle", 25.0d),
            property("reelin", false),

            property("motorwhencrouching", true),
            property("smartdoublemotor", true),

            property("angle", 25.0d),
            property("sneakingangle", 0.0d),

            property("rocket", true),
            property("rocket_vertical_angle", 30.0d),

            property("throwspeed", 20.0d),

            property("playermovementmult", 2.0d)
    ));


    private final String identifier;
    private final Set<AbstractProperty<?>> properties;

    private GrapplingHookTemplate(String identifier, AbstractProperty<?>... properties) {
        this.identifier = identifier;
        this.properties = Set.of(properties);
    }

    public String getId() {
        return identifier;
    }

    public boolean isEnabled() {
        return properties.stream()
                .map(AbstractProperty::getId)
                .noneMatch(p -> CustomizationVolume.optionEnabledInConfig(p) >= 2); // 2 = Disabled Fully.
    }

    public CustomizationVolume getCustomizations() {
        CustomizationVolume customization = new CustomizationVolume();

        properties.forEach(p -> {
            if (p instanceof BooleanProperty b)
                customization.setBoolean(b.getId(), b.getValue());

            if (p instanceof DoubleProperty d)
                customization.setDouble(d.getId(), d.getValue());
        });

        return customization;
    }

    public ItemStack getAsStack() {
        ItemStack itemStack = GrappleModItems.GRAPPLING_HOOK.get().getDefaultInstance();
        GrappleModItems.GRAPPLING_HOOK.get().setCustomOnServer(itemStack, this.getCustomizations());

        return itemStack;
    }




    private static abstract class AbstractProperty<T> {

        private final String id;
        private final T value;

        public AbstractProperty(String id, T value) {
            this.id = id;
            this.value = value;
        }

        public String getId() {
            return id;
        }

        public T getValue() {
            return value;
        }
    }

    public static class BooleanProperty extends AbstractProperty<Boolean> {
        public BooleanProperty(String id, boolean value) {
            super(id, value);
        }
    }

    public static class DoubleProperty extends AbstractProperty<Double> {
        public DoubleProperty(String id, double value) {
            super(id, value);
        }
    }
}
