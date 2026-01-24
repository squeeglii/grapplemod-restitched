package com.yyon.grapplinghook.customization;

import com.yyon.grapplinghook.content.item.GrapplehookItem;
import com.yyon.grapplinghook.content.registry.internal.ModItems;
import com.yyon.grapplinghook.customization.data.HookCustomization;
import com.yyon.grapplinghook.customization.helper.PropertyOverride;
import com.yyon.grapplinghook.customization.type.enums.CrouchToggle;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static com.yyon.grapplinghook.content.registry.CustomizationProperties.*;

// These mimic the old recipes, automatically checking if a given template is valid.
public class HookTemplates {

    private static final Map<String, HookTemplates> defaultTemplates = new LinkedHashMap<>();

    private static HookTemplates registerDefault(HookTemplates template) {
        HookTemplates.defaultTemplates.put(template.getId().toLowerCase(), template);
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

    public static Collection<HookTemplates> getTemplates() {
        return Collections.unmodifiableCollection(defaultTemplates.values());
    }


    public static final HookTemplates ENDER_HOOK = registerDefault(new HookTemplates(
            "ender_hook", Component.translatable("hook_template.grapplemod.ender_hook"),
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(ENDER_STAFF_ATTACHED, true)
    ));

    public static final HookTemplates MOTOR_HOOK = registerDefault(new HookTemplates(
            "motor_hook", Component.translatable("hook_template.grapplemod.motor_hook"),
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MOTOR_ATTACHED, true),
            property(MOVE_SPEED_MULTIPLIER, 2.0d)
    ));

    public static final HookTemplates SMART_HOOK = registerDefault(new HookTemplates(
            "smart_hook", Component.translatable("hook_template.grapplemod.smart_hook"),
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MOTOR_ATTACHED, true),
            property(SMART_MOTOR, true),
            property(MOVE_SPEED_MULTIPLIER, 2.0d)
    ));

    public static final HookTemplates MAGNET_HOOK = registerDefault(new HookTemplates(
            "magnet_hook", Component.translatable("hook_template.grapplemod.magnet_hook"),
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MAGNET_ATTACHED, true),
            property(FORCEFIELD_ATTACHED, true)
    ));

    public static final HookTemplates ROCKET_HOOK = registerDefault(new HookTemplates(
            "rocket_hook", Component.translatable("hook_template.grapplemod.rocket_hook"),
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(ROCKET_ATTACHED, true)
    ));

    public static final HookTemplates DOUBLE_MOTOR_HOOK = registerDefault(new HookTemplates(
            "double_motor_hook", Component.translatable("hook_template.grapplemod.double_motor_hook"),
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

    public static final HookTemplates DOUBLE_ROCKET_MOTOR_HOOK = registerDefault(new HookTemplates(
            "double_rocket_motor_hook", Component.translatable("hook_template.grapplemod.double_rocket_motor_hook"),
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
    private final Component displayName;
    private final Component author;

    private final Set<PropertyOverride<?>> properties;


    private HookTemplates(PropertyOverride<?>... properties) {
        this(null, properties);
    }
    private HookTemplates(String identifier, PropertyOverride<?>... properties) {
        this(identifier, null, properties);
    }

    private HookTemplates(String identifier, Component displayName, PropertyOverride<?>... properties) {
        this(identifier, displayName, Component.translatable("grapple_template.author.default"), properties);
    }

    public HookTemplates(String identifier, Component displayName, Component author, PropertyOverride<?>... properties) {
        this.identifier = identifier == null
                ? "user-generated"
                : identifier;
        this.displayName = displayName;
        this.author = author;
        this.properties = Set.of(properties);
    }

    public String getId() {
        return this.identifier;
    }

    public Component getDisplayName() {
        return this.displayName;
    }

    public Component getAuthor() {
        return this.author;
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
        return this.saveNBTToStack(itemStack);
    }

    /**
     * Encodes metadata details of a template (name, author, etc.)
     * and saves it to an NBT Compound tag.
     */
    public CompoundTag saveMetadataToNBT() {
        CompoundTag data = new CompoundTag();

        data.putString("id", this.identifier);

        if(this.displayName != null) {
            String json = Component.Serializer.toJson(this.displayName);
            data.putString("display_name", json);
        }

        if(this.author != null) {
            String json = Component.Serializer.toJson(this.author);
            data.putString("author", json);
        }

        return data;
    }

    /**
     * Overwrites the NBT of an itemstack with the contents of the
     * template.
     */
    public ItemStack saveNBTToStack(ItemStack stack) {
        GrapplehookItem hook = ModItems.GRAPPLING_HOOK.get();

        hook.applyCustomizations(stack, this.getCustomizations());
        hook.applyTemplateMetadata(stack, this); //todo: seperate

        return stack;
    }
}
