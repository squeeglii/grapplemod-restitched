package com.yyon.grapplinghook.content.blockentity;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.block.TemplateTableBlock;
import com.yyon.grapplinghook.content.item.BlueprintItem;
import com.yyon.grapplinghook.content.registry.GrappleModBlockEntities;
import com.yyon.grapplinghook.data.UpgraderUpper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class TemplateTableBlockEntity extends BaseContainerBlockEntity {

    public static int MAX_CAPACITY = 15;

    private final NonNullList<ItemStack> storedTemplates;

    public TemplateTableBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(GrappleModBlockEntities.TEMPLATE_TABLE.get(), blockPos, blockState);
        this.storedTemplates = NonNullList.createWithCapacity(MAX_CAPACITY);
    }


    @NotNull
    @Override
    protected Component getDefaultName() {
        return Component.translatable("template_table.title.default");
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
        return ChestMenu.oneRow(containerId, inventory);

        //TODO: Implement actual UI for 15 slots. Needs:
        // - List view on left with 'favourite' blueprint on the top.
        //  - list needs way to add blueprints (slot at top of selected item?)
        //  - add empty entries for unfilled slots?
        // - Details of what's on blueprint + slot to apply it to new items.
        // - Settings pane for how to treat new items.
    }

    @Override
    public void load(CompoundTag tag) {
        this.storedTemplates.clear();
        ContainerHelper.loadAllItems(tag, this.storedTemplates);
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        UpgraderUpper.setLatestVersionInTag(tag);
        ContainerHelper.saveAllItems(tag, this.storedTemplates, true);
    }

    @Override
    public void clearContent() {
        this.storedTemplates.clear();
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
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

    @NotNull
    @Override
    public ItemStack getItem(int slot) {
        return this.storedTemplates.get(slot);
    }

    @NotNull
    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack currentSlotStack = this.storedTemplates.get(slot);

        this.storedTemplates.set(slot, ItemStack.EMPTY);
        if (!currentSlotStack.isEmpty())
            this.updateBlockState();

        return currentSlotStack;
    }

    @NotNull
    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return this.removeItem(slot, 1);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if(slot > this.getContainerSize()) return;

        //TODO: Add item datapack tag for acceptable items.
        //      Handle weird functionality when slot is selected?
        if(!(stack.getItem() instanceof BlueprintItem)) return;

        this.storedTemplates.set(slot, stack);
        this.updateBlockState();
    }

    @Override
    public int getContainerSize() {
        return MAX_CAPACITY;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public boolean isEmpty() {
        return this.getTemplateCount() <= 0;
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        //TODO: Add item datapack tag for acceptable items.
        //      Handle weird functionality when slot is selected?
        return stack.getItem() instanceof BlueprintItem &&
               this.getItem(index).isEmpty();
    }

    @Override
    public boolean canTakeItem(Container outputContainer, int slot, ItemStack stackInTableSlot) {
        return outputContainer.hasAnyMatching(outputStack -> {
            if (outputStack.isEmpty())
                return true;

            // Check NBT matches + is same Item
            if(!ItemStack.isSameItemSameTags(stackInTableSlot, outputStack))
                return false;

            int maxOutputStackSize =  Math.min(outputStack.getMaxStackSize(), outputContainer.getMaxStackSize());

            return outputStack.getCount() + stackInTableSlot.getCount() <= maxOutputStackSize;
        });
    }

    public void updateBlockState() {
        IntegerProperty templatesHeld = TemplateTableBlock.TEMPLATES_HELD;

        if(this.getTemplateCount() > this.getContainerSize()) {
            GrappleMod.LOGGER.error("TemplateTableBlockEntity has more items than it's capacity!!");

            this.getBlockState().setValue(templatesHeld, TemplateTableBlock.FULL);
            return;
        }

        float fillFraction = (float) this.getTemplateCount() / this.getContainerSize();
        float cappedFillFraction = Mth.clamp(fillFraction, 0f, 1f);

        float fillStage = Mth.lerp(cappedFillFraction, TemplateTableBlock.EMPTY, TemplateTableBlock.FULL);
        int steppedFillStage = Mth.floor(fillStage);

        this.getBlockState().setValue(templatesHeld, steppedFillStage);
    }


    public int getTemplateCount() {
        return (int) this.storedTemplates.stream()
                .filter(Predicate.not(ItemStack::isEmpty))
                .count();
    }
}
