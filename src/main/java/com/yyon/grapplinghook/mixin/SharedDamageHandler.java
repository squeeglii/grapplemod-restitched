package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.entity.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.item.GrapplehookItem;
import com.yyon.grapplinghook.item.LongFallBoots;
import com.yyon.grapplinghook.network.clientbound.GrappleDetachMessage;
import com.yyon.grapplinghook.server.ServerControllerManager;
import com.yyon.grapplinghook.util.GrappleModUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;

public class SharedDamageHandler {

    /** @return true if the death should be cancelled. */
    public static boolean handleDeath(Entity deadEntity) {
        if (!deadEntity.level.isClientSide) {
            int id = deadEntity.getId();
            boolean isConnected = ServerControllerManager.allGrapplehookEntities.containsKey(id);

            if (isConnected) return false;

            HashSet<GrapplehookEntity> grapplehookEntities = ServerControllerManager.allGrapplehookEntities.get(id);
            for (GrapplehookEntity hookEntity: grapplehookEntities) hookEntity.removeServer();

            grapplehookEntities.clear();

            ServerControllerManager.attached.remove(id);

            GrapplehookItem.grapplehookEntitiesLeft.remove(deadEntity);
            GrapplehookItem.grapplehookEntitiesRight.remove(deadEntity);

            GrappleModUtils.sendToCorrectClient(new GrappleDetachMessage(id), id, deadEntity.level);
        }

        return false;
    }

    /** @return true if the death should be cancelled. */
    public static boolean handleDamage(Entity damagedEntity, DamageSource source) {
        if (damagedEntity instanceof Player player) {

            for (ItemStack armor : player.getArmorSlots()) {
                if (armor != null && armor.getItem() instanceof LongFallBoots) continue;
                if (source == DamageSource.FLY_INTO_WALL) return true;
            }
        }

        return false;
    }
}
