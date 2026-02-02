package com.yyon.grapplinghook.config;

import com.google.gson.FieldNamingPolicy;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.config.helper.ConfigUtil;
import com.yyon.grapplinghook.config.helper.FieldCodec;
import com.yyon.grapplinghook.config.helper.FieldCodecGroup;
import com.yyon.grapplinghook.config.helper.IConfig;
import com.yyon.grapplinghook.config.helper.annotation.*;
import com.yyon.grapplinghook.config.helper.impl.DefaultValueTracker;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.SyncServerConfigS2CPayload;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import io.netty.buffer.ByteBuf;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

// I reimplemented my autoconfig for YACL implementation from BridgingMod. A todo: is to
// extract the implementations from this and BridgingMod to a separate library. I just cba rn.
// -w
public class GrappleModCommonConfig extends DefaultValueTracker implements IConfig {

    public static GrappleModCommonConfig serverProvidedConfig = null;

    private static final ConfigClassHandler<GrappleModCommonConfig> INTERNAL_HANDLER = ConfigClassHandler.createBuilder(GrappleModCommonConfig.class)
            .id(GrappleMod.id("common"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(GrappleMod.getDefaultConfigPath().resolve(GrappleMod.MOD_ID + "-common.json"))
                    .setJson5(false)
                    .appendGsonBuilder(builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES))
                    .build())
            .build();

    /** Config handler with added save-load listener hooks. */
    public static final ConfigClassHandler<GrappleModCommonConfig> HANDLER = new WrappedConfigClassHandler<>(
            INTERNAL_HANDLER,
            Set.of(GrappleModCommonConfig::redistributeConfigToClients),
            Set.of(GrappleModCommonConfig::redistributeConfigToClients)
    );

    public static final FieldCodecGroup<RegistryFriendlyByteBuf, GrappleModCommonConfig> STREAM_CODEC = new FieldCodecGroup<>(
            GrappleModCommonConfig::new,
            List.of(
                    new FieldCodec<>(ByteBufCodecs.INT, conf -> conf.version, (config, val) -> config.version = val),
                    new FieldCodec<>(ByteBufCodecs.BOOL, conf -> conf.forceAllowFlight, (config, val) -> config.forceAllowFlight = val),
                    new FieldCodec<>(ByteBufCodecs.FLOAT, conf -> conf.maxStrafeSpeedInAir, (config, val) -> config.maxStrafeSpeedInAir = val),
                    new FieldCodec<>(ByteBufCodecs.DOUBLE, conf -> conf.strafeAcceleration, (config, val) -> config.strafeAcceleration = val),
                    new FieldCodec<>(ByteBufCodecs.BOOL, conf -> conf.overrideMovementInAir, (config, val) -> config.overrideMovementInAir = val),
                    new FieldCodec<>(ByteBufCodecs.BOOL, conf -> conf.hookAffectsEntities, (config, val) -> config.hookAffectsEntities = val),
                    new FieldCodec<>(ByteBufCodecs.FLOAT, conf -> conf.ropeSnapBuffer, (config, val) -> config.ropeSnapBuffer = val),
                    new FieldCodec<>(ByteBufCodecs.FLOAT, conf -> conf.ropeJumpPower, (config, val) -> config.ropeJumpPower = val),
                    new FieldCodec<>(ByteBufCodecs.BOOL, conf -> conf.ropeJumpAtAngle, (config, val) -> config.ropeJumpAtAngle = val),
                    new FieldCodec<>(ByteBufCodecs.INT, conf -> conf.ropeJumpCooldown, (config, val) -> config.ropeJumpCooldown = val),
                    new FieldCodec<>(ByteBufCodecs.FLOAT, conf -> conf.climbSpeed, (config, val) -> config.climbSpeed = val),
                    new FieldCodec<>(ByteBufCodecs.FLOAT, conf -> conf.enderStaffStrength, (config, val) -> config.enderStaffStrength = val),
                    new FieldCodec<>(ByteBufCodecs.INT, conf -> conf.enderStaffCooldown, (config, val) -> config.enderStaffCooldown = val)
            )
    );

    public GrappleModCommonConfig() {
        this.saveDefaults(); // This should be run before /any/ saving or loading occurs.
    }

    @Override
    public void upgradeToLatest() {
        //todo: here, fix any old values & copy to new places.
        // then, bump value to latest.
        this.version = ConfigUtil.LATEST_COMMON_VERSION;
    }

    /**
     * Gets the config instance. It returns the local common config if playing in single player and the
     * common config if connected to a server.
     *
     * Be careful! If executing from the integrated server, this will get the synced config and will not update.
     */
    public static GrappleModCommonConfig get() {
        return GrappleModCommonConfig.isUsingServerProvidedConfig()
                ? GrappleModCommonConfig.serverProvidedConfig
                : HANDLER.instance();
    }

