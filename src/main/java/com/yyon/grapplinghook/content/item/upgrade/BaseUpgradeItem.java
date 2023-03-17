package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.customization.CustomizationVolume;
import net.minecraft.world.item.Item;

public class BaseUpgradeItem extends Item {
	public CustomizationVolume.UpgradeCategory category;

	public BaseUpgradeItem() {
		this(64, null);
	}

	public BaseUpgradeItem(int maxStackSize, CustomizationVolume.UpgradeCategory theCategory) {
		super(new Item.Properties().stacksTo(maxStackSize));
		
		this.category = theCategory;
	}
}
