package com.yyon.grapplinghook.config;

import com.google.gson.FieldNamingPolicy;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.config.helper.ConfigUtil;
import com.yyon.grapplinghook.config.helper.IConfig;
import com.yyon.grapplinghook.config.helper.annotation.Category;
import com.yyon.grapplinghook.config.helper.annotation.ContinuousRange;
import com.yyon.grapplinghook.config.helper.annotation.HideInConfigUI;
import com.yyon.grapplinghook.config.helper.annotation.InlineSubCategory;
import com.yyon.grapplinghook.config.helper.impl.DefaultValueTracker;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;

// I reimplemented my autoconfig for YACL implementation from BridgingMod. A todo: is to
// extract the implementations from this and BridgingMod to a separate library. I just cba rn.
// -w
public class GrappleModClientConfig extends DefaultValueTracker implements IConfig {

    public static ConfigClassHandler<GrappleModClientConfig> HANDLER = ConfigClassHandler.createBuilder(GrappleModClientConfig.class)
            .id(GrappleMod.id("client"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(GrappleMod.getDefaultConfigPath().resolve(GrappleMod.MOD_ID + "-client.json"))
                    .setJson5(false)
                    .appendGsonBuilder(builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES))
                    .build())
            .build();

    public GrappleModClientConfig() {
        this.saveDefaults(); // This should be run before /any/ saving or loading occurs.
    }

    @SerialEntry
    @HideInConfigUI
    private int version = 2;


    @Override
    public void upgradeToLatest() {
        //todo: here, fix any old values & copy to new places.


        // then, bump value to latest.
        this.version = ConfigUtil.LATEST_CLIENT_VERSION;
    }

    @SerialEntry @Category("camera") @ContinuousRange(min = 0, max = 90, sliderStep = 0.5f, formatTranslationKey = "config.auto.value.degrees")
    private float wallrunTilt = 10.0f;
    @SerialEntry @Category("camera") @ContinuousRange(min = 0, max = 2, sliderStep = 0.05f, formatTranslationKey = "config.auto.value.seconds")
    private float wallrunAnimationSeconds = 0.5f;

    @InlineSubCategory("volume")
    @SerialEntry @Category("sound") @ContinuousRange(min = 0, max = 100, sliderStep = 1f, formatTranslationKey = "config.auto.value.percentage")
    private float wallrunVolume = 100.0f;
    @SerialEntry @Category("sound") @ContinuousRange(min = 0, max = 100, sliderStep = 1f, formatTranslationKey = "config.auto.value.percentage")
    private float wallrunJumpVolume = 100.0f;
    @SerialEntry @Category("sound") @ContinuousRange(min = 0, max = 100, sliderStep = 1f, formatTranslationKey = "config.auto.value.percentage")
    private float doubleJumpVolume = 100.0f;
    @SerialEntry @Category("sound") @ContinuousRange(min = 0, max = 100, sliderStep = 1f, formatTranslationKey = "config.auto.value.percentage")
    private float slideVolume = 100.0f;
    @SerialEntry @Category("sound") @ContinuousRange(min = 0, max = 100, sliderStep = 1f, formatTranslationKey = "config.auto.value.percentage")
    private float rocketVolume = 100.0f;
    @SerialEntry @Category("sound") @ContinuousRange(min = 0, max = 100, sliderStep = 1f, formatTranslationKey = "config.auto.value.percentage")
    private float enderstaffVolume = 100f;
    @InlineSubCategory("tweaks")
    @SerialEntry @Category("sound") @ContinuousRange(min = 0, max = 2, sliderStep = 0.05f, formatTranslationKey = "config.auto.value.seconds")
    private double wallrunEffectSeconds = 0.35f;

    public int getVersion() {
        return this.version;
    }

    public float getWallrunTilt() {
        return this.wallrunTilt;
    }

    public float getWallrunAnimationSeconds() {
        return this.wallrunAnimationSeconds;
    }

    public double getWallrunEffectSeconds() {
        return this.wallrunEffectSeconds;
    }

    public float getWallrunVolume() {
        return this.wallrunVolume / 100.0f;
    }

    public float getWallrunJumpVolume() {
        return this.wallrunJumpVolume / 100.0f;
    }

    public float getDoubleJumpVolume() {
        return this.doubleJumpVolume / 100.0f;
    }

    public float getSlideVolume() {
        return this.slideVolume / 100.0f;
    }

    public float getRocketVolume() {
        return this.rocketVolume / 100.0f;
    }

    public float getEnderstaffVolume() {
        return this.enderstaffVolume / 100.0f;
    }
}
