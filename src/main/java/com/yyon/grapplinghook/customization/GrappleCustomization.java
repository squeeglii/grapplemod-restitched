package com.yyon.grapplinghook.customization;

import com.yyon.grapplinghook.client.ClientCheck;
import com.yyon.grapplinghook.config.GrappleModConfig;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class GrappleCustomization {
	public static final String[] booleanoptions = new String[] {"phaserope", "motor", "motorwhencrouching", "motorwhennotcrouching", "smartmotor", "enderstaff", "repel", "attract", "doublehook", "smartdoublemotor", "motordampener", "reelin", "pullbackwards", "oneropepull", "sticky", "detachonkeyrelease", "rocket"};
	public static final String[] doubleoptions = new String[] {"maxlen", "hookgravity", "throwspeed", "motormaxspeed", "motoracceleration", "playermovementmult", "repelforce", "attractradius", "angle", "sneakingangle", "verticalthrowangle", "sneakingverticalthrowangle", "rocket_force", "rocket_active_time", "rocket_refuel_ratio", "rocket_vertical_angle"};
	
	// rope
	public double maxlen;
	public boolean phaserope;
	public boolean sticky;

	// hook thrower
	public double hookgravity;
	public double throwspeed;
	public boolean reelin;
	public double verticalthrowangle;
	public double sneakingverticalthrowangle;
	public boolean detachonkeyrelease;

	// motor
	public boolean motor;
	public double motormaxspeed;
	public double motoracceleration;
	public boolean motorwhencrouching;
	public boolean motorwhennotcrouching;
	public boolean smartmotor;
	public boolean motordampener;
	public boolean pullbackwards;
	
	// swing speed
	public double playermovementmult;

	// ender staff
	public boolean enderstaff;

	// forcefield
	public boolean repel;
	public double repelforce;
	
	// hook magnet
	public boolean attract;
	public double attractradius;
	
	// double hook
	public boolean doublehook;
	public boolean smartdoublemotor;
	public double angle;
	public double sneakingangle;
	public boolean oneropepull;
	
	// rocket
	public boolean rocket;
	public double rocket_force;
	public double rocket_active_time;
	public double rocket_refuel_ratio;
	public double rocket_vertical_angle;
	
	public enum UpgradeCategory {
		ROPE ("rope"), 
		THROW ("throw"), 
		MOTOR ("motor"), 
		SWING ("swing"), 
		STAFF ("staff"), 
		FORCEFIELD ("forcefield"), 
		MAGNET ("magnet"), 
		DOUBLE ("double"),
		LIMITS ("limits"),
		ROCKET ("rocket");
		
		private final String nameUnlocalized;
		UpgradeCategory(String name) {
			this.nameUnlocalized = name;
		}
		
		public String getName() {
			if (ClientCheck.isClientLoaded()) {
				return Component.translatable("grapplemod.upgradecategories." + this.nameUnlocalized).getString();
			} else {
				return nameUnlocalized;
			}
		}
		
		public static UpgradeCategory fromInt(int i) {
			return UpgradeCategory.values()[i];
		}
		public int toInt() {
			for (int i = 0; i < size(); i++) {
				if (UpgradeCategory.values()[i] == this) {
					return i;
				}
			}
			return -1;
		}
		public static int size() {
			return UpgradeCategory.values().length;
		}
		public Item getItem() {
			return switch (this) {
				case ROPE -> GrappleModItems.ROPE_UPGRADE.get();
				case THROW -> GrappleModItems.THROW_UPGRADE.get();
				case MOTOR -> GrappleModItems.MOTOR_UPGRADE.get();
				case SWING -> GrappleModItems.SWING_UPGRADE.get();
				case STAFF -> GrappleModItems.ENDER_STAFF_UPGRADE.get();
				case FORCEFIELD -> GrappleModItems.FORCE_FIELD.get();
				case MAGNET -> GrappleModItems.MAGNET_UPGRADE.get();
				case DOUBLE -> GrappleModItems.DOUBLE_UPGRADE.get();
				case LIMITS -> GrappleModItems.LIMITS_UPGRADE.get();
				case ROCKET -> GrappleModItems.ROCKET_UPGRADE.get();
			};
		}
	}
	
	public GrappleCustomization() {
		this.setDefaults();
	}
	
	public void setDefaults() {
		for (String option : booleanoptions) {
			GrappleModConfig.Config.GrapplingHook.Custom.BooleanCustomizationOption optionconfig = getBooleanConfig(option);
			this.setBoolean(option, optionconfig.default_value);
		}

		for (String option : doubleoptions) {
			GrappleModConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption optionconfig = getDoubleConfig(option);
			this.setDouble(option, optionconfig.default_value);
		}
	}
	
	public static GrappleModConfig.Config.GrapplingHook.Custom.BooleanCustomizationOption getBooleanConfig(String option) {
		return switch (option) {
			case "phaserope" -> GrappleModConfig.getConf().grapplinghook.custom.rope.phaserope;
			case "motor" -> GrappleModConfig.getConf().grapplinghook.custom.motor.motor;
			case "motorwhencrouching" -> GrappleModConfig.getConf().grapplinghook.custom.motor.motorwhencrouching;
			case "motorwhennotcrouching" -> GrappleModConfig.getConf().grapplinghook.custom.motor.motorwhennotcrouching;
			case "smartmotor" -> GrappleModConfig.getConf().grapplinghook.custom.motor.smartmotor;
			case "enderstaff" -> GrappleModConfig.getConf().grapplinghook.custom.enderstaff.enderstaff;
			case "repel" -> GrappleModConfig.getConf().grapplinghook.custom.forcefield.repel;
			case "attract" -> GrappleModConfig.getConf().grapplinghook.custom.magnet.attract;
			case "doublehook" -> GrappleModConfig.getConf().grapplinghook.custom.doublehook.doublehook;
			case "smartdoublemotor" -> GrappleModConfig.getConf().grapplinghook.custom.doublehook.smartdoublemotor;
			case "motordampener" -> GrappleModConfig.getConf().grapplinghook.custom.motor.motordampener;
			case "reelin" -> GrappleModConfig.getConf().grapplinghook.custom.hookthrower.reelin;
			case "pullbackwards" -> GrappleModConfig.getConf().grapplinghook.custom.motor.pullbackwards;
			case "oneropepull" -> GrappleModConfig.getConf().grapplinghook.custom.doublehook.oneropepull;
			case "sticky" -> GrappleModConfig.getConf().grapplinghook.custom.rope.sticky;
			case "detachonkeyrelease" -> GrappleModConfig.getConf().grapplinghook.custom.hookthrower.detachonkeyrelease;
			case "rocket" -> GrappleModConfig.getConf().grapplinghook.custom.rocket.rocketenabled;
			default -> null;
		};
	}

	public static GrappleModConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption getDoubleConfig(String option) {
		return switch (option) {
			case "maxlen" -> GrappleModConfig.getConf().grapplinghook.custom.rope.maxlen;
			case "hookgravity" -> GrappleModConfig.getConf().grapplinghook.custom.hookthrower.hookgravity;
			case "throwspeed" -> GrappleModConfig.getConf().grapplinghook.custom.hookthrower.throwspeed;
			case "motormaxspeed" -> GrappleModConfig.getConf().grapplinghook.custom.motor.motormaxspeed;
			case "motoracceleration" -> GrappleModConfig.getConf().grapplinghook.custom.motor.motoracceleration;
			case "playermovementmult" -> GrappleModConfig.getConf().grapplinghook.custom.swing.playermovementmult;
			case "repelforce" -> GrappleModConfig.getConf().grapplinghook.custom.forcefield.repelforce;
			case "attractradius" -> GrappleModConfig.getConf().grapplinghook.custom.magnet.attractradius;
			case "angle" -> GrappleModConfig.getConf().grapplinghook.custom.doublehook.angle;
			case "sneakingangle" -> GrappleModConfig.getConf().grapplinghook.custom.doublehook.sneakingangle;
			case "verticalthrowangle" -> GrappleModConfig.getConf().grapplinghook.custom.hookthrower.verticalthrowangle;
			case "sneakingverticalthrowangle" ->
					GrappleModConfig.getConf().grapplinghook.custom.hookthrower.sneakingverticalthrowangle;
			case "rocket_force" -> GrappleModConfig.getConf().grapplinghook.custom.rocket.rocket_force;
			case "rocket_active_time" -> GrappleModConfig.getConf().grapplinghook.custom.rocket.rocket_active_time;
			case "rocket_refuel_ratio" -> GrappleModConfig.getConf().grapplinghook.custom.rocket.rocket_refuel_ratio;
			case "rocket_vertical_angle" -> GrappleModConfig.getConf().grapplinghook.custom.rocket.rocket_vertical_angle;
			default -> null;
		};
	}

	public CompoundTag writeNBT() {
		CompoundTag compound = new CompoundTag();
		for (String option : booleanoptions) {
			compound.putBoolean(option, this.getBoolean(option));
		}
		for (String option : doubleoptions) {
			compound.putDouble(option, this.getDouble(option));
		}
		compound.putLong("crc32", this.getChecksum());
		return compound;
	}
	
	public void loadNBT(CompoundTag compound) {
		for (String option : booleanoptions) {
			if (compound.contains(option)) {
				this.setBoolean(option, compound.getBoolean(option));
			}
		}
		for (String option : doubleoptions) {
			if (compound.contains(option)) {
				this.setDouble(option, compound.getDouble(option));
			}
		}
		if (compound.contains("crc32")) {
			long recordedChecksum = compound.getLong("crc32");
			if (this.getChecksum() != recordedChecksum) {
				GrappleMod.LOGGER.error("Error checksum reading from NBT");
				this.setDefaults();
			}
		}
	}
	
	public void setBoolean(String option, boolean bool) {
		switch (option) {
			case "phaserope" -> this.phaserope = bool;
			case "motor" -> this.motor = bool;
			case "motorwhencrouching" -> this.motorwhencrouching = bool;
			case "motorwhennotcrouching" -> this.motorwhennotcrouching = bool;
			case "smartmotor" -> this.smartmotor = bool;
			case "enderstaff" -> this.enderstaff = bool;
			case "repel" -> this.repel = bool;
			case "attract" -> this.attract = bool;
			case "doublehook" -> this.doublehook = bool;
			case "smartdoublemotor" -> this.smartdoublemotor = bool;
			case "motordampener" -> this.motordampener = bool;
			case "reelin" -> this.reelin = bool;
			case "pullbackwards" -> this.pullbackwards = bool;
			case "oneropepull" -> this.oneropepull = bool;
			case "sticky" -> this.sticky = bool;
			case "detachonkeyrelease" -> this.detachonkeyrelease = bool;
			case "rocket" -> this.rocket = bool;
			default -> GrappleMod.LOGGER.warn("Option doesn't exist: " + option);
		}
	}
	
	public boolean getBoolean(String option) {
		return switch (option) {
			case "phaserope" -> this.phaserope;
			case "motor" -> this.motor;
			case "motorwhencrouching" -> this.motorwhencrouching;
			case "motorwhennotcrouching" -> this.motorwhennotcrouching;
			case "smartmotor" -> this.smartmotor;
			case "enderstaff" -> this.enderstaff;
			case "repel" -> this.repel;
			case "attract" -> this.attract;
			case "doublehook" -> this.doublehook;
			case "smartdoublemotor" -> this.smartdoublemotor;
			case "motordampener" -> this.motordampener;
			case "reelin" -> this.reelin;
			case "pullbackwards" -> this.pullbackwards;
			case "oneropepull" -> this.oneropepull;
			case "sticky" -> this.sticky;
			case "detachonkeyrelease" -> this.detachonkeyrelease;
			case "rocket" -> this.rocket;
			default -> {
				GrappleMod.LOGGER.warn("Option doesn't exist: " + option);
				yield false;
			}
		};
	}
	
	public void setDouble(String option, double d) {
		switch (option) {
			case "maxlen" -> this.maxlen = d;
			case "hookgravity" -> this.hookgravity = d;
			case "throwspeed" -> this.throwspeed = d;
			case "motormaxspeed" -> this.motormaxspeed = d;
			case "motoracceleration" -> this.motoracceleration = d;
			case "playermovementmult" -> this.playermovementmult = d;
			case "repelforce" -> this.repelforce = d;
			case "attractradius" -> this.attractradius = d;
			case "angle" -> this.angle = d;
			case "sneakingangle" -> this.sneakingangle = d;
			case "verticalthrowangle" -> this.verticalthrowangle = d;
			case "sneakingverticalthrowangle" -> this.sneakingverticalthrowangle = d;
			case "rocket_force" -> this.rocket_force = d;
			case "rocket_active_time" -> this.rocket_active_time = d;
			case "rocket_refuel_ratio" -> this.rocket_refuel_ratio = d;
			case "rocket_vertical_angle" -> this.rocket_vertical_angle = d;
			default -> GrappleMod.LOGGER.warn("Option doesn't exist: " + option);
		}
	}
	
	public double getDouble(String option) {
		return switch (option) {
			case "maxlen" -> this.maxlen;
			case "hookgravity" -> this.hookgravity;
			case "throwspeed" -> this.throwspeed;
			case "motormaxspeed" -> this.motormaxspeed;
			case "motoracceleration" -> this.motoracceleration;
			case "playermovementmult" -> this.playermovementmult;
			case "repelforce" -> this.repelforce;
			case "attractradius" -> this.attractradius;
			case "angle" -> this.angle;
			case "sneakingangle" -> this.sneakingangle;
			case "verticalthrowangle" -> this.verticalthrowangle;
			case "sneakingverticalthrowangle" -> this.sneakingverticalthrowangle;
			case "rocket_force" -> this.rocket_force;
			case "rocket_active_time" -> this.rocket_active_time;
			case "rocket_refuel_ratio" -> this.rocket_refuel_ratio;
			case "rocket_vertical_angle" -> this.rocket_vertical_angle;
			default -> {
				GrappleMod.LOGGER.warn("Option doesn't exist: " + option);
				yield 0.0D;
			}
		};
	}
	
	public long getChecksum() {
		Checksum checker = new CRC32();
		for (String option : booleanoptions) {
			checker.update(this.getBoolean(option) ? 1 : 0);
		}
		for (String option : doubleoptions) {
			// https://stackoverflow.com/questions/13071777/convert-double-to-byte-array
			checker.update(ByteBuffer.allocate(8).putDouble(this.getDouble(option)).array());
		}
		checker.update(54902349);
		return checker.getValue();
	}
	
	public void writeToBuf(ByteBuf buf) {
		for (String option : booleanoptions) {
			buf.writeBoolean(this.getBoolean(option));
		}
		for (String option : doubleoptions) {
			buf.writeDouble(this.getDouble(option));
		}
		buf.writeLong(this.getChecksum());
	}
	
	public void readFromBuf(ByteBuf buf) {
		for (String option : booleanoptions) {
			this.setBoolean(option, buf.readBoolean());
		}
		for (String option : doubleoptions) {
			this.setDouble(option, buf.readDouble());
		}
		long recordedChecksum = buf.readLong();
		if (this.getChecksum() != recordedChecksum) {
			GrappleMod.LOGGER.error("Error checksum reading from buffer");
			this.setDefaults();
		}
	}

	public String getName(String option) {
		return "grapplecustomization." + option;
	}
	
	public String getDescription(String option) {
		return "grapplecustomization." + option + ".desc";
	}
	
	public boolean isOptionValid(String option) {
		if(option == null) return false;

		if (option.equals("motormaxspeed") || option.equals("motoracceleration") || option.equals("motorwhencrouching") || option.equals("motorwhennotcrouching") || option.equals("smartmotor") || option.equals("motordampener") || option.equals("pullbackwards")) {
			return this.motor;
		}
		
		if (option.equals("sticky")) {
			return !this.phaserope;
		}
		
		else if (option.equals("sneakingangle")) {
			return this.doublehook && !this.reelin;
		}
		
		else if (option.equals("repelforce")) {
			return this.repel;
		}
		
		else if (option.equals("attractradius")) {
			return this.attract;
		}
		
		else if (option.equals("angle")) {
			return this.doublehook;
		}
		
		else if (option.equals("smartdoublemotor") || option.equals("oneropepull")) {
			return this.doublehook && this.motor;
		}
		
		else if (option.equals("rocket_active_time") || option.equals("rocket_refuel_ratio") || option.equals("rocket_force") || option.equals("rocket_vertical_angle")) {
			return this.rocket;
		}
		
		return true;
	}
	
	public static double getMaxFromConfig(String option, int upgrade) {
		GrappleModConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption configoption = getDoubleConfig(option);
		return upgrade == 1 ? configoption.max_upgraded : configoption.max;
	}
	
	public static double getMinFromConfig(String option, int upgrade) {
		GrappleModConfig.Config.GrapplingHook.Custom.DoubleCustomizationOption configoption = getDoubleConfig(option);
		return upgrade == 1 ? configoption.min_upgraded : configoption.min;
	}
	
	public static int optionEnabledInConfig(String option) {
		GrappleModConfig.Config.GrapplingHook.Custom.BooleanCustomizationOption configoption = getBooleanConfig(option);
		if (configoption != null) {
			return configoption.enabled;
		}
		return getDoubleConfig(option).enabled;
	}
	
	public boolean equals(GrappleCustomization other) {
		for (String option : booleanoptions) {
			if (this.getBoolean(option) != other.getBoolean(option)) {
				return false;
			}
		}
		for (String option : doubleoptions) {
			if (this.getDouble(option) != other.getDouble(option)) {
				return false;
			}
		}
		return true;
	}
	
	public static GrappleCustomization DEFAULT = new GrappleCustomization();
}
