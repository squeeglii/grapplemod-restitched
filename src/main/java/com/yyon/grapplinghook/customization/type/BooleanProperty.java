package com.yyon.grapplinghook.customization.type;

import com.yyon.grapplinghook.customization.render.AbstractCustomizationDisplay;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

public class BooleanProperty extends CustomizationProperty<Boolean> {

    public BooleanProperty(Boolean defaultValue) {
        super(defaultValue);
    }

    @Override
    public void encodeValueTo(ByteBuf targetBuffer, Boolean value) {

    }

    @Override
    public Boolean decodeValueFrom(ByteBuf targetBuffer) {
        return null;
    }

    @Override
    public void saveValueToTag(CompoundTag nbt, Boolean value) {

    }

    @Override
    public Boolean loadValueFromTag(CompoundTag nbt) {
        return null;
    }

    @Override
    public byte[] valueToChecksumBytes(Boolean value) {
        return new byte[] { (byte) (value ? 1 : 0) };
    }

    @Override
    public AbstractCustomizationDisplay<Boolean, CustomizationProperty<Boolean>> getRenderer() {
        throw new UnsupportedOperationException("Unimplemented");
    }
}
