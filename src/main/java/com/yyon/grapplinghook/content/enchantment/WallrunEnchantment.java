package com.yyon.grapplinghook.content.enchantment;

import com.yyon.grapplinghook.config.GrappleModConfig;
import com.yyon.grapplinghook.config.ConfigUtility;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class WallrunEnchantment extends Enchantment {
	public WallrunEnchantment() {
		super(ConfigUtility.getRarityFromInt(GrappleModConfig.getConf().enchantments.wallrun.enchant_rarity_wallrun), EnchantmentCategory.ARMOR_FEET, new EquipmentSlot[] {EquipmentSlot.FEET});
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
