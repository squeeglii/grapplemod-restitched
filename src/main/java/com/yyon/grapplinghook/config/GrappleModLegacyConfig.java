package com.yyon.grapplinghook.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.annotation.ConfigEntry.BoundedDiscrete;
import me.shedaniel.autoconfig.annotation.ConfigEntry.Gui.Tooltip;

@Config(name = "grapplemod")
public class GrappleModLegacyConfig implements ConfigData {

	public static class Config {
		@ConfigEntry.Gui.CollapsibleObject
		@Tooltip
		public GrapplingHook grapplinghook = new GrapplingHook();
		public static class GrapplingHook {

			@ConfigEntry.Gui.CollapsibleObject
			@Tooltip
			public Blocks blocks = new Blocks();
			public static class Blocks {
				@Tooltip
				public String grapplingBlocks = "any";
				@Tooltip
				public String grapplingNonBlocks = "none";
				@Tooltip
				public String grappleBreakBlocks = "none";
			}
			
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
			@Tooltip
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
				@BoundedDiscrete(max=3, min=0)
				@Tooltip
				public int enchant_rarity_wallrun = 0;
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
				@BoundedDiscrete(max=3, min=0)
				@Tooltip
				public int enchant_rarity_double_jump = 0;
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
				@BoundedDiscrete(max=3, min=0)
				@Tooltip
				public int enchant_rarity_sliding = 0;
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
	
    @ConfigEntry.Gui.CollapsibleObject
    @Tooltip
	public Config options = new Config(); // local options
	
    @ConfigEntry.Gui.Excluded
	private static Config serverOptions = null;
	
	public static class ClientConfig {
		@ConfigEntry.Gui.CollapsibleObject
		@Tooltip
		public Camera camera = new Camera();
		public static class Camera {
			@Tooltip
			public float wallrun_camera_tilt_degrees = 10;
			@Tooltip
			public float wallrun_camera_animation_s = 0.5f;
		}
		
		@ConfigEntry.Gui.CollapsibleObject
		@Tooltip
		public Sounds sounds = new Sounds();
		public static class Sounds {
			@Tooltip
			public double wallrun_sound_effect_time_s = 0.35;
			@Tooltip
			public float wallrun_sound_volume = 1.0F;
			@Tooltip
			public float doublejump_sound_volume = 1.0F;
			@Tooltip
			public float slide_sound_volume = 1.0F;
			@Tooltip
			public float wallrunjump_sound_volume = 1.0F;
			@Tooltip
			public float rocket_sound_volume = 1.0F;
			@Tooltip
			public float enderstaff_sound_volume = 1.0F;
		}
	}
	
    @ConfigEntry.Gui.CollapsibleObject
	@Tooltip
	public ClientConfig clientOptions = new ClientConfig(); // client-only options, don't need to sync with server

	public static Config getConf() {
		if (serverOptions == null) {
			return AutoConfig.getConfigHolder(GrappleModLegacyConfig.class).getConfig().options;
		} else {
			return serverOptions;
		}
	}
	
	public static void setServerOptions(Config newserveroptions) {
		serverOptions = newserveroptions;
	}
	
	public static ClientConfig getClientConf() {
		return AutoConfig.getConfigHolder(GrappleModLegacyConfig.class).getConfig().clientOptions;
	}
}
