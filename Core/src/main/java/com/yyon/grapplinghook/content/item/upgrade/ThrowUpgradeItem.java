package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.content.registry.CustomizationCategories;

public class ThrowUpgradeItem extends BaseUpgradeItem {
	public ThrowUpgradeItem() {
		super(1, CustomizationCategories.HOOK_THROWER::get);
	}
}
