package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.customization.GrappleCustomization;
import net.minecraft.world.item.Item;

public class BaseUpgradeItem extends Item {
	public GrappleCustomization.UpgradeCategory category;

	public BaseUpgradeItem() {
		this(64, null);
	}

	public BaseUpgradeItem(int maxStackSize, GrappleCustomization.UpgradeCategory theCategory) {
		super(new Item.Properties().stacksTo(maxStackSize));
		
		this.category = theCategory;
	}
}
