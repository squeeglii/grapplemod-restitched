package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.render.DoubleCustomizationDisplay;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

import java.nio.ByteBuffer;

public class DoubleProperty extends CustomizationProperty<Double> {

    protected double min;
    protected double max;
    protected DoubleCustomizationDisplay display;

    public DoubleProperty(double defaultValue, double min, double max) {
        super(defaultValue);
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
        this.display = null;
    }

    @Override
    public void encodeValueTo(ByteBuf targetBuffer, Double value) {
        targetBuffer.writeDouble(this.ifNullDefault(value));
    }

    @Override
    public Double decodeValueFrom(ByteBuf targetBuffer) {
        return targetBuffer.readDouble();
    }

    @Override
    public void saveValueToTag(CompoundTag nbt, Double value) {
        nbt.putDouble(this.getIdentifier().toString(), this.ifNullDefault(value));
    }

    @Override
    public Double loadValueFromTag(CompoundTag nbt) {
        return nbt.getDouble(this.getIdentifier().toString());
    }

    @Override
    public byte[] valueToChecksumBytes(Double value) {
        // https://stackoverflow.com/questions/13071777/convert-double-to-byte-array
        return ByteBuffer.allocate(8).putDouble(this.ifNullDefault(value)).array();
    }

    @Override
    public DoubleCustomizationDisplay getDisplay() {
        if(this.display == null)
            this.display = new DoubleCustomizationDisplay(this);

        return this.display;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }
}
