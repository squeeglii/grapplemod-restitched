package com.yyon.grapplinghook.client.gui.menu;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.gui.widget.slot.InputSlot;
import com.yyon.grapplinghook.client.gui.widget.slot.OutputSlot;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.content.customization.CustomizationCategory;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.content.customization.data.TemplateAuthor;
import com.yyon.grapplinghook.content.item.type.ICustomizationApplicable;
import com.yyon.grapplinghook.content.registry.internal.ModBlocks;
import com.yyon.grapplinghook.content.registry.internal.ModDataComponents;
import com.yyon.grapplinghook.content.registry.internal.ModMenus;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.joml.Vector2i;

// Used the loom as a good model for this class.
public class ModificationTableMenu extends AbstractContainerMenu {

    // UI Constants
    public static final Vector2i MENU_SIZE = new Vector2i(248, 200);

    public static final Vector2i INPUT_SLOT_TOP_LEFT = new Vector2i(10, 68);
    public static final Vector2i OUTPUT_SLOT_TOP_LEFT = new Vector2i(222, 68);
    public static final Vector2i INVENTORY_TOP_LEFT = new Vector2i(44, 118);
    public static final int SLOT_SIZE = 18;
    public static final int HOTBAR_TOP_BUFFER = 4;

    public static final Vector2i BLUEPRINT_SCROLLABLE_TOP_LEFT = new Vector2i(50, 7);
    public static final Vector2i BLUEPRINT_SCROLLABLE_SIZE = new Vector2i(150, 67);
    public static final int BLUEPRINT_NAME_EDIT_WIDTH = 76;

    // State Access
    private final Slot inputSlot;
    private final Slot outputSlot;

    private final Container inputSlotHolder = new SimpleContainer(1);
    private final Container outputSlotHolder = new SimpleContainer(1);

    private final int inventorySlotStart;
    private final int inventoryBodySlotStart;
    private final int hotbarSlotStart;

    private final int inventorySlotEnd;
    private final int inventoryBodySlotEnd;
    private final int hotbarSlotEnd;

    private final Player player;
    private final ContainerLevelAccess access;

    // Mutable State - syncing.
    private HookCustomization tableCustomization;
    private TemplateAuthor tableAuthor;


