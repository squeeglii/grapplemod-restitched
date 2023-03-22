package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.content.registry.GrappleModMetaRegistry;
import com.yyon.grapplinghook.customization.CustomizationAvailability;
import com.yyon.grapplinghook.customization.render.AbstractCustomizationDisplay;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public abstract class CustomizationProperty<T> {

    private CustomizationAvailability status; // config can update this at any time.

    private final T defaultValue;


    public CustomizationProperty(T defaultValue) {
        if(defaultValue == null) throw new IllegalArgumentException("Default value cannot be null");

        this.defaultValue = defaultValue;
        this.status = CustomizationAvailability.ALLOWED;
    }

    public abstract void encodeValueTo(ByteBuf targetBuffer, T value);
    public abstract T decodeValueFrom(ByteBuf targetBuffer);

    public abstract void saveValueToTag(CompoundTag nbt, T value);
    public abstract T loadValueFromTag(CompoundTag nbt);
    public abstract byte[] valueToChecksumBytes(T value);

    public abstract AbstractCustomizationDisplay<T, CustomizationProperty<T>> getRenderer();


    public CustomizationAvailability getAvailability() {
        return this.status;
    }

    public CustomizationProperty<T> setAvailability(CustomizationAvailability status) {
        this.status = status;
        return this;
    }


    public final ResourceLocation getIdentifier() {
        return GrappleModMetaRegistry.CUSTOMIZATION_PROPERTIES.getKey(this);
    }

    public final T getDefaultValue() {
        return this.defaultValue;
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
        if(!(obj instanceof CustomizationProperty<?> other)) return false;

        boolean matchingIDs = this.getIdentifier().equals(other.getIdentifier());
        boolean matchingDefaults = this.getDefaultValue().equals(other.getDefaultValue());
        boolean defaultsDefinitelyWorkTogether = this.getDefaultValue() // Order of equals matters here.
                .getClass()
                .isInstance(other.getDefaultValue());

        return matchingIDs && matchingDefaults && defaultsDefinitelyWorkTogether;
    }
}
