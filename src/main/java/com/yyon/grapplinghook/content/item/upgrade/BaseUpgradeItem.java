package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.customization.CustomizationCategory;
import com.yyon.grapplinghook.content.registry.GrappleModCustomizationCategories;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class BaseUpgradeItem extends Item {

	private final Supplier<CustomizationCategory> category;

	public BaseUpgradeItem() {
		this(64, null);
	}

	public BaseUpgradeItem(int maxStackSize, Supplier<CustomizationCategory> theCategory) {
		super(new Item.Properties().stacksTo(maxStackSize));
		this.category = theCategory;
	}

	public CustomizationCategory getCategory() {
		return this.category.get();
	}
}
