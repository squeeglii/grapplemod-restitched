package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.content.registry.CustomizationCategories;

public class MotorUpgradeItem extends BaseUpgradeItem {
	public MotorUpgradeItem() {
		super(1, CustomizationCategories.MOTOR::get);
	}
}
