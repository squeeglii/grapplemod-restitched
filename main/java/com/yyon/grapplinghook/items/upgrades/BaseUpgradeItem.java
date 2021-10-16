package com.yyon.grapplinghook.items.upgrades;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import com.yyon.grapplinghook.grapplemod;
import com.yyon.grapplinghook.common.CommonSetup;
import com.yyon.grapplinghook.utils.GrappleCustomization;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class BaseUpgradeItem extends Item {
	public GrappleCustomization.upgradeCategories category = null;

	public BaseUpgradeItem(int maxStackSize, GrappleCustomization.upgradeCategories theCategory) {
		super(new Item.Properties().stacksTo(maxStackSize).tab(CommonSetup.tabGrapplemod));
		
		this.category = theCategory;
		
		this.setCraftingRemainingItem();
	}
	
	public void setCraftingRemainingItem() {
		try {
			Field craftingRemainingItem = ObfuscationReflectionHelper.findField(Item.class, "field_77700_c");
			craftingRemainingItem.setAccessible(true);

			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			modifiersField.setInt(craftingRemainingItem, craftingRemainingItem.getModifiers() & ~Modifier.FINAL);

			craftingRemainingItem.set(this, this);
		} catch (Exception e) {
			grapplemod.LOGGER.warn("unable to set craftingRemainingItem for upgrade item");
		}
	}
	
	public BaseUpgradeItem() {
		this(64, null);
	}
}
