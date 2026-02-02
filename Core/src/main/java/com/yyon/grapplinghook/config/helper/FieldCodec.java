package com.yyon.grapplinghook.config.helper;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.BiConsumer;
import java.util.function.Function;

public record FieldCodec<C, T>(StreamCodec<ByteBuf, T> codec, Function<C, T> getter, BiConsumer<C, T> setter) {

    public void encode(C config, ByteBuf buf) {
        T val = getter.apply(config);
        this.codec.encode(buf, val);
    }

    public void decode(C config, ByteBuf buf) {
        T val = this.codec.decode(buf);
        this.setter.accept(config, val);
    }
}
