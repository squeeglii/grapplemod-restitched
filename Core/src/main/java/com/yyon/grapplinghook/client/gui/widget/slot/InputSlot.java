package com.yyon.grapplinghook.client.gui.widget.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class InputSlot extends Slot {

    private final Predicate<ItemStack> filter;
    private final Runnable[] changeListeners;

    public InputSlot(Container container, int slot, int x, int y, Predicate<ItemStack> filter, Runnable... onChanged) {
        super(container, slot, x, y);
        this.filter = filter;
        this.changeListeners = onChanged;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return this.filter.test(stack);
    }

    @Override
    public void setChanged() {
        super.setChanged();

        for(Runnable listener : this.changeListeners)
            listener.run();
    }
}
