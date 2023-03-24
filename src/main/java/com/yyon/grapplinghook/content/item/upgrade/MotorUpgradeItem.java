package com.yyon.grapplinghook.content.item.upgrade;

import com.yyon.grapplinghook.content.registry.GrappleModCustomizationCategories;

public class MotorUpgradeItem extends BaseUpgradeItem {
	public MotorUpgradeItem() {
		super(1, GrappleModCustomizationCategories.MOTOR::get);
	}
}
