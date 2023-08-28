package com.yyon.grapplinghook.content.blockentity;

import com.yyon.grapplinghook.content.registry.GrappleModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TemplateTableBlockEntity extends BlockEntity implements Container {

    private final NonNullList<ItemStack> storedTemplates;

    public TemplateTableBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(GrappleModBlockEntities.TEMPLATE_TABLE.get(), blockPos, blockState);
        this.storedTemplates = NonNullList.createWithCapacity(18);
    }


    @Override
    public void saveAdditional(CompoundTag nbtTagCompound) {
        super.saveAdditional(nbtTagCompound);

        //TODO: Save BlockEntity data
    }

    @Override
    public void load(CompoundTag parentNBTTagCompound) {
        // The super call is required to load the block entity's location
        super.load(parentNBTTagCompound);

        // TODO: Load BlockEntity data
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbtTagCompound = new CompoundTag();
        this.saveAdditional(nbtTagCompound);
        return ClientboundBlockEntityDataPacket.create(this);
    }


    /* Creates a tag containing all the BlockEntity information,
       used by vanilla to transmit from server to client */
    @Override
    @NotNull
    public CompoundTag getUpdateTag() {
        CompoundTag nbtTagCompound = new CompoundTag();
        this.saveAdditional(nbtTagCompound);
        return nbtTagCompound;
    }


    // todo: handle containers

    @Override
    public int getContainerSize() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack getItem(int slot) {
        return null;
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return null;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return null;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {

    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }

    @Override
    public void clearContent() {

    }
}