    /** Run when config is received from remote server. Overrides the local config to keep in sync with server. */
    public static void syncIncomingFromServer(GrappleModCommonConfig serverConfig) {
        if(GrappleModCommonConfig.isUsingServerProvidedConfig())
            GrappleMod.LOGGER.info("Replacing existing server-provided mod config with another server-provided config!");

        GrappleMod.LOGGER.info("Using server-provided common config.");
        GrappleModCommonConfig.serverProvidedConfig = serverConfig;
    }

    /** Run when disconnected from remote server. Restores local config. */
    public static void resetConfigFromServer() {
        GrappleMod.LOGGER.info("Using client-provided common config.");
        GrappleModCommonConfig.serverProvidedConfig = null;
    }

    /** Is the local config currently overridden by a server. */
    public static boolean isUsingServerProvidedConfig() {
        return GrappleModCommonConfig.serverProvidedConfig != null;
    }

    //todo: add file listener for dedicated-server reloads.
    /** Broadcasts from the server-side the current config. */
    public static void redistributeConfigToClients() {
        GrappleModCommonConfig config = GrappleModCommonConfig.HANDLER.instance(); // use this over get() to work on integrated server
        SyncServerConfigS2CPayload packet = new SyncServerConfigS2CPayload(config);

        NetworkManager.broadcastToClients(packet);
    }


    // the config:

    @SerialEntry
    @HideInConfigUI
    private int version = 2;

    // gameplay section

    @SerialEntry @Category("gameplay")
    private boolean forceAllowFlight = true;
    @SerialEntry @Category("gameplay") @ContinuousRange(min = 0.0f, max = 1.0f, sliderStep = 0.05f, formatTranslationKey = ConfigUtil.TYPE_SPEED)
    private float maxStrafeSpeedInAir = 0.7f;
    @SerialEntry @Category("gameplay")
    private double strafeAcceleration = 0.015f;
    @SerialEntry @Category("gameplay") // todo: this has been inverted from v1.x -- double check this maps right.
    private boolean overrideMovementInAir = true;

    public boolean forceAllowFlight() {
        return this.forceAllowFlight;
    }

    public double getMaxStrafeSpeedInAir() {
        return this.maxStrafeSpeedInAir;
    }

    public double getStrafeAcceleration() {
        return this.strafeAcceleration;
    }

    public boolean shouldOverrideMovementInAir() {
        return this.overrideMovementInAir;
    }


    // items section -- each item uses a sub-category for a subsection.

    @InlineSubCategory("item.grappling_hook")
    @SerialEntry @Category("item")
    private boolean hookAffectsEntities = true;
    @SerialEntry @Category("item") @ContinuousRange(min = 0.0f, max = 10.0f, sliderStep = 0.5f, formatTranslationKey = ConfigUtil.TYPE_BLOCKS)
    private float ropeSnapBuffer = 5.0f;
    @SerialEntry @Category("item") @ContinuousRange(min = 0.0f, max = 500.0f, sliderStep = 0.5f, formatTranslationKey = ConfigUtil.TYPE_PERCENTAGE)
    private float ropeJumpPower = 100.0f;
    @SerialEntry @Category("item")
    private boolean ropeJumpAtAngle = false;
    @SerialEntry @Category("item") @DiscreteRange(min = 0, max = 100, formatTranslationKey = ConfigUtil.TYPE_TICKS)
    private int ropeJumpCooldown = 0;
    @SerialEntry @Category("item") @ContinuousRange(min = 0.0f, max = 1.0f, sliderStep = 0.05f, formatTranslationKey = ConfigUtil.TYPE_SPEED)
    private float climbSpeed = 0.3f;

    public boolean doHooksAffectEntities() {
        return this.hookAffectsEntities;
    }

    public float getRopeSnapBuffer() {
        return this.ropeSnapBuffer;
    }

    public float getRopeJumpPower() {
        return this.ropeJumpPower / 100.0f;
    }

    public boolean shouldJumpAtAngleFromRope() {
        return this.ropeJumpAtAngle;
    }

    public int getRopeJumpCooldown() {
        return this.ropeJumpCooldown;
    }

    public float getClimbSpeed() {
        return this.climbSpeed;
    }


    @InlineSubCategory("item.enderstaff")
    @SerialEntry @Category("item") @ContinuousRange(min = 0.0f, max = 500.0f, sliderStep = 0.5f, formatTranslationKey = ConfigUtil.TYPE_PERCENTAGE)
    private float enderStaffStrength = 100.0f;
    @SerialEntry @Category("item") @DiscreteRange(min = 0, max = 100, formatTranslationKey = ConfigUtil.TYPE_TICKS)
    private int enderStaffCooldown = 100;

    public float getEnderStaffStrength() {
        return this.enderStaffStrength / 100.0f * 1.5f;
    }

    public int getEnderStaffCooldown() {
        return this.enderStaffCooldown;
    }
}
