package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.render.AbstractCustomizationDisplay;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

import java.nio.ByteBuffer;

public class EnumProperty<E extends Enum<E>> extends CustomizationProperty<E> {

    private final E[] ordinalReversal;

    public EnumProperty(E defaultValue, E[] ordinalReverser) {
        super(defaultValue);

        if(ordinalReverser == null) throw new IllegalArgumentException("Ordinal reverser cannot be null. Please just pass [Enum Here].values()");

        this.ordinalReversal = ordinalReverser;
    }

    @Override
    public void encodeValueTo(ByteBuf targetBuffer, E value) {

    }

    @Override
    public E decodeValueFrom(ByteBuf targetBuffer) {
        return null;
    }

    @Override
    public void saveValueToTag(CompoundTag nbt, E value) {

    }

    @Override
    public E loadValueFromTag(CompoundTag nbt) {
        return null;
    }

    @Override
    public byte[] valueToChecksumBytes(E value) {
        int ordinal = value.ordinal();
        if(ordinal <= Byte.MAX_VALUE)
            return new byte[] { (byte) ordinal };

        // enum is not small - give up and just save an int.
        ByteBuffer buffer = ByteBuffer.allocate(4).putInt(ordinal);
        return buffer.array();
    }

    @Override
    public AbstractCustomizationDisplay<E, CustomizationProperty<E>> getRenderer() {
        throw new UnsupportedOperationException("Unimplemented");
    }

    public E[] getOrdinalReversal() {
        return this.ordinalReversal;
    }
}
