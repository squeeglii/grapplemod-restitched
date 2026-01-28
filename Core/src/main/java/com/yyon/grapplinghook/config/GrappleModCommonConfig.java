package com.yyon.grapplinghook.config;

import com.google.gson.FieldNamingPolicy;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.config.helper.ConfigUtil;
import com.yyon.grapplinghook.config.helper.IConfig;
import com.yyon.grapplinghook.config.helper.annotation.HideInConfigUI;
import com.yyon.grapplinghook.config.helper.impl.DefaultValueTracker;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

// I reimplemented my autoconfig for YACL implementation from BridgingMod. A todo: is to
// extract the implementations from this and BridgingMod to a separate library. I just cba rn.
// -w
public class GrappleModCommonConfig extends DefaultValueTracker implements IConfig {

    public static ConfigClassHandler<GrappleModCommonConfig> HANDLER = ConfigClassHandler.createBuilder(GrappleModCommonConfig.class)
            .id(GrappleMod.id("main"))
            .serializer(config -> GsonConfigSerializerBuilder.create(config)
                    .setPath(GrappleMod.getDefaultConfigPath().resolve(GrappleMod.MOD_ID + "-common.json"))
                    .setJson5(false)
                    .appendGsonBuilder(builder -> builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES))
                    .build())
            .build();

    public static final StreamCodec<RegistryFriendlyByteBuf, GrappleModCommonConfig> STREAM_CODEC = StreamCodec.composite(

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


    public static void syncIncomingFromServer(GrappleModCommonConfig config) {
        //todo: SYNC
        throw new UnsupportedOperationException("Config Sync unimplemented!");
    }



    @SerialEntry
    @HideInConfigUI
    private int version = 2;


    //todo: fix annotations & groupings

    @ConfigEntry.Gui.CollapsibleObject
    @Tooltip
    public GrapplingHook grapplinghook = new GrapplingHook();
    public static class GrapplingHook {

        @ConfigEntry.Gui.CollapsibleObject
        @Tooltip
        public Other other = new Other();
        public static class Other {
            @Tooltip
            public boolean hookaffectsentities = true;
            @Tooltip
            public double rope_snap_buffer = 5;
            @Tooltip
            public int default_durability = 500;
            @Tooltip
            public double rope_jump_power = 1;
            @Tooltip
            public boolean rope_jump_at_angle = false;
            @Tooltip
            public double rope_jump_cooldown_s = 0;
            @Tooltip
            public double climb_speed = 0.3;
        }
    }

    @ConfigEntry.Gui.CollapsibleObject
    @Tooltip
    public LongFallBoots longfallboots = new LongFallBoots();
    public static class LongFallBoots {

        @Comment("This option is now ignored (will be removed in the next release) - see Server Features in the datapack file 'grapplemod:content/enabled_server_features.json' for its replacement.")
        @Tooltip
        @Deprecated
        public boolean longfallbootsrecipe = true;

    }

    @ConfigEntry.Gui.CollapsibleObject
    @Tooltip
    public EnderStaff enderstaff = new EnderStaff();
    public static class EnderStaff {
        @Tooltip
        public double ender_staff_strength = 1.5;
        @Tooltip
        public int ender_staff_recharge = 100;
    }

    @ConfigEntry.Gui.CollapsibleObject
    @Tooltip
    public Enchantments enchantments = new Enchantments();
    public static class Enchantments {
        @ConfigEntry.Gui.CollapsibleObject
        @Tooltip
        public Wallrun wallrun = new Wallrun();
        public static class Wallrun {
            @Tooltip
            public double wall_jump_up = 0.7;
            @Tooltip
            public double wall_jump_side = 0.4;
            @Tooltip
            public double max_wallrun_time = 3;
            @Tooltip
            public double wallrun_speed = 0.1;
            @Tooltip
            public double wallrun_max_speed = 0.7;
            @Tooltip
            public double wallrun_drag = 0.01;
            @Tooltip
            public double wallrun_min_speed = 0;
        }

        @ConfigEntry.Gui.CollapsibleObject
        @Tooltip
        public DoubleJump doublejump = new DoubleJump();
        public static class DoubleJump {
            @Tooltip
            public double doublejumpforce = 0.8;
            @Tooltip
            public boolean doublejump_relative_to_falling = false;
            @Tooltip
            public double dont_doublejump_if_falling_faster_than = 99999999.0;
        }

        @ConfigEntry.Gui.CollapsibleObject
        @Tooltip
        public Slide slide = new Slide();
        public static class Slide {
            @Tooltip
            public double slidingjumpforce =  0.6;
            @Tooltip
            public double sliding_friction = 1 / 150F;
            @Tooltip
            public double sliding_min_speed = 0.15;
            @Tooltip
            public double sliding_end_min_speed = 0.01;
        }
    }

    @ConfigEntry.Gui.CollapsibleObject
    @Tooltip
    public Other other = new Other();
    public static class Other {
        @Tooltip
        public boolean override_allowflight = true;
        @Tooltip
        public double airstrafe_max_speed = 0.7;
        @Tooltip
        public double airstrafe_acceleration = 0.015;
        @Tooltip
        public boolean dont_override_movement_in_air = false;
    }

}
