package com.yyon.grapplinghook.item.upgrade;

import com.yyon.grapplinghook.util.GrappleCustomization;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class BaseUpgradeItem extends Item {
	public GrappleCustomization.UpgradeCategories category = null;

	public BaseUpgradeItem() {
		this(64, null);
	}

	public BaseUpgradeItem(int maxStackSize, GrappleCustomization.UpgradeCategories theCategory) {
		super(new Item.Properties().stacksTo(maxStackSize));
		
		this.category = theCategory;
	}
}
