package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.content.registry.CustomizationCategories;

public class DoubleUpgradeItem extends BaseUpgradeItem {
	public DoubleUpgradeItem() {
		super(1, CustomizationCategories.DOUBLE_HOOK::get);
	}
}
