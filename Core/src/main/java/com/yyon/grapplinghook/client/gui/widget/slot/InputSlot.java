package com.yyon.grapplinghook.client.gui.widget.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class InputSlot extends Slot {

    private final Predicate<ItemStack> filter;

    public InputSlot(Container container, int slot, int x, int y, Predicate<ItemStack> filter) {
        super(container, slot, x, y);
        this.filter = filter;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.filter.test(stack);
    }
}
