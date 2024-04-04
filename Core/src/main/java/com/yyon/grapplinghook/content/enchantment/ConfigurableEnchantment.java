package com.yyon.grapplinghook.content.enchantment;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public abstract class ConfigurableEnchantment extends Enchantment {

    private boolean canDiscover;
    private boolean canTrade;

    protected ConfigurableEnchantment(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot[] equipmentSlots) {
        super(rarity, enchantmentCategory, equipmentSlots);

        this.canDiscover = true;
        this.canTrade = true;
    }

    protected ConfigurableEnchantment(Rarity rarity, EnchantmentCategory enchantmentCategory, EquipmentSlot equipmentSlot) {
        this(rarity, enchantmentCategory, new EquipmentSlot[]{ equipmentSlot });
    }


    public void setDiscoverable(boolean canDiscover) {
        this.canDiscover = canDiscover;
    }

    public void setTradeable(boolean canTrade) {
        this.canTrade = canTrade;
    }

    @Override
    public boolean isDiscoverable() {
        return this.canDiscover;
    }

    @Override
    public boolean isTradeable() {
        return this.canTrade;
    }
}
