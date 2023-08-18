package com.yyon.grapplinghook.content.enchantment;

import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.config.ConfigUtility;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class DoubleJumpEnchantment extends Enchantment {

    private boolean isEnchantmentEnabled;

	public DoubleJumpEnchantment() {
		super(
                ConfigUtility.getRarity(GrappleModLegacyConfig.getConf().enchantments.doublejump.enchant_rarity_double_jump),
                EnchantmentCategory.ARMOR_FEET,
                new EquipmentSlot[] { EquipmentSlot.FEET }
        );

        int rarity = GrappleModLegacyConfig.getConf().enchantments.doublejump.enchant_rarity_double_jump;
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
