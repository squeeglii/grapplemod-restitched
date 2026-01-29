package com.yyon.grapplinghook.config.helper;

import net.minecraft.network.chat.Component;

public class ConfigUtil {

    public static final int LATEST_CLIENT_VERSION = 2;
    public static final int LATEST_COMMON_VERSION = 2;

    public static final String GRAPPLE_MOD_COMMON_TRANSLATION_TITLE = "config.grapplemod.common.title";
    public static final String GRAPPLE_MOD_CLIENT_TRANSLATION_TITLE = "config.grapplemod.client.title";

    public static final Component COMMON_CONFIG_TRANSLATION = Component.translatable("config.auto.common.title");
    public static final Component CLIENT_CONFIG_TRANSLATION = Component.translatable("config.auto.client.title");

    public static final String TRANSLATION_CATEGORY_NAME = "config.grapplemod.category.%s";
    public static final String TRANSLATION_CATEGORY_TOOLTIP = "config.grapplemod.category.%s.tooltip";
    public static final String TRANSLATION_SUB_CATEGORY_NAME = "config.grapplemod.sub_category.%s";
    public static final String TRANSLATION_OPTION_NAME = "config.grapplemod.option.%s";
    public static final String TRANSLATION_OPTION_DESCRIPTION = "config.grapplemod.option.%s.description.%s";

    public static final String TYPE_PERCENTAGE = "config.auto.value.percentage";
    public static final String TYPE_SECONDS = "config.auto.value.seconds";
    public static final String TYPE_TICKS = "config.auto.value.ticks";
    public static final String TYPE_DEGREES = "config.auto.value.degrees";
    public static final String TYPE_BLOCKS = "config.auto.value.blocks";
    public static final String TYPE_SPEED = "config.auto.value.speed";

}
