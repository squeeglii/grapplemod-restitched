package com.yyon.grapplinghook.content.customization.type;

import com.mojang.serialization.Codec;
import com.yyon.grapplinghook.content.customization.display.EnumPropertyDisplay;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

import java.nio.ByteBuffer;

public class EnumProperty<E extends Enum<E>> extends CustomizationProperty<E> {


    private final Codec<E> codec;
    private final E[] ordinalReversal;
    protected EnumPropertyDisplay<E> display;

    public EnumProperty(E defaultValue, E[] ordinalReverser, Codec<E> codec) {
        super(defaultValue);

        if(ordinalReverser == null)
            throw new IllegalArgumentException("Ordinal reverser cannot be null. Please just pass [Enum Here].values()");

        this.codec = codec;
        this.ordinalReversal = ordinalReverser;
    }

    @Override
    public Codec<E> getValueCodec() {
        return this.codec;
    }

    @Override
    public void encodeValueTo(ByteBuf targetBuffer, E value) {
        targetBuffer.writeInt(this.ifNullDefault(value).ordinal());
    }

    @Override
    public E decodeValueFrom(ByteBuf targetBuffer) {
        int ordinal = targetBuffer.readInt();
        return this.reverse(ordinal);
    }

    @Override
    public void saveValueToTag(CompoundTag nbt, E value) {
        nbt.putInt(this.getIdentifier().toString(), this.ifNullDefault(value).ordinal());
    }

    @Override
    public E loadValueFromTag(CompoundTag nbt) {
        int ordinal = nbt.getInt(this.getIdentifier().toString());
        return this.reverse(ordinal);
    }

    @Override
    public byte[] valueToChecksumBytes(E value) {
        int ordinal = this.ifNullDefault(value).ordinal();
        ByteBuffer buffer = ByteBuffer.allocate(4).putInt(ordinal);
        return buffer.array();
    }

    @Override
    public EnumPropertyDisplay<E> getDisplay() {
        if(this.display == null)
            this.display = new EnumPropertyDisplay<>(this);
        return this.display;
    }

    public final E[] getOrdinalReversal() {
        return this.ordinalReversal;
    }

    public E reverse(int ordinal) {
        if(ordinal > this.getOrdinalReversal().length)
            throw new IllegalStateException("Enum ordinal lookup does not contain the ordinal %s!".formatted(ordinal));
        return this.getOrdinalReversal()[ordinal];
    }
}
