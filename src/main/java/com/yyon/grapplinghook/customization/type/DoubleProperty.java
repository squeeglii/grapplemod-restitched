package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.render.AbstractCustomizationDisplay;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

import java.nio.ByteBuffer;

public class DoubleProperty extends CustomizationProperty<Double> {

    protected double min;
    protected double max;

    public DoubleProperty(double defaultValue, double min, double max) {
        super(defaultValue);
        this.min = Math.min(min, max);
        this.max = Math.max(min, max);
    }

    @Override
    public void encodeValueTo(ByteBuf targetBuffer, Double value) {

    }

    @Override
    public Double decodeValueFrom(ByteBuf targetBuffer) {
        return null;
    }

    @Override
    public void saveValueToTag(CompoundTag nbt, Double value) {

    }

    @Override
    public Double loadValueFromTag(CompoundTag nbt) {
        return null;
    }

    @Override
    public byte[] valueToChecksumBytes(Double value) {
        // https://stackoverflow.com/questions/13071777/convert-double-to-byte-array
        return ByteBuffer.allocate(8).putDouble(value).array();
    }

    @Override
    public AbstractCustomizationDisplay<Double, CustomizationProperty<Double>> getRenderer() {
        throw new UnsupportedOperationException("Unimplemented");
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }
}
