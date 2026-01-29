package com.yyon.grapplinghook.config;

import com.yyon.grapplinghook.config.helper.ConfigUtil;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
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
        this.layout.addTitleHeader(TITLE, this.font);

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

        this.layout.addToContents(commonConfig, settings -> {});
        this.layout.addToContents(clientConfig, settings -> {});


        Button backButton = Button.builder(
                ConfigUtil.BACK_TRANSLATION,
                button -> this.onClose()
        ).build();

        this.layout.addToFooter(backButton, settings -> {});

        this.layout.visitWidgets(this::addRenderableWidget);
        this.repositionElements();
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.lastScreen);
    }
}
