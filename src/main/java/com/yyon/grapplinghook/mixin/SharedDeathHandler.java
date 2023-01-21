package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.item.LongFallBoots;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SharedDeathHandler {


    /** @return true if the death should be cancelled. */
    public static boolean handleDeath(DamageSource source, Entity deadEntity) {
        if (deadEntity instanceof Player player) {
            for (ItemStack armor : player.getArmorSlots()) {
                if (armor != null && armor.getItem() instanceof LongFallBoots) {
                    if (source == DamageSource.FLY_INTO_WALL) {
                        // this cancels the fall event so you take no damage
                        return true;
                    }
                }
            }
        }

        return false;
    }

}
