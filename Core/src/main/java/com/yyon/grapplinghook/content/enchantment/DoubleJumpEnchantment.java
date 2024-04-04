package com.yyon.grapplinghook.content.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DoubleJumpEnchantment extends ConfigurableEnchantment {

	public DoubleJumpEnchantment() {
		super(Rarity.RARE, EnchantmentCategory.ARMOR_FEET, EquipmentSlot.FEET);
	}
	
	@Override
    public int getMinCost(int enchantmentLevel) {
        return 1;
    }

	@Override
    public int getMaxCost(int enchantmentLevel) {
        return this.getMinCost(enchantmentLevel) + 40;
    }

}
