package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.render.AbstractCustomizationDisplay;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

import java.nio.ByteBuffer;

public class EnumProperty<E extends Enum<E>> extends CustomizationProperty<E> {

    private final E[] ordinalReversal;

    public EnumProperty(E defaultValue, E[] ordinalReverser) {
        super(defaultValue);

        if(ordinalReverser == null)
            throw new IllegalArgumentException("Ordinal reverser cannot be null. Please just pass [Enum Here].values()");

        this.ordinalReversal = ordinalReverser;
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
    public AbstractCustomizationDisplay<E, CustomizationProperty<E>> getDisplay() {
        throw new UnsupportedOperationException("Unimplemented");
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
