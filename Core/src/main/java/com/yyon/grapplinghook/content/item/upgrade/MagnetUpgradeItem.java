package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.content.registry.CustomizationCategories;

public class MagnetUpgradeItem extends BaseUpgradeItem {
	public MagnetUpgradeItem() {
		super(1, CustomizationCategories.MAGNET::get);
	}
}
