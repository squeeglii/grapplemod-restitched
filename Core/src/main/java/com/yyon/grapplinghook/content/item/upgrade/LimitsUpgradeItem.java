package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.content.registry.CustomizationCategories;

public class LimitsUpgradeItem extends BaseUpgradeItem {
	public LimitsUpgradeItem() {
		super(1, CustomizationCategories.LIMITS::get);
	}
}