    public ModificationTableMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, null, ContainerLevelAccess.NULL);
    }

    public ModificationTableMenu(int containerId, Inventory playerInventory, Player player, ContainerLevelAccess access) {
        super(ModMenus.MODIFICATION_TABLE, containerId);
        this.access = access;
        this.player = player;

        this.loadState();

        // Table slots
        this.inputSlot = this.addSlot(new InputSlot(
                this.inputSlotHolder, 0,
                INPUT_SLOT_TOP_LEFT.x(), INPUT_SLOT_TOP_LEFT.y(),
                stack -> stack.getItem() instanceof ICustomizationApplicable,
                this::setTableDataFromInputSlot,
                this::recomputeOutputSlot
        ));

        this.outputSlot = this.addSlot(new OutputSlot(
                this.outputSlotHolder, 0,
                OUTPUT_SLOT_TOP_LEFT.x(), OUTPUT_SLOT_TOP_LEFT.y(),
                this::onFinalizeCraft
        ));



        // Main Inventory Grid
        this.inventorySlotStart = this.slots.size(); // next id
        this.inventoryBodySlotStart = this.slots.size();
        for(int row = 0; row < 3; row++) {
            int slotY = INVENTORY_TOP_LEFT.y() + row * SLOT_SIZE;

            for(int column = 0; column < 9; column++) {
                int slotX = INVENTORY_TOP_LEFT.x() + column * SLOT_SIZE;
                this.addSlot(new Slot(playerInventory, (row + 1) * 9 + column, slotX, slotY));
            }
        }
        this.inventoryBodySlotEnd = this.slots.size();


        // Hotbar
        this.hotbarSlotStart = this.slots.size();
        int hotbarY = INVENTORY_TOP_LEFT.y() + 3*SLOT_SIZE + HOTBAR_TOP_BUFFER;
        for(int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            int slotX = INVENTORY_TOP_LEFT.x() + hotbarSlot * SLOT_SIZE;
            this.addSlot(new Slot(playerInventory, hotbarSlot, slotX, hotbarY)); // 27 - inventory body size.
        }
        this.hotbarSlotEnd = this.slots.size();
        this.inventorySlotEnd = this.slots.size(); // final id


        GrappleMod.LOGGER.info("Creating ModificationTable menu.");
    }


    // Slot stuff -- just maintaining state consistency
    public void setTableDataFromInputSlot() {
        ItemStack inputStack = this.inputSlot.getItem();

        if(inputStack.isEmpty())
            return; // do not reset if empty.

        HookCustomization customization = inputStack.getOrDefault(ModDataComponents.CUSTOMIZABLE, new HookCustomization());
        TemplateAuthor author = inputStack.getOrDefault(ModDataComponents.AUTHORED, new TemplateAuthor());

        Component newAuthor = this.player == null
                ? TemplateAuthor.DEFAULT_AUTHOR
                : this.player.getName();

        this.tableCustomization = customization;
        this.tableAuthor = author.adopt(newAuthor);
    }

    public void recomputeOutputSlot() {
        ItemStack inputStack = this.inputSlot.getItem();

        if(inputStack.isEmpty() || !(inputStack.getItem() instanceof ICustomizationApplicable customizer)) {
            this.outputSlot.set(ItemStack.EMPTY);
            return;
        }

        ItemStack outputStack = inputStack.copy();

        HookCustomization originalCustomizations = inputStack.getOrDefault(ModDataComponents.CUSTOMIZABLE, new HookCustomization());
        HookCustomization tableCustomizations = this.getCurrentTableCustomizations();

        customizer.applyCustomizations(inputStack, tableCustomizations);
        //outputStack.set(ModDataComponents.CUSTOMIZABLE, tableCustomizations);
        outputStack.set(ModDataComponents.CUSTOMIZATION_DELTA, originalCustomizations);

        // todo, should I leave this be if the table doesn't have author data or wipe it entirely. Currently wiping.
        outputStack.set(ModDataComponents.AUTHORED, this.getCurrentTableAuthorData());

        this.outputSlot.set(outputStack);
    }

    public void onFinalizeCraft(Player player, ItemStack stack) {
        stack.remove(ModDataComponents.CUSTOMIZATION_DELTA);
        this.inputSlotHolder.clearContent();
        this.saveState();
    }

    // Implementation methods --
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        if(this.slots.size() <= index)
            return ItemStack.EMPTY;

        Slot slot = this.slots.get(index);

        if(!slot.hasItem())
            return ItemStack.EMPTY;

        ItemStack stack = slot.getItem();
        ItemStack refStack = stack.copy();

        int inpSlot = this.inputSlot.index;

        // Output -> Inventory
        if(index == this.outputSlot.index) {
            if (!this.moveItemStackTo(stack, this.inventorySlotStart, this.inventorySlotEnd, true))
                return ItemStack.EMPTY;

        // Input -> Inventory
        } else if(index == inpSlot) {
            if (!this.moveItemStackTo(stack, this.inventorySlotStart, this.inventorySlotEnd, false))
                return ItemStack.EMPTY;

        // Inventory -> Input Slot
        } else {
            // Valid input, try to place
            if(this.inputSlot.mayPlace(stack)) {
                if (!this.moveItemStackTo(stack, inpSlot, inpSlot + 1, false))
                    return ItemStack.EMPTY;

            // Invalid input, rearrange in inventory.
            } else {
                // hotbar -> body
                if(index >= this.hotbarSlotStart && index < this.hotbarSlotEnd) {
                    if (!this.moveItemStackTo(stack, this.inventoryBodySlotStart, this.inventoryBodySlotEnd, false))
                        return ItemStack.EMPTY;

                // body -> hotbar
                } else if (index >= this.inventoryBodySlotStart && index < this.inventoryBodySlotEnd) {
                    if (!this.moveItemStackTo(stack, this.hotbarSlotStart, this.hotbarSlotEnd, false))
                        return ItemStack.EMPTY;

                } else return ItemStack.EMPTY;
            }
        }

        if(stack.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if(refStack.getCount() == stack.getCount())
            return ItemStack.EMPTY;

        slot.onTake(player, stack);

        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(this.access, player, ModBlocks.GRAPPLE_MODIFIER.get());
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.inputSlotHolder));
    }

    public boolean isUnlocked(CustomizationCategory category) {
        return this.access.evaluate((level, pos) -> {
            if(!this.stillValid(this.player)) {
                this.player.openMenu(null); // todo, is this the right way to clear a menu? do I need to do this?
                return false;
            }

            BlockEntity entity = level.getBlockEntity(pos);

            if(!(entity instanceof GrappleModifierBlockEntity modificationTable)) {
                this.player.openMenu(null);
                return false;
            }

            return modificationTable.isUnlocked(category);
        }).orElseThrow();
    }

    // Only save if an item is taken out of the output or a value is changed.
    // This should avoid mistakes.
    public void saveState() {
        this.access.execute((level, pos) -> {

        });
    }

    public void loadState() {
        this.access.execute((level, pos) -> {

        });
    }

    // Getters
    public Player getPlayer() {
        return player;
    }

    public HookCustomization getCurrentTableCustomizations() {
        return this.tableCustomization;
    }

    public TemplateAuthor getCurrentTableAuthorData() {
        return this.tableAuthor;
    }

}
