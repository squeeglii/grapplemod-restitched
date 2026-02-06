package com.yyon.grapplinghook.client.gui.menu;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.gui.widget.slot.InputSlot;
import com.yyon.grapplinghook.client.gui.widget.slot.OutputSlot;
import com.yyon.grapplinghook.content.item.type.ICustomizationApplicable;
import com.yyon.grapplinghook.content.registry.internal.ModBlocks;
import com.yyon.grapplinghook.content.registry.internal.ModDataComponents;
import com.yyon.grapplinghook.content.registry.internal.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector2i;

// Used the loom as a good model for this class.
public class ModificationTableMenu extends AbstractContainerMenu {

    // UI Constants,
    public static final Vector2i MENU_SIZE = new Vector2i(248, 200);

    public static final Vector2i INPUT_SLOT_TOP_LEFT = new Vector2i(10, 68);
    public static final Vector2i OUTPUT_SLOT_TOP_LEFT = new Vector2i(222, 68);
    public static final Vector2i INVENTORY_TOP_LEFT = new Vector2i(44, 118);
    public static final int SLOT_SIZE = 18;
    public static final int HOTBAR_TOP_BUFFER = 4;

    public static final Vector2i BLUEPRINT_SCROLLABLE_TOP_LEFT = new Vector2i(50, 7);
    public static final Vector2i BLUEPRINT_SCROLLABLE_SIZE = new Vector2i(150, 67);
    public static final int BLUEPRINT_NAME_EDIT_WIDTH = 76;

    private final Slot inputSlot;
    private final Slot outputSlot;

    private final Container inputSlotHolder = new SimpleContainer(1);
    private final Container outputSlotHolder = new SimpleContainer(1);

    private final ContainerLevelAccess access;

    public ModificationTableMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public ModificationTableMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenus.MODIFICATION_TABLE, containerId);
        this.access = access;

        // Table slots
        this.inputSlot = this.addSlot(new InputSlot(
                this.inputSlotHolder, 0,
                INPUT_SLOT_TOP_LEFT.x(), INPUT_SLOT_TOP_LEFT.y(),
                stack -> stack.getItem() instanceof ICustomizationApplicable
        ));

        this.outputSlot = this.addSlot(new OutputSlot(
                this.outputSlotHolder, 0,
                OUTPUT_SLOT_TOP_LEFT.x(), OUTPUT_SLOT_TOP_LEFT.y(),
                this::onFinalizeCraft
        ));

        // Hotbar
        int hotbarY = INVENTORY_TOP_LEFT.y() + 3*SLOT_SIZE + HOTBAR_TOP_BUFFER;
        for(int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            int slotX = INVENTORY_TOP_LEFT.x() + hotbarSlot * SLOT_SIZE;
            this.addSlot(new Slot(playerInventory, hotbarSlot, slotX, hotbarY));
        }

        // Main Inventory Grid
        for(int row = 1; row < 4; row++) {
            int slotY = INVENTORY_TOP_LEFT.y() + (row - 1) * SLOT_SIZE;

            for(int column = 0; column < 9; column++) {
                int slotX = INVENTORY_TOP_LEFT.x() + column * SLOT_SIZE;
                this.addSlot(new Slot(playerInventory, row * 9 + column, slotX, slotY));
            }
        }

        GrappleMod.LOGGER.info("Creating ModificationTable menu.");
    }

    // Utility methods -- slot listeners
    public void onItemSlotChange(Runnable onChange) {
        this.addSlotListener(new RunnableSlotListener(onChange, null));
    }

    public void onDataSlotChange(Runnable onChange) {
        this.addSlotListener(new RunnableSlotListener(null, onChange));
    }

    public void onAnySlotChange(Runnable onChange) {
        this.addSlotListener(new RunnableSlotListener(onChange, onChange));
    }


    // Implementation methods --
    public void onFinalizeCraft(Player player, ItemStack stack) {
        stack.remove(ModDataComponents.CUSTOMIZATION_DELTA);

        throw new UnsupportedOperationException("Crafting new hook unimplemented.");
        //todo: clear inputs & ensure configuration is saved.
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        throw new UnsupportedOperationException("Unimplemented quick move");
        //todo: figure out quickmove
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

    // Helper -- used to wrap listeners for slots.
    public record RunnableSlotListener(Runnable onSlotChange, Runnable onDataChange) implements ContainerListener {

        @Override
        public void slotChanged(AbstractContainerMenu containerToSend, int dataSlotIndex, ItemStack stack) {
            if(this.onSlotChange != null)
                this.onSlotChange.run();
        }

        @Override
        public void dataChanged(AbstractContainerMenu containerMenu, int dataSlotIndex, int value) {
            if(this.onDataChange != null)
                this.onDataChange.run();
        }
    }

}
