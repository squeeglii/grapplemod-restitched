package com.yyon.grapplinghook.content.enchantment;

import com.yyon.grapplinghook.config.GrappleModConfig;
import com.yyon.grapplinghook.config.ConfigUtility;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SlidingEnchantment extends Enchantment {
	public SlidingEnchantment() {
		super(ConfigUtility.getRarityFromInt(GrappleModConfig.getConf().enchantments.slide.enchant_rarity_sliding), EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[] {EquipmentSlot.FEET});
	}
	
	@Override
    public int getMinCost(int enchantmentLevel)
    {
        return 1;
    }

	@Override
    public int getMaxCost(int enchantmentLevel)
    {
        return this.getMinCost(enchantmentLevel) + 40;
    }

	@Override
	public int getMaxLevel()
    {
        return 1;
    }
}
