package com.yyon.grapplinghook.content.customization.type;

import com.mojang.serialization.Codec;
import com.yyon.grapplinghook.content.customization.display.BooleanPropertyDisplay;
import io.netty.buffer.ByteBuf;

public class BooleanProperty extends CustomizationProperty<Boolean> {

    private BooleanPropertyDisplay display;

    public BooleanProperty(Boolean defaultValue) {
        super(defaultValue);
        this.display = null;
    }

    @Override
    public Codec<Boolean> getValueCodec() {
        return Codec.BOOL;
    }

    @Override
    public void encodeValueTo(ByteBuf targetBuffer, Boolean value) {
        targetBuffer.writeBoolean(this.ifNullDefault(value));
    }

    @Override
    public Boolean decodeValueFrom(ByteBuf targetBuffer) {
        return targetBuffer.readBoolean();
    }

    @Override
    public byte[] valueToChecksumBytes(Boolean value) {
        return new byte[] { (byte) (this.ifNullDefault(value) ? 1 : 0) };
    }

    @Override
    public BooleanPropertyDisplay getDisplay() {
        if(this.display == null)
            this.display = new BooleanPropertyDisplay(this);

        return this.display;
    }
}
