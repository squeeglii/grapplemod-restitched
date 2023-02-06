package com.yyon.grapplinghook.blockentity;

import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.serverbound.GrappleModifierMessage;
import com.yyon.grapplinghook.registry.GrappleModBlockEntities;
import com.yyon.grapplinghook.util.GrappleCustomization;
import com.yyon.grapplinghook.util.GrappleCustomization.UpgradeCategories;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class GrappleModifierBlockEntity extends BlockEntity {
	public HashMap<UpgradeCategories, Boolean> unlockedCategories = new HashMap<>();
	public GrappleCustomization customization;

	public GrappleModifierBlockEntity(BlockPos pos, BlockState state) {
		super(GrappleModBlockEntities.GRAPPLE_MODIFIER.get(), pos, state);
		this.customization = new GrappleCustomization();
	}

	private void triggerUpdate() {
		if(this.level != null) {
			BlockState state = this.level.getBlockState(worldPosition);
			this.level.sendBlockUpdated(worldPosition, state, state, 3);
			this.setChanged();
		}
	}

	public void unlockCategory(UpgradeCategories category) {
		unlockedCategories.put(category, true);
		this.triggerUpdate();
	}

	public void setCustomizationClient(GrappleCustomization customization) {
		this.customization = customization;
		NetworkManager.packetToServer(new GrappleModifierMessage(this.worldPosition, this.customization));
		this.triggerUpdate();
	}

	public void setCustomizationServer(GrappleCustomization customization) {
		this.customization = customization;
		this.triggerUpdate();
	}

	public boolean isUnlocked(UpgradeCategories category) {
		return this.unlockedCategories.containsKey(category) && this.unlockedCategories.get(category);
	}

	@Override
	public void saveAdditional(CompoundTag nbtTagCompound) {
		super.saveAdditional(nbtTagCompound);

		CompoundTag unlockedNBT = nbtTagCompound.getCompound("unlocked");

		for (UpgradeCategories category : UpgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = this.isUnlocked(category);

			unlockedNBT.putBoolean(num, unlocked);
		}

		nbtTagCompound.put("unlocked", unlockedNBT);
		nbtTagCompound.put("customization", this.customization.writeNBT());
	}

	@Override
	public void load(CompoundTag parentNBTTagCompound) {
		super.load(parentNBTTagCompound); // The super call is required to load the tiles location

		CompoundTag unlockedNBT = parentNBTTagCompound.getCompound("unlocked");

		for (UpgradeCategories category : UpgradeCategories.values()) {
			String num = String.valueOf(category.toInt());
			boolean unlocked = unlockedNBT.getBoolean(num);

			this.unlockedCategories.put(category, unlocked);
		}

		CompoundTag custom = parentNBTTagCompound.getCompound("customization");
		this.customization.loadNBT(custom);
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

}
