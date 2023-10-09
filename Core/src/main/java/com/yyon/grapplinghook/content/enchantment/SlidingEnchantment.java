package com.yyon.grapplinghook.content.enchantment;

import com.yyon.grapplinghook.config.ConfigUtility;
import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SlidingEnchantment extends Enchantment {

    private final boolean isEnchantmentEnabled;

    public SlidingEnchantment() {
        super(
                ConfigUtility.getRarity(GrappleModLegacyConfig.getConf().enchantments.slide.enchant_rarity_sliding),
                EnchantmentCategory.ARMOR_FEET,
                new EquipmentSlot[] { EquipmentSlot.FEET }
        );

        int rarity = GrappleModLegacyConfig.getConf().enchantments.slide.enchant_rarity_sliding;
        this.isEnchantmentEnabled = rarity >= 0;
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

    @Override
    public boolean isDiscoverable() {
        return this.isEnchantmentEnabled;
    }
}
