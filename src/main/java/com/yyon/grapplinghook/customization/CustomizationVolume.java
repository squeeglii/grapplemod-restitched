package com.yyon.grapplinghook.customization;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.registry.GrappleModMetaRegistry;
import com.yyon.grapplinghook.customization.template.PropertyOverride;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class CustomizationVolume {

	private HashMap<CustomizationProperty<?>, Object> values;

	
	public CustomizationVolume() {
		this.setDefaults();
	}
	
	public void setDefaults() {
		this.values = new HashMap<>();
	}

	@SuppressWarnings("unchecked") // Types are already verified
	public <T> CompoundTag writeToNBT() {
		CompoundTag base = new CompoundTag();
		CompoundTag properties = new CompoundTag();

		this.values.forEach((k, v) -> {
			CustomizationProperty<T> key = (CustomizationProperty<T>) k;
			key.saveValueToTag(properties, (T) v);
		});

		base.put("properties", properties);
		base.putLong("crc32", this.getChecksum());
		return base;
	}
	
	public void loadFromNBT(CompoundTag compound) {
		Tag propTag = compound.get("properties");
		if(!(propTag instanceof CompoundTag propertiesTag)) {
			this.setDefaults();
			return;
		}

		propertiesTag.getAllKeys().forEach(k ->
			tryParseProperty(k).ifPresent(property -> {
				Object val = property.loadValueFromTag(propertiesTag);

				if(val == null) {
					GrappleMod.LOGGER.warn("NBT Parse Error: value for item property '%s'".formatted(property.getIdentifier()));
					return;
				}

				this.setUnsafe(property, val);
			})
		);

		if (!compound.contains("crc32")) return;

		long recordedChecksum = compound.getLong("crc32");
		if (this.getChecksum() != recordedChecksum) {
			String keyCollection = Arrays.toString(propertiesTag.getAllKeys().toArray());
			GrappleMod.LOGGER.error("Error checksum reading from NBT! Keys present: %s".formatted(keyCollection));
			this.setDefaults();
		}
	}
	
	public <T> void set(CustomizationProperty<T> property, T value) {
		if(value == null) {
			this.reset(property);
			return;
		}

		this.setUnsafe(property, value);
	}

	public <T> void set(PropertyOverride<T> override) {
		this.setUnsafe(override.property(), override.value());
	}

	private void setUnsafe(CustomizationProperty<?> property, Object value) {
		if(property == null) throw new IllegalArgumentException("Property cannot be null");
		if(value == null) throw new IllegalArgumentException("Value cannot be null");

		if(property.getIdentifier() == null)
			throw new IllegalArgumentException("Property must be present in the registry!");

		// Use the default to reduce redundancy.
		if(value.equals(property.getDefaultValue())) {
			this.reset(property);
			return;
		}

		this.values.put(property, value);
	}

	@SuppressWarnings("unchecked") // Checks are implemented in CustomizationProperty#equals()
	public <T> T get(CustomizationProperty<T> property) {
		if(property == null) return null;
		if(property.getIdentifier() == null)
			throw new IllegalStateException("Tried to get unregistered property");

		return (T) this.values.getOrDefault(property, property.getDefaultValue());
	}

	public <T> boolean has(CustomizationProperty<T> property) {
		if(property == null) return false;
		return this.values.containsKey(property);
	}

	public void reset(CustomizationProperty<?> property) {
		if(property == null) return;
		this.values.remove(property);
	}

	public <T> void copyPropertyFrom(CustomizationVolume volume, CustomizationProperty<T> property) {
		this.set(property, volume.get(property));
	}

	@SuppressWarnings("unchecked") // properties and keys are always consistent in type.
	public <T> long getChecksum() {
		Checksum checker = new CRC32();
		Set<Long> pairs = new HashSet<>();

		this.values.forEach((k, v) -> {
			CRC32 pairChecker = new CRC32();
			CustomizationProperty<T> key = (CustomizationProperty<T>) k;
			T value = (T) v;

			String id = key.getIdentifier().toString();

			pairChecker.update(id.getBytes(StandardCharsets.UTF_8));
			pairChecker.update(key.valueToChecksumBytes(value));
			pairChecker.update(69420);
			pairs.add(pairChecker.getValue());
		});

		pairs.stream().sorted()
				.map(pairChecksum -> ByteBuffer.allocate(8).putLong(pairChecksum).array())
				.forEach(checker::update);

		checker.update(54902349);
		return checker.getValue();
	}


	@SuppressWarnings("unchecked") // properties and keys are always consistent in type
	public <T> void writeToBuf(ByteBuf buf) {
		if(this.values.size() > Short.MAX_VALUE)
			throw new IllegalStateException("Too many properties!! - 32k properties is excessive.");

		buf.writeShort((short) this.values.size());

		this.values.forEach((k, v) -> {
			CustomizationProperty<T> property = (CustomizationProperty<T>) k;
			T value = (T) v;

			String id = property.getIdentifier().toString();
			byte[] idBytes = id.getBytes(StandardCharsets.UTF_8);

			if(idBytes.length > Byte.MAX_VALUE)
				throw new IllegalStateException("Property ID with length greater than 127 - This is kinda unnecessary, please shorten it.");

			buf.writeByte((byte) idBytes.length);
			buf.writeBytes(idBytes);

			property.encodeValueTo(buf, value);
		});

		buf.writeLong(this.getChecksum());
	}
	
	public void readFromBuf(ByteBuf buf) {
		int propertyCount = buf.readShort();

		for(int i = 0; i < propertyCount; i++) {
			int idByteCount = buf.readByte();
			byte[] idBytes = new byte[idByteCount];

			buf.readBytes(idBytes);

			String id = new String(idBytes, StandardCharsets.UTF_8);
			Optional<CustomizationProperty<?>> prop = tryParseProperty(id);

			prop.ifPresentOrElse(
					property -> this.setUnsafe(property, property.decodeValueFrom(buf)),
					this::setDefaults
			);
		}

		long recordedChecksum = buf.readLong();
		if (this.getChecksum() != recordedChecksum) {
			GrappleMod.LOGGER.error("Error checksum reading from buffer");
			this.setDefaults();
		}
	}

	/**
	 * Attempts to parse a CustomizationProperty from a string id, checking to
	 * see if one is present in the registry. It emits error messages and returns empty if
	 * not found.
	 * @param id the identifier to try parse
	 * @return the associated property if found, else an empty optional
	 */
	private static Optional<CustomizationProperty<?>> tryParseProperty(String id) {
		ResourceLocation identifier = ResourceLocation.tryParse(id);

		if(identifier == null)  {
			GrappleMod.LOGGER.error("Unable to parse customization property key: '%s'".formatted(id));
			return Optional.empty();
		}

		CustomizationProperty<?> property = GrappleModMetaRegistry.CUSTOMIZATION_PROPERTIES.get(identifier);

		if(property == null) {
			GrappleMod.LOGGER.error("Missing key for property: '%s' - are your mods synced with the server?".formatted(identifier.toString()));
			return Optional.empty();
		}

		return Optional.of(property);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof CustomizationVolume other)) return false;
		for(Map.Entry<CustomizationProperty<?>, ?> entry: this.values.entrySet()) {
			Object otherVal = other.get(entry.getKey());

			if(otherVal == null)
				otherVal = entry.getKey().getDefaultValue();

			if(!otherVal.equals(entry.getValue()))
				return false;
		}

		return true;
	}

	public Set<CustomizationProperty<?>> getPropertiesPresent() {
		return Collections.unmodifiableSet(this.values.keySet());
	}


	public static CustomizationVolume fromNBT(CompoundTag compound) {
		CustomizationVolume volume = new CustomizationVolume();
		volume.loadFromNBT(compound);
		return volume;
	}

	public static CustomizationVolume copyAllFrom(CustomizationVolume volume) {
		CustomizationVolume newVol = new CustomizationVolume();
		volume.values.forEach(newVol::setUnsafe);
		return newVol;
	}

}
