package com.yyon.grapplinghook.content.item.smithing;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class LongFallBootsTemplateItem extends SmithingTemplateItem {

    // Taken from superclass as it was private there.
    private static final ChatFormatting TITLE_FORMAT = ChatFormatting.GRAY;
    private static final ChatFormatting DESCRIPTION_FORMAT = ChatFormatting.BLUE;
    private static final ResourceLocation EMPTY_SLOT_BOOTS = new ResourceLocation("item/empty_armor_slot_boots");

    // And this stuff is new
    private static final String TRANSLATION_APPLIES_TO = Util.makeDescriptionId("item", GrappleMod.id("smithing_template.long_fall_boots.applies_to"));
    private static final String TRANSLATION_INGREDIENTS = Util.makeDescriptionId("item", GrappleMod.id("smithing_template.long_fall_boots.ingredients"));
    private static final String TRANSLATION_UPGRADE_NAME = Util.makeDescriptionId("upgrade", GrappleMod.id("long_fall_boots"));
    private static final String TRANSLATION_BASE_SLOT = Util.makeDescriptionId("item", GrappleMod.id("smithing_template.long_fall_boots.base_slot_description"));
    private static final String TRANSLATION_ADDITIONAL_SLOT = Util.makeDescriptionId("item", GrappleMod.id("smithing_template.long_fall_boots.additions_slot_description"));

    private static final ResourceLocation MATERIAL_PHANTOM_MEMBRANE = GrappleMod.id("items/hint/empty_slot_membrane");

    public LongFallBootsTemplateItem() {
        super(
                Component.translatable(TRANSLATION_APPLIES_TO).withStyle(DESCRIPTION_FORMAT), // applies to
                Component.translatable(TRANSLATION_INGREDIENTS).withStyle(DESCRIPTION_FORMAT), // ingredients
                Component.translatable(TRANSLATION_UPGRADE_NAME).withStyle(TITLE_FORMAT), // upgrade description
                Component.translatable(TRANSLATION_BASE_SLOT), // base slot description
                Component.translatable(TRANSLATION_ADDITIONAL_SLOT), // additionsSlotDescription
                List.of(EMPTY_SLOT_BOOTS), // baseSlotEmptyIcons
                List.of(MATERIAL_PHANTOM_MEMBRANE) // additionalSlotEmptyIcons
        );
    }

}
