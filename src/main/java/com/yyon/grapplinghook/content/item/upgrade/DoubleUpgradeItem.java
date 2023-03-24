package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.content.registry.GrappleModCustomizationCategories;

public class DoubleUpgradeItem extends BaseUpgradeItem {
	public DoubleUpgradeItem() {
		super(1, GrappleModCustomizationCategories.DOUBLE_HOOK::get);
	}
}
