package com.yyon.grapplinghook.customization.template;

import com.yyon.grapplinghook.content.item.GrapplehookItem;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import com.yyon.grapplinghook.customization.CustomizationAvailability;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.type.CrouchToggle;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.*;

import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.*;

// These mimic the old recipes, automatically checking if a given template is valid.
public class GrapplingHookTemplate {

    private static final Map<String, GrapplingHookTemplate> defaultTemplates = new LinkedHashMap<>();

    private static GrapplingHookTemplate registerDefault(GrapplingHookTemplate template) {
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


    public static final GrapplingHookTemplate ENDER_HOOK = registerDefault(new GrapplingHookTemplate(
            "ender_hook", Component.translatable("hook_template.grapplemod.ender_hook"),
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(ENDER_STAFF_ATTACHED, true)
    ));

    public static final GrapplingHookTemplate MOTOR_HOOK = registerDefault(new GrapplingHookTemplate(
            "motor_hook", Component.translatable("hook_template.grapplemod.motor_hook"),
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MOTOR_ATTACHED, true),
            property(MOVE_SPEED_MULTIPLIER, 2.0d)
    ));

    public static final GrapplingHookTemplate SMART_HOOK = registerDefault(new GrapplingHookTemplate(
            "smart_hook", Component.translatable("hook_template.grapplemod.smart_hook"),
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MOTOR_ATTACHED, true),
            property(SMART_MOTOR, true),
            property(MOVE_SPEED_MULTIPLIER, 2.0d)
    ));

    public static final GrapplingHookTemplate MAGNET_HOOK = registerDefault(new GrapplingHookTemplate(
            "magnet_hook", Component.translatable("hook_template.grapplemod.magnet_hook"),
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(MAGNET_ATTACHED, true),
            property(FORCEFIELD_ATTACHED, true)
    ));

    public static final GrapplingHookTemplate ROCKET_HOOK = registerDefault(new GrapplingHookTemplate(
            "rocket_hook", Component.translatable("hook_template.grapplemod.rocket_hook"),
            property(HOOK_THROW_SPEED, 3.5d),
            property(MAX_ROPE_LENGTH, 60.0d),

            property(ROCKET_ATTACHED, true)
    ));

    public static final GrapplingHookTemplate DOUBLE_MOTOR_HOOK = registerDefault(new GrapplingHookTemplate(
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

    public static final GrapplingHookTemplate DOUBLE_ROCKET_MOTOR_HOOK = registerDefault(new GrapplingHookTemplate(
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


    private GrapplingHookTemplate(PropertyOverride<?>... properties) {
        this(null, properties);
    }
    private GrapplingHookTemplate(String identifier, PropertyOverride<?>... properties) {
        this(identifier, null, properties);
    }

    private GrapplingHookTemplate(String identifier, Component displayName, PropertyOverride<?>... properties) {
        this(identifier, displayName, Component.translatable("grapple_template.author.default"), properties);
    }

    public GrapplingHookTemplate(String identifier, Component displayName, Component author, PropertyOverride<?>... properties) {
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
                .noneMatch(p -> p.getAvailability() == CustomizationAvailability.BLOCKED); // 2 = Disabled Fully.
    }

    public CustomizationVolume getCustomizations() {
        CustomizationVolume customization = new CustomizationVolume();
        this.properties.forEach(customization::set);
        return customization;
    }

    public ItemStack getAsStack() {
        ItemStack itemStack = GrappleModItems.GRAPPLING_HOOK.get().getDefaultInstance();
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
        GrapplehookItem hook = GrappleModItems.GRAPPLING_HOOK.get();

        hook.applyCustomizations(stack, this.getCustomizations());
        hook.applyTemplateMetadata(stack, this);

        return stack;
    }


    public static GrapplingHookTemplate fromStack(ItemStack stack) {
        if(stack == null) throw new IllegalArgumentException("Stack cannot be null");

        CompoundTag tag = stack.getTag();

        return tag == null
                ? new GrapplingHookTemplate()
                : GrapplingHookTemplate.fromNBT(tag);
    }

    // Suppressed - Deals with generics in a way that shouldn't cause issues with types.
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static GrapplingHookTemplate fromNBT(CompoundTag tag) {
        if(tag == null) throw new IllegalArgumentException("NBT Tag cannot be null");

        Tag customizationsTag = tag.get(TemplateUtils.NBT_HOOK_CUSTOMIZATIONS);
        CustomizationVolume volume = customizationsTag instanceof CompoundTag customizationsCompound
                ? CustomizationVolume.fromNBT(customizationsCompound)
                : new CustomizationVolume();

        Tag metaTag = tag.get(TemplateUtils.NBT_HOOK_TEMPLATE);
        CompoundTag metaCompound = metaTag instanceof CompoundTag compound
                ? compound
                : null;

        String id = TemplateUtils.getIdFromMetadata(metaCompound).orElse(null);
        Component displayName = TemplateUtils.getDisplayNameFromMetadata(metaCompound).orElse(null);
        Component author = TemplateUtils.getAuthorFromMetadata(metaCompound).orElse(null);

        PropertyOverride[] overrides = volume.getPropertiesPresent().stream()
                .filter(property -> volume.get(property) != null)
                .filter(property -> {
                    Object currentValue = volume.get(property);
                    return property.getDefaultValue().equals(currentValue);
                })
                .map(property -> {
                    Object currentValue = volume.get(property);
                    return new PropertyOverride(property, currentValue);
                })
                .toArray(PropertyOverride[]::new);

        return new GrapplingHookTemplate(id, displayName, author, overrides);
    }
}
