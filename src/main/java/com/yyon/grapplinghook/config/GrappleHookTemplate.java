package com.yyon.grapplinghook.config;

import com.yyon.grapplinghook.item.GrapplehookItem;
import com.yyon.grapplinghook.registry.GrappleModItems;
import com.yyon.grapplinghook.util.GrappleCustomization;
import net.minecraft.world.item.ItemStack;

import java.util.*;

// These mimic the old recipes, automatically checking if a given template is valid.
public class GrappleHookTemplate {

    private static final Map<String, GrappleHookTemplate> defaultTemplates = new HashMap<>();

    private static GrappleHookTemplate register(GrappleHookTemplate template) {
        GrappleHookTemplate.defaultTemplates
                .put(template.getId().toLowerCase(), template);
        return template;
    }

    private static BooleanProperty property(String id, boolean value) {
        return new BooleanProperty(id, value);
    }

    private static DoubleProperty property(String id, double value) {
        return new DoubleProperty(id, value);
    }

    public static Collection<GrappleHookTemplate> getTemplates() {
        return Collections.unmodifiableCollection(defaultTemplates.values());
    }

    public static final GrappleHookTemplate DOUBLE_MOTOR_HOOK = register(new GrappleHookTemplate(
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




    private final String identifier;
    private final Set<AbstractProperty<?>> properties;

    private GrappleHookTemplate(String identifier, AbstractProperty<?>... properties) {
        this.identifier = identifier;
        this.properties = Set.of(properties);
    }

    public String getId() {
        return identifier;
    }

    public boolean isEnabled() {
        return properties.stream()
                .map(AbstractProperty::getId)
                .noneMatch(p -> GrappleCustomization.optionEnabledInConfig(p) <= 0);
    }

    public GrappleCustomization getCustomizations() {
        GrappleCustomization customization = new GrappleCustomization();

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
