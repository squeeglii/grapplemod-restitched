package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.customization.CustomizationCategory;
import com.yyon.grapplinghook.content.registry.GrappleModCustomizationCategories;
import net.minecraft.world.item.Item;

public class BaseUpgradeItem extends Item {

	private final CustomizationCategory category;

	public BaseUpgradeItem() {
		this(64, null);
	}

	public BaseUpgradeItem(int maxStackSize, CustomizationCategory theCategory) {
		super(new Item.Properties().stacksTo(maxStackSize));
		this.category = theCategory;
	}

	public CustomizationCategory getCategory() {
		return this.category;
	}
}
