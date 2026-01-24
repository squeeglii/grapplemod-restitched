package com.yyon.grapplinghook.customization.type.enums;

import com.mojang.serialization.Codec;
import com.yyon.grapplinghook.util.IFriendlyNameProvider;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

public enum RopeStyle implements IFriendlyNameProvider, StringRepresentable {

    REGULAR(0),
    IRON(1),
    RIBBON(2),
    TRANS_PRIDE(3),
    GHOSTLY(4),
    TAPE_MEASURE(5);


    public static final Codec<RopeStyle> CODEC = StringRepresentable.fromValues(RopeStyle::values);

    private final int id;

    RopeStyle(int id) {
        this.id = id;
    }

    public float getTextureMinBound() {
        return (this.id) / 8f;
    }

    public float getTextureMidBound() {
        return (this.id + 0.5f) / 8f;
    }

    public float getTextureMaxBound() {
        return (this.id + 1) / 8f;
    }

    @Override
    public String getFriendlyName() {
        return "rope_style";
    }

    @NotNull
    @Override
    public String getSerializedName() {
        return this.name();
    }

}
