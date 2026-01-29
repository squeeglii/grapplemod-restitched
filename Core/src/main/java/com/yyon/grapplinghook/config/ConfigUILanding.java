package com.yyon.grapplinghook.config;

import com.yyon.grapplinghook.config.helper.ConfigUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ConfigUILanding extends Screen {

    private static final Component TITLE = Component.translatable("config.grapplemod.landing.title");

    private final Screen lastScreen;
    private final HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);

    protected ConfigUILanding(Screen lastScreen) {
        super(ConfigUILanding.TITLE);
        this.lastScreen = lastScreen;
    }


    @Override
    protected void init() {
        this.layout.addTitleHeader(this.title, this.font);

        Button commonConfig = Button.builder(
                ConfigUtil.COMMON_CONFIG_TRANSLATION,
                button -> {
                    Screen configScreen = ConfigUI.buildCommonConfig().generateScreen(this);
                    this.minecraft.setScreen(configScreen);
                }
        ).build();

        Button clientConfig = Button.builder(
                ConfigUtil.CLIENT_CONFIG_TRANSLATION,
                button -> {
                    Screen configScreen = ConfigUI.buildClientConfig().generateScreen(this);
                    this.minecraft.setScreen(configScreen);
                }
        ).build();

        LinearLayout contents = LinearLayout.vertical().spacing(10);
        contents.addChild(commonConfig);
        contents.addChild(clientConfig);

        this.layout.addToContents(contents);

        Button backButton = Button.builder(
                CommonComponents.GUI_CANCEL,
                button -> this.onClose()
        ).build();

        this.layout.addToFooter(backButton, settings -> {});

        this.layout.visitWidgets(this::addRenderableWidget);
        this.layout.arrangeElements();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
}
