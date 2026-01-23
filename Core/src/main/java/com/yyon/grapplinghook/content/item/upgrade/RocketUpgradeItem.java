package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.content.registry.CustomizationCategories;

public class RocketUpgradeItem extends BaseUpgradeItem {
	public RocketUpgradeItem() {
		super(1, CustomizationCategories.ROCKET::get);
	}
}
