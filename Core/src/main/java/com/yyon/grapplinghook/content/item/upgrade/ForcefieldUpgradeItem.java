package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.content.registry.CustomizationCategories;

public class ForcefieldUpgradeItem extends BaseUpgradeItem {
	public ForcefieldUpgradeItem() {
		super(1, CustomizationCategories.FORCEFIELD::get);
	}
}
