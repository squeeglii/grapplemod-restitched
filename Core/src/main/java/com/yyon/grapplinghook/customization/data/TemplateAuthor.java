package com.yyon.grapplinghook.customization.data;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;

public record TemplateAuthor(String templateId, Component templateDisplayName, Component author) {

    public static final Codec<TemplateAuthor> CODEC;
    public static final StreamCodec<? super RegistryFriendlyByteBuf, TemplateAuthor> STREAM_CODEC;

    public TemplateAuthor(Component templateDisplayName, Component author) {
        this("user-generated", templateDisplayName, author);
    }



    public static TemplateAuthor unknown() {
        return new TemplateAuthor(Component.translatable(), Component.translatable("Unknown") );
    }
}
