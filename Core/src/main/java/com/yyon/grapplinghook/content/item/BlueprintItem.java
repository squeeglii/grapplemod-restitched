package com.yyon.grapplinghook.content.item;

import com.yyon.grapplinghook.content.item.type.IAuthorable;
import com.yyon.grapplinghook.content.item.type.ICustomizationApplicable;
import com.yyon.grapplinghook.content.registry.internal.ModDataComponents;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.content.customization.data.TemplateAuthor;
import com.yyon.grapplinghook.content.customization.type.CustomizationProperty;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class BlueprintItem extends Item implements ICustomizationApplicable, IAuthorable {

    public BlueprintItem() {
        super(
                new Item.Properties()
                        .stacksTo(64)
                        //.component(GrappleModItemComponents.AUTHORED, new TemplateAuthor()) // blank!
                        .component(ModDataComponents.CUSTOMIZABLE, new HookCustomization())
        );
    }

    @Override
    public boolean shouldAllowQuickOverwrite() {
        return false;
    }

    @Override
    public void applyCustomizations(ItemStack stack, HookCustomization customizations) {
        stack.set(ModDataComponents.CUSTOMIZABLE, customizations);
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
        TemplateAuthor authorComp = new TemplateAuthor(displayName, author);
        stack.set(ModDataComponents.AUTHORED, authorComp);
    }

    @NotNull
    @Override
    public Component getName(ItemStack stack) {
        return this.isBlank(stack)
                ? Component.translatable("item.grapplemod.blank_blueprint")
                : super.getName(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        // Blueprint item has no template NBT soooooo, it's probably not a template.
        if(this.isBlank(stack)) {
            tooltipComponents.add(Component.translatable("tooltip.blueprint.unused_hint")
                              .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC));
            return;
        }

        if(stack.has(ModDataComponents.AUTHORED)) {
            TemplateAuthor metadata = stack.get(ModDataComponents.AUTHORED);

            Component name = metadata.templateDisplayName()
                    .copy()
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);

            tooltipComponents.add(Component.empty()
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.translatable("grapple_tooltip.template.name"))
                    .append(Component.literal(" "))
                    .append(name)
            );

            Component author = metadata.author()
                    .copy()
                    .withStyle(ChatFormatting.GRAY, ChatFormatting.UNDERLINE);

            tooltipComponents.add(Component.empty()
                    .withStyle(ChatFormatting.DARK_GRAY)
                    .append(Component.translatable("grapple_tooltip.template.author"))
                    .append(Component.literal(" "))
                    .append(author)
            );
        }

        if (!Screen.hasControlDown()) {
            tooltipComponents.add(Component.translatable("grapple_tooltip.configuration.hint")
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
            return;
        }


        Optional<HookCustomization> optCustomizations = this.getCustomizationsOrDefault(stack);

        if(optCustomizations.isEmpty()) {
            tooltipComponents.add(Component.literal(""));
            tooltipComponents.add(Component.translatable("tooltip.blueprint.no_customizations")
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.RED));
            return;
        }

        HookCustomization customizations = optCustomizations.get();

        if(customizations.getPropertiesPresent().isEmpty()) {
            tooltipComponents.add(Component.translatable("tooltip.blueprint.no_customizations")
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.RED));
            return;
        }


        for(CustomizationProperty<?> property: customizations.getPropertiesPresent()) {
            Component hintText = property.getDisplay().getModificationHint(customizations);

            if(hintText == null)
                continue;

            Component formatted = hintText.copy().withStyle(ChatFormatting.DARK_GRAY);
            tooltipComponents.add(formatted);
        }

    }


    public Optional<HookCustomization> getCustomizationsOrDefault(ItemStack stack) {
        return stack.has(ModDataComponents.CUSTOMIZABLE)
                ? Optional.ofNullable(stack.get(ModDataComponents.CUSTOMIZABLE))
                : Optional.empty();
    }

    public boolean isBlank(ItemStack stack) {
        boolean isTemplateMetaMissing = !stack.has(ModDataComponents.AUTHORED);
        boolean areCustomizationsMissing = !stack.has(ModDataComponents.CUSTOMIZABLE) ||
                                           stack.get(ModDataComponents.CUSTOMIZABLE).isDefault();

        return isTemplateMetaMissing && areCustomizationsMissing;
    }
}
