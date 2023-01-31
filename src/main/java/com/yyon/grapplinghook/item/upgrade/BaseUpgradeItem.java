package com.yyon.grapplinghook.item.upgrade;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.util.GrappleCustomization;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BaseUpgradeItem extends Item {
	public GrappleCustomization.upgradeCategories category = null;
	boolean craftingRemaining = false;

	public BaseUpgradeItem(int maxStackSize, GrappleCustomization.upgradeCategories theCategory) {
		super(new Item.Properties().stacksTo(maxStackSize).tab(GrappleMod.ITEM_GROUP));
		
		this.category = theCategory;
		
		if (theCategory != null) {
			this.setCraftingRemainingItem();
		}
	}
	
	public void setCraftingRemainingItem() {
		craftingRemaining = true;
	}

	//TODO: @Override
	public ItemStack getCraftingRemainingItem(ItemStack itemStack)
    {
        if (!this.craftingRemaining)
        {
            return ItemStack.EMPTY;
        }
        return new ItemStack(this);
    }
	
	@Override
	public boolean hasCraftingRemainingItem() {
		return this.craftingRemaining;
	}


	public BaseUpgradeItem() {
		this(64, null);
	}
}
