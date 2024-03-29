package com.yyon.grapplinghook.content.blockentity;

import com.yyon.grapplinghook.content.registry.GrappleModBlockEntities;
import com.yyon.grapplinghook.content.registry.GrappleModMetaRegistry;
import com.yyon.grapplinghook.customization.CustomizationCategory;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.customization.template.TemplateUtils;
import com.yyon.grapplinghook.data.UpgraderUpper;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.serverbound.GrappleModifierMessage;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class GrappleModifierBlockEntity extends BlockEntity {

	private final HashMap<CustomizationCategory, Boolean> categoryUnlockStates = new HashMap<>();
	private CustomizationVolume customization;

	public GrappleModifierBlockEntity(BlockPos pos, BlockState state) {
		super(GrappleModBlockEntities.GRAPPLE_MODIFIER.get(), pos, state);
		this.customization = new CustomizationVolume();
	}


	@Override
	public void saveAdditional(CompoundTag nbtOut) {
		super.saveAdditional(nbtOut);
		UpgraderUpper.setLatestVersionInTag(nbtOut);

		CompoundTag unlockedNBT = nbtOut.getCompound("unlocked");

		this.categoryUnlockStates.forEach((key, value) -> {
            unlockedNBT.putBoolean(key.getIdentifier().toString(), value);
        });

		nbtOut.put("unlocked", unlockedNBT);
		nbtOut.put(TemplateUtils.NBT_HOOK_CUSTOMIZATIONS, this.customization.writeToNBT());
	}

	@Override
	public void load(CompoundTag nbtIn) {
		super.load(nbtIn); // The super call is required to load the tiles location

		// Upgrade the data from old versions. If an upgrade has happened, it'll be returned
		// and swapped in.
		Optional<CompoundTag> fixedTag = UpgraderUpper.upgradeModificationTable(nbtIn);
		CompoundTag parentNBTTagCompound = fixedTag.orElse(nbtIn);

		CompoundTag unlockedNBT = parentNBTTagCompound.getCompound("unlocked");

		GrappleModMetaRegistry.CUSTOMIZATION_CATEGORIES.stream().forEach(category -> {
			boolean unlocked = unlockedNBT.getBoolean(category.getIdentifier().toString());

			if(unlocked) this.categoryUnlockStates.put(category, true);
		});

		CompoundTag custom = parentNBTTagCompound.getCompound(TemplateUtils.NBT_HOOK_CUSTOMIZATIONS);
		this.customization.loadFromNBT(custom);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag nbtTagCompound = new CompoundTag();
		this.saveAdditional(nbtTagCompound);
		return ClientboundBlockEntityDataPacket.create(this);
	}


	/* Creates a tag containing all of the TileEntity information, used by vanilla to transmit from server to client */
	@Override
	@NotNull
	public CompoundTag getUpdateTag() {
		CompoundTag nbtTagCompound = new CompoundTag();
		this.saveAdditional(nbtTagCompound);
		return nbtTagCompound;
	}



	private void triggerUpdate() {
		if(this.level == null) return;

		BlockState state = this.level.getBlockState(worldPosition);
		this.level.sendBlockUpdated(worldPosition, state, state, 3);
		this.setChanged();
	}

	public void unlockCategory(CustomizationCategory category) {
		this.categoryUnlockStates.put(category, true);
		this.triggerUpdate();
	}

	public void setCustomization(CustomizationVolume customization) {
		this.customization = customization;

		if(this.level != null && this.level.isClientSide)
			NetworkManager.packetToServer(new GrappleModifierMessage(this.worldPosition, this.customization));

		this.triggerUpdate();
	}

	public boolean isUnlocked(CustomizationCategory category) {
		return this.categoryUnlockStates.containsKey(category) && this.categoryUnlockStates.get(category);
	}

	public CustomizationVolume getCurrentCustomizations() {
		return this.customization;
	}

	public Set<CustomizationCategory> getUnlockedCategories() {
		return this.categoryUnlockStates.entrySet()
				.stream()
				.filter(Map.Entry::getValue)
				.map(Map.Entry::getKey)
				.collect(Collectors.toUnmodifiableSet());
	}
}
