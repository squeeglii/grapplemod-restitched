package com.yyon.grapplinghook.customization.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.registry.GrappleModRegistries;
import com.yyon.grapplinghook.customization.helper.PropertyOverride;
import com.yyon.grapplinghook.customization.type.CustomizationProperty;
import com.yyon.grapplinghook.util.exception.InvalidDataException;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class HookCustomization {

	//todo: check casts actually work.

	// Very useful resource: https://wiki.fabricmc.net/tutorial:codec
	// CODEC:
	// properties: Map<ResourceLocation, Mixed-Type>
	//  - ... n
	// crc32: long
	public static final Codec<HookCustomization> CODEC = Codec.lazyInitialized(() -> RecordCodecBuilder.create(builder -> builder.apply2(
			HookCustomization::new,

			CustomizationProperty.VALUE_MAP_CODEC
			 .fieldOf("properties")
			 .forGetter(HookCustomization::getValues),

			Codec.LONG
			 .fieldOf("crc32")
			 .forGetter(HookCustomization::getChecksum)
		)
	));


	public static final StreamCodec<? super RegistryFriendlyByteBuf, HookCustomization> STREAM_CODEC = StreamCodec.of();


	private Map<CustomizationProperty<?>, Object> values;

	private HookCustomization(Map<CustomizationProperty<?>, Object> properties, long crc32) {
		this.values = new HashMap<>(properties);

		// todo: checksum might be redundant these days.

		if(crc32 != this.getChecksum())
			throw new InvalidDataException("Checksum invalid for hook data!");
	}
	
	public HookCustomization() {
		this.setDefaults();
	}
	
	public void setDefaults() {
		this.values = new HashMap<>();
	}

	/** @deprecated Should not be used for item serialisation. See new data components.*/
	@Deprecated(since = "mc 1.21.1")
	public Tag writeToNBT() {
		return CODEC.encode(this, NbtOps.INSTANCE, new CompoundTag()).getOrThrow();
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

	public <T> void copyPropertyFrom(HookCustomization volume, CustomizationProperty<T> property) {
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


	//todo: migrate read & write to buf as STREAM_CODEC

	@SuppressWarnings("unchecked") // properties and keys are always consistent in type
	public <T> void writeToBuf(ByteBuf buf) {
		if(this.values.size() >= Short.MAX_VALUE)
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

		CustomizationProperty<?> property = GrappleModRegistries.CUSTOMIZATION_PROPERTIES.get(identifier);

		if(property == null) {
			GrappleMod.LOGGER.error("Missing key for property: '%s' - are your mods synced with the server?".formatted(identifier.toString()));
			return Optional.empty();
		}

		return Optional.of(property);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof HookCustomization other)) return false;
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

	private Map<CustomizationProperty<?>, Object> getValues() {
		return this.values;
	}

	/** Parses a new HookCustomization instance from the root of the provided tag. See top of class for format.*/
	public static HookCustomization fromNBT(CompoundTag compound) {
		DataResult<HookCustomization> parsed = CODEC.parse(NbtOps.INSTANCE, compound);
		return parsed.getOrThrow();
	}

	/** Performs a copy, creating a new instance with each property copied to a new list. */
	public static HookCustomization copyAllFrom(HookCustomization volume) {
		HookCustomization newVol = new HookCustomization();
		volume.values.forEach(newVol::setUnsafe);
		return newVol;
	}

}
