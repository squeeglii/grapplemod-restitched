package com.yyon.grapplinghook.config;

import com.yyon.grapplinghook.content.registry.GrappleModCustomizationCategories;
import com.yyon.grapplinghook.customization.CustomizationAvailability;
import com.yyon.grapplinghook.customization.CustomizationCategory;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.DropdownBoxEntry;
import me.shedaniel.clothconfig2.impl.builders.DropdownMenuBuilder;
import me.shedaniel.clothconfig2.impl.builders.EnumSelectorBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GrappleModConfig {

    public static final Component YEET = Component.translatable("config.grapplemod.yeet");


    public static final Component CUSTOMIZATION_DEFAULT_VALUE = Component.translatable("config.grapplemod.customization.default_value");
    public static final Component CUSTOMIZATION_AVAILIBILITY = Component.translatable("config.grapplemod.customization.availability");


    public Screen buildConfigScreen(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create();

        // Both sides
        this.configureCustomizations(builder);
        this.configureAbilities(builder);

        // Client-only - do not sync
        this.configureCamera(builder);
        this.configureSounds(builder);

        // Both sides
        this.configureMiscellaneous(builder);

        builder.setParentScreen(parent);

        return builder.build();
    }

    private void configureCustomizations(ConfigBuilder builder) {
        ConfigCategory category = section(builder, "customizations");

        GrappleModCustomizationCategories.getModCategories().forEach(modCategory -> {
            SubCategoryBuilder categorySub = ConfigEntryBuilder.create()
                    .startSubCategory(modCategory.get().getName());

            this.configurePropertyCategory(categorySub, modCategory.get());

            category.addEntry(categorySub.build());
        });
    }

    private void configurePropertyCategory(SubCategoryBuilder builder, CustomizationCategory category) {
        category.getLinkedProperties().forEach(categoryProperty -> {
            SubCategoryBuilder categorySub = ConfigEntryBuilder.create()
                    .startSubCategory(categoryProperty.getDisplayName());

            categorySub.add(new EnumSelectorBuilder<>(
                    YEET, CUSTOMIZATION_AVAILIBILITY,
                    CustomizationAvailability.class,
                    CustomizationAvailability.ALLOWED)
                    .setEnumNameProvider(val -> ((CustomizationAvailability) val).getTranslationString())
                    .build()
            );

            categorySub.add() // default value provider
            // extra providers

            builder.add(categorySub.build());
        });
    }

    // Contains grappling hook, enchantments, and other items
    private void configureAbilities(ConfigBuilder builder) {
        ConfigCategory category = section(builder, "abilities");

    }

    private void configureSounds(ConfigBuilder builder) {
        ConfigCategory category = section(builder, "sounds");


    }

    private void configureCamera(ConfigBuilder builder) {
        ConfigCategory category = section(builder, "camera");
    }

    private void configureMiscellaneous(ConfigBuilder builder) {
        ConfigCategory category = section(builder, "misc");
    }

    private static ConfigCategory section(ConfigBuilder builder, String sectionName) {
        Component name = Component.translatable("config.grapplemod.section.%s.name".formatted(sectionName));
        return builder.getOrCreateCategory(name);
    }

}
