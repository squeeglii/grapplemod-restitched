package com.yyon.grapplinghook.content.blockentity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yyon.grapplinghook.content.registry.internal.ModBlockEntities;
import com.yyon.grapplinghook.content.registry.GrappleModRegistries;
import com.yyon.grapplinghook.content.customization.CustomizationCategory;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.serverbound.SyncModifierTableC2SPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class GrappleModifierBlockEntity extends BlockEntity {

	public static final Codec<Data> DATA_CODEC = RecordCodecBuilder.create(builder -> builder.apply2(
			Data::new,

			HookCustomization.CODEC
				 .fieldOf("customizations")
				 .forGetter(Data::getCustomization),

			Codec.list(CustomizationCategory.KEY_CODEC)
				 .fieldOf("unlocked")
				 .forGetter(Data::getUnlockedCategories)
	));

	private Data data;

	public GrappleModifierBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.GRAPPLE_MODIFIER.get(), pos, state);
		this.data = new Data();
	}


	@Override
	public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);

		Tag data = DATA_CODEC.encode(this.data, NbtOps.INSTANCE, new CompoundTag()).getOrThrow();
		tag.put("data", data);

		//todo: verify, this should work though.
	}

	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries); // The super call is required to load the tiles location

		CompoundTag dataTag = tag.getCompound("data");

		if(dataTag.isEmpty()) {
			this.data = new Data();
			return;
		}

		this.data = DATA_CODEC.decode(NbtOps.INSTANCE, dataTag).getOrThrow().getFirst(); //todo: verify
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag nbtTagCompound = new CompoundTag();
		this.saveAdditional(nbtTagCompound, null); //todo: check what to do about provider.
		return ClientboundBlockEntityDataPacket.create(this);
	}


	/* Creates a tag containing all of the TileEntity information, used by vanilla to transmit from server to client */
	@Override
	@NotNull
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag nbtTagCompound = new CompoundTag();
		this.saveAdditional(nbtTagCompound, registries);
		return nbtTagCompound;
	}



	private void triggerUpdate() {
		if(this.level == null) return;

		BlockState state = this.level.getBlockState(worldPosition);
		this.level.sendBlockUpdated(worldPosition, state, state, 3);
		this.setChanged();
	}

	public void unlockCategory(CustomizationCategory category) {
		this.data.categoryUnlockStates.put(category, true);
		this.triggerUpdate();
	}

	public void setCustomization(HookCustomization customization) {
		this.data.customization = customization;

		if(this.level != null && this.level.isClientSide)
			NetworkManager.packetToServer(new SyncModifierTableC2SPayload(this.worldPosition, this.data.customization));

		this.triggerUpdate();
	}

	public boolean isUnlocked(CustomizationCategory category) {
		return this.data.categoryUnlockStates.containsKey(category) && this.data.categoryUnlockStates.get(category);
	}

	public HookCustomization getCurrentCustomizations() {
		return this.data.customization;
	}

	public List<CustomizationCategory> getUnlockedCategories() {
		return this.data.getUnlockedCategories();
	}

	private Data getData() {
		return this.data;
	}


	public static class Data {

		private HookCustomization customization;
		private Map<CustomizationCategory, Boolean> categoryUnlockStates;

		public Data() {
			this.customization = new HookCustomization();
			this.categoryUnlockStates = new HashMap<>();
		}

		public Data(HookCustomization customization, List<CustomizationCategory> categoryUnlockStates) {
			this.customization = customization;
			this.categoryUnlockStates = new HashMap<>();

			GrappleModRegistries.CUSTOMIZATION_CATEGORIES.stream()
					.forEach(category -> this.categoryUnlockStates.put(category, false));

			for(CustomizationCategory category : categoryUnlockStates) {
				this.categoryUnlockStates.put(category, true);
			}
		}

		public Data setCustomization(HookCustomization customization) {
			this.customization = customization;
			return this;
		}

		public Data setCategoryUnlockStates(HashMap<CustomizationCategory, Boolean> categoryUnlockStates) {
			this.categoryUnlockStates = categoryUnlockStates;
			return this;
		}

		public HookCustomization getCustomization() {
			return this.customization;
		}

		public Map<CustomizationCategory, Boolean> getCategoryUnlockStates() {
			return this.categoryUnlockStates;
		}

		public List<CustomizationCategory> getUnlockedCategories() {
			return this.categoryUnlockStates.entrySet()
					.stream()
					.filter(Map.Entry::getValue)
					.map(Map.Entry::getKey)
					.toList();
		}
	}
}
