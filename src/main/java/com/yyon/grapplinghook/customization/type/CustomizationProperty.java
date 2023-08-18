package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.content.registry.GrappleModMetaRegistry;
import com.yyon.grapplinghook.customization.CustomizationAvailability;
import com.yyon.grapplinghook.customization.predicate.CustomizationPredicate;
import com.yyon.grapplinghook.customization.predicate.SuccessCustomizationPredicate;
import com.yyon.grapplinghook.customization.render.AbstractCustomizationDisplay;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class CustomizationProperty<T> {

    private T defaultValue;
    private CustomizationAvailability status; // config can update this at any time.

    private final CustomizationPredicate<?> validityPredicate;

    public CustomizationProperty(T defaultValue) {
        this(defaultValue, null);
    }

    public CustomizationProperty(T defaultValue, CustomizationPredicate<?> validityPredicate) {
        if(defaultValue == null) throw new IllegalArgumentException("Default value cannot be null");

        this.defaultValue = defaultValue;
        this.status = CustomizationAvailability.ALLOWED;

        this.validityPredicate = validityPredicate == null
                ? SuccessCustomizationPredicate.INSTANCE
                : validityPredicate;
    }

    public abstract void encodeValueTo(ByteBuf targetBuffer, T value);
    public abstract T decodeValueFrom(ByteBuf targetBuffer);

    public abstract void saveValueToTag(CompoundTag nbt, T value);
    public abstract T loadValueFromTag(CompoundTag nbt);
    public abstract byte[] valueToChecksumBytes(T value);

    public abstract AbstractCustomizationDisplay<T, ? extends CustomizationProperty<T>> getDisplay();

    public final T ifNullDefault(T value) {
        return value == null
                ? this.getDefaultValue()
                : value;
    }



    public CustomizationProperty<T> setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }

    public CustomizationProperty<T> setAvailability(CustomizationAvailability status) {
        this.status = status;
        return this;
    }



    public final T getDefaultValue() {
        return this.defaultValue;
    }

    public CustomizationAvailability getAvailability() {
        return this.status;
    }

    public final ResourceLocation getIdentifier() {
        return GrappleModMetaRegistry.CUSTOMIZATION_PROPERTIES.getKey(this);
    }

    public CustomizationPredicate<?> getValidityPredicate() {
        return this.validityPredicate;
    }

    public String getLocalization() {
        return this.getLocalization(null);
    }

    public String getLocalization(String suffix) {
        String path = this.getIdentifier().toLanguageKey();
        boolean includeConnectingDot = suffix != null && !suffix.isEmpty() && !suffix.startsWith(".");
        return "grapple_property.%s%s%s".formatted(
                path,
                includeConnectingDot ? "." : "",
                suffix == null ? "" : suffix
        );
    }

    public Component getDisplayName() {
        ResourceLocation id = this.getIdentifier();
        return id == null
                ? Component.translatable("grapple_property.invalid").withStyle(ChatFormatting.RED)
                : Component.translatable(this.getLocalization());
    }

    public Component getDescription() {
        ResourceLocation id = this.getIdentifier();
        return id == null
                ? Component.translatable("grapple_property.invalid.desc")
                : Component.translatable(this.getLocalization("desc"));
    }

    @Override
    public int hashCode() {
        ResourceLocation identifier = this.getIdentifier();
        int result = identifier != null
                ? identifier.hashCode()
                : 0;
        return 31 * result + this.getDefaultValue().hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        if (!(obj instanceof CustomizationProperty<?> other)) return false;

        boolean matchingIDs = this.getIdentifier().equals(other.getIdentifier());
        boolean matchingDefaults = this.getDefaultValue().equals(other.getDefaultValue());
        boolean defaultsDefinitelyWorkTogether = this.getDefaultValue() // Order of equals matters here.
                .getClass()
                .isInstance(other.getDefaultValue());

        return matchingIDs && matchingDefaults && defaultsDefinitelyWorkTogether;
    }
}
