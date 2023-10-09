package com.yyon.grapplinghook.customization.template;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public class TemplateUtils {

    public static final String NBT_HOOK_CUSTOMIZATIONS = "customization";
    public static final String NBT_HOOK_TEMPLATE = "template";

    public static final String NBT_TEMPLATE_IDENTIFIER = "id";
    public static final String NBT_TEMPLATE_DISPLAY_NAME = "display_name";
    public static final String NBT_TEMPLATE_AUTHOR = "author";


    public static Optional<CompoundTag> getTemplateMetadataTag(ItemStack stack) {
        CompoundTag tag = stack.getTag();

        if(tag == null)
            return Optional.empty();

        Tag metaTag = tag.get(NBT_HOOK_TEMPLATE);
        if(!(metaTag instanceof CompoundTag metaCompound))
            return Optional.empty();

        return Optional.of(metaCompound);
    }

    public static Optional<Component> getDisplayNameFromMetadata(CompoundTag metadataBlob) {
        if(metadataBlob == null) return Optional.empty();
        return getComponentFromNBT(metadataBlob, NBT_TEMPLATE_DISPLAY_NAME);
    }

    public static Optional<Component> getTemplateDisplayName(ItemStack stack) {
        Optional<CompoundTag> template = getTemplateMetadataTag(stack);

        if(template.isEmpty())
            return Optional.empty();

        return getComponentFromNBT(template.get(), NBT_TEMPLATE_DISPLAY_NAME);
    }

    public static Optional<Component> getAuthorFromMetadata(CompoundTag metadataBlob) {
        if(metadataBlob == null) return Optional.empty();
        return getComponentFromNBT(metadataBlob, NBT_TEMPLATE_DISPLAY_NAME);
    }

    public static Optional<Component> getTemplateAuthor(ItemStack stack) {
        Optional<CompoundTag> template = getTemplateMetadataTag(stack);

        if(template.isEmpty())
            return Optional.empty();

        return getComponentFromNBT(template.get(), NBT_TEMPLATE_AUTHOR);
    }

    public static Optional<String> getIdFromMetadata(CompoundTag metadataBlob) {
        if(metadataBlob == null) return Optional.empty();

        Tag idTag = metadataBlob.get(NBT_TEMPLATE_IDENTIFIER);
        if(!(idTag instanceof StringTag stringTag))
            return Optional.empty();

        return Optional.of(stringTag.getAsString());
    }

    public static Optional<String> getTemplateId(ItemStack stack) {
        Optional<CompoundTag> template = getTemplateMetadataTag(stack);

        if(template.isEmpty())
            return Optional.empty();

        return getIdFromMetadata(template.get());
    }


    private static Optional<Component> getComponentFromNBT(CompoundTag tag, String tagName) {
        Tag componentTag = tag.get(tagName);
        if(!(componentTag instanceof StringTag))
            return Optional.empty();

        try {
            Component text = Component.Serializer.fromJson(componentTag.getAsString());
            return text != null
                    ? Optional.of(text)
                    : Optional.empty();

        } catch (Exception exception) {
            GrappleMod.LOGGER.error(exception);
            return Optional.empty();
        }
    }

}
