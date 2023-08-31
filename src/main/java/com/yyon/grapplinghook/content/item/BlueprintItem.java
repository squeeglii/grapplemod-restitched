package com.yyon.grapplinghook.content.item;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.template.GrapplingHookTemplate;
import com.yyon.grapplinghook.customization.template.TemplateUtils;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class BlueprintItem extends Item implements ICustomizationAppliable, IAuthorable {

    public BlueprintItem() {
        super(new Item.Properties().stacksTo(64));
    }


    @Override
    public void applyCustomizations(ItemStack stack, CustomizationVolume customizations) {
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag nbt = customizations.writeToNBT();

        tag.put(TemplateUtils.NBT_HOOK_CUSTOMIZATIONS, nbt);

        stack.setTag(tag);
    }

    @Override
    public CustomizationVolume resetCustomizations(ItemStack stack) {
        CustomizationVolume custom = new CustomizationVolume();
        this.applyCustomizations(stack, custom);

        return custom;
    }

    @Override
    public Component getOverwriteMessage() {
        return Component.translatable("feedback.grapplemod.modifier.applied_blueprint");
    }

    @Override
    public SoundEvent getOverwriteSoundEffect() {
        return SoundEvents.VILLAGER_WORK_LIBRARIAN;
    }

    @Override
    public void commit(ItemStack stack, Component displayName, Component author) {
        GrapplingHookTemplate template = new GrapplingHookTemplate(null, displayName, author);
        CompoundTag metadata = template.saveMetadataToNBT();

        CompoundTag base = stack.getOrCreateTag();
        base.put(TemplateUtils.NBT_HOOK_TEMPLATE, metadata);

        stack.setTag(base);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> text, TooltipFlag isAdvanced) {
        Optional<Component> templateAuthor = TemplateUtils.getTemplateAuthor(stack);
        Optional<Component> templateName = TemplateUtils.getTemplateDisplayName(stack);

        // Blueprint item has no template NBT soooooo, it's probably not a template.
        if(this.isBlank(stack)) {
            text.add(Component.translatable("tooltip.blueprint.unused_hint")
                              .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            return;
        }

        if(templateName.isPresent()) {
            Component name = templateName.get()
                    .copy()
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);

            text.add(Component.empty()
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.translatable("grapple_tooltip.template.name"))
                    .append(Component.literal(" "))
                    .append(name)
            );
        }

        if(templateAuthor.isPresent()) {
            Component author = templateAuthor.get()
                    .copy()
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);

            text.add(Component.empty()
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.translatable("grapple_tooltip.template.author"))
                    .append(Component.literal(" "))
                    .append(author)
            );
        }

        if (!Screen.hasControlDown()) {
            text.add(Component.translatable("grapple_tooltip.configuration.hint")
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            return;
        }


        Optional<CustomizationVolume> optCustomizations = this.getCustomizations(stack);

        if(optCustomizations.isEmpty()) {
            text.add(Component.literal(""));
            text.add(Component.translatable("tooltip.blueprint.no_customizations")
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.RED));
            return;
        }

        CustomizationVolume customizations = optCustomizations.get();

        if(customizations.getPropertiesPresent().isEmpty()) {
            text.add(Component.translatable("tooltip.blueprint.no_customizations")
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.RED));
            return;
        }


        for(CustomizationProperty<?> property: customizations.getPropertiesPresent()) {
            Component hintText = property.getDisplay().getModificationHint(customizations);

            if(hintText == null)
                continue;

            Component formatted = hintText.copy().withStyle(ChatFormatting.DARK_GRAY);
            text.add(formatted);
        }

    }


    public Optional<CustomizationVolume> getCustomizations(ItemStack itemstack) {
        CompoundTag tag = itemstack.getOrCreateTag();
        Tag customizationsTag = tag.get(TemplateUtils.NBT_HOOK_CUSTOMIZATIONS);

        return customizationsTag instanceof CompoundTag customizationsCompound
                ? Optional.of(CustomizationVolume.fromNBT(customizationsCompound))
                : Optional.empty();
    }

    public boolean isBlank(ItemStack itemStack) {
        Optional<CustomizationVolume> optCustomizations = this.getCustomizations(itemStack);

        boolean isTemplateMetaMissing = TemplateUtils.getTemplateMetadataTag(itemStack).isEmpty();
        boolean areCustomizationsMissing = optCustomizations.isEmpty();
        return isTemplateMetaMissing && areCustomizationsMissing;
    }
}
