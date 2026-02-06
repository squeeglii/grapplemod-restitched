package com.yyon.grapplinghook.client.gui.menu;

import com.yyon.grapplinghook.content.registry.internal.ModMenus;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ModificationTableMenu extends AbstractContainerMenu {

    private Slot inputSlot;
    private Slot outputSlot;

    private Container inputSlotHolder = new SimpleContainer(1);
    private Container outputSlotHolder = new SimpleContainer(1);

    private final ContainerLevelAccess access;

    public ModificationTableMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public ModificationTableMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenus.MODIFICATION_TABLE, containerId);
        this.access = access;

        // todo: set x, y for slots.
        this.inputSlot = this.addSlot(new Slot(
                this.inputSlotHolder, 0, 0, 0
        ));

        this.outputSlot = this.addSlot(new Slot(
                this.outputSlotHolder, 0, 0, 0
        ));

        for(int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
            this.addSlot(new Slot(playerInventory, hotbarSlot, 0, 0));
        }

        for(int row = 1; row < 4; row++) {
            for(int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, row * 9 + column, 0, 0));
            }
        }

        //todo: data slots however that'll work.
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {

    }

    @Override
    public boolean stillValid(Player player) {
        return false;
    }
}
