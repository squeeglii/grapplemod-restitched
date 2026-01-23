package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.content.registry.CustomizationCategories;

public class StaffUpgradeItem extends BaseUpgradeItem {
	public StaffUpgradeItem() {
		super(1, CustomizationCategories.ENDER_STAFF::get);
	}
}
