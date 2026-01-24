package com.yyon.grapplinghook.customization.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;

public record TemplateAuthor(String templateId, Component templateDisplayName, Component author) {

    private static final String DEFAULT_TEMPLATE_ID = "user-generated";

    //todo: properly implement the codec across the project.

    public static final Codec<TemplateAuthor> CODEC = RecordCodecBuilder.create(builder -> builder.apply3(
            TemplateAuthor::new,

            Codec.STRING.optionalFieldOf("template_id", DEFAULT_TEMPLATE_ID).forGetter(TemplateAuthor::templateId),
            ComponentSerialization.FLAT_CODEC.optionalFieldOf("display_name", Component.empty()).forGetter(TemplateAuthor::templateDisplayName),
            ComponentSerialization.FLAT_CODEC.optionalFieldOf("author", Component.empty()).forGetter(TemplateAuthor::author)
    ));

    public static final StreamCodec<? super RegistryFriendlyByteBuf, TemplateAuthor> STREAM_CODEC;

    public TemplateAuthor(Component templateDisplayName, Component author) {
        this(DEFAULT_TEMPLATE_ID, templateDisplayName, author);
    }



    //todo: create translations for blank author data.
    public static TemplateAuthor unknown() {
        return new TemplateAuthor(Component.empty(), Component.empty());
    }
}
