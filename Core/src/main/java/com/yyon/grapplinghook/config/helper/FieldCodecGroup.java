package com.yyon.grapplinghook.config.helper;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.function.Supplier;

public class FieldCodecGroup<B extends ByteBuf, V> implements StreamCodec<B, V> {

    private final Supplier<V> constructor;
    private final List<FieldCodec<V, ?>> fieldCodecs;

    public FieldCodecGroup(Supplier<V> constructor, List<FieldCodec<V, ?>> fieldCodecs) {
        this.constructor = constructor;
        this.fieldCodecs = fieldCodecs;
    }

    @Override
    public void encode(B buf, V config) {
        for(FieldCodec<V, ?> field : this.fieldCodecs)
            field.encode(config, buf);
    }

    @Override
    public V decode(B buf) {
        V config = this.constructor.get();

        for(FieldCodec<V, ?> field : this.fieldCodecs) {
            field.decode(config, buf);
        }

        return config;
    }
}
