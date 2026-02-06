package com.yyon.grapplinghook.client.gui.widget.slot;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class OutputSlot extends Slot {

    public final IOutputProcessor out;

    public OutputSlot(Container container, int slot, int x, int y, IOutputProcessor out) {
        super(container, slot, x, y);
        this.out = out;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return false;
    }

    @Override
    public void onTake(Player player, ItemStack stack) {
        this.out.accept(player, stack);
    }

}
