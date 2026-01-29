package com.yyon.grapplinghook.content.customization.type;

import com.mojang.serialization.Codec;
import com.yyon.grapplinghook.content.customization.display.IntegerPropertyDisplay;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

import java.nio.ByteBuffer;

public class IntegerProperty extends CustomizationProperty<Integer> {

    protected int min;
    protected int max;
    protected IntegerPropertyDisplay display;

    public IntegerProperty(int defaultValue, int min, int max) {
        super(defaultValue);
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
        this.display = null;
    }

    @Override
    public Codec<Integer> getValueCodec() {
        return Codec.INT;
    }

    @Override
    public void encodeValueTo(ByteBuf targetBuffer, Integer value) {
        targetBuffer.writeInt(this.ifNullDefault(value));
    }

    @Override
    public Integer decodeValueFrom(ByteBuf targetBuffer) {
        return targetBuffer.readInt();
    }

    @Override
    public void saveValueToTag(CompoundTag nbt, Integer value) {
        nbt.putInt(this.getIdentifier().toString(), this.ifNullDefault(value));
    }

    @Override
    public Integer loadValueFromTag(CompoundTag nbt) {
        return nbt.getInt(this.getIdentifier().toString());
    }

    @Override
    public byte[] valueToChecksumBytes(Integer value) {
        // https://stackoverflow.com/questions/13071777/convert-double-to-byte-array
        return ByteBuffer.allocate(4).putInt(this.ifNullDefault(value)).array();
    }

    @Override
    public IntegerPropertyDisplay getDisplay() {
        if(this.display == null)
            this.display = new IntegerPropertyDisplay(this);

        return this.display;
    }

    public int getMin() {
        return this.min;
    }

    public int getMax() {
        return this.max;
    }
}
