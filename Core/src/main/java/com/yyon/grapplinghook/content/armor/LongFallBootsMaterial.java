package com.yyon.grapplinghook.content.armor;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

public class LongFallBootsMaterial implements ArmorMaterial {

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return 800;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return 3;
    }

    @Override
    public int getEnchantmentValue() {
        return 10; // Match diamond
    }

    @Override
    @NotNull
    public SoundEvent getEquipSound() {
        return SoundEvents.ARMOR_EQUIP_CHAIN;
    }

    @Override
    @NotNull
    public Ingredient getRepairIngredient() {
        return Ingredient.of(Items.QUARTZ);
    }

    @Override
    @NotNull
    public String getName() {
        return "long_fall_boot_ish";
    }

    @Override
    public float getToughness() {
        return 2.0f;
    }

    @Override
    public float getKnockbackResistance() {
        return 0;
    }
}
