package com.yyon.grapplinghook.customization;

import com.yyon.grapplinghook.client.ClientCheck;
import com.yyon.grapplinghook.config.GrappleModConfig;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class CustomizationVolume {
	
	public CustomizationVolume() {
		this.setDefaults();
	}
	
	public void setDefaults() {

	}

	public CompoundTag writeNBT() {
		CompoundTag compound = new CompoundTag();
		//TODO: Put all config options
		// Checksum relies on every key being present.
		compound.putLong("crc32", this.getChecksum());
		return compound;
	}
	
	public void loadNBT(CompoundTag compound) {
		//TODO: Set all from NBT
		if (compound.contains("crc32")) {
			long recordedChecksum = compound.getLong("crc32");
			if (this.getChecksum() != recordedChecksum) {
				GrappleMod.LOGGER.error("Error checksum reading from NBT");
				this.setDefaults();
			}
		}
	}
	
	public void setBoolean(CustomizationProperty<Boolean> property, boolean bool) {

	}
	
	public boolean getBoolean(CustomizationProperty<Boolean> option) {

	}
	
	public void setDouble(String option, double d) {

	}
	
	public double getDouble(String option) {

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


	// read/write from buf relied on the keys being in a consistent order.
	public void writeToBuf(ByteBuf buf) {
		//TODO: write
		buf.writeLong(this.getChecksum());
	}
	
	public void readFromBuf(ByteBuf buf) {
		//TODO: read
		long recordedChecksum = buf.readLong();
		if (this.getChecksum() != recordedChecksum) {
			GrappleMod.LOGGER.error("Error checksum reading from buffer");
			this.setDefaults();
		}
	}
	
	public boolean equals(CustomizationVolume other) {
		// Compare options
		return true;
	}
	
	public static CustomizationVolume DEFAULT = new CustomizationVolume();
}
