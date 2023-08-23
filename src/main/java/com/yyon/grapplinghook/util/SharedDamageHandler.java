package com.yyon.grapplinghook.util;

import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.item.GrapplehookItem;
import com.yyon.grapplinghook.content.item.LongFallBootsItem;
import com.yyon.grapplinghook.network.clientbound.GrappleDetachMessage;
import com.yyon.grapplinghook.physics.PhysicsContextTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.HashSet;

public class SharedDamageHandler {

    public static void handleDeath(Entity deadEntity) {
        Level level = deadEntity.level();

        if (level.isClientSide)
            return;

        int id = deadEntity.getId();
        boolean isConnected = PhysicsContextTracker.allGrapplehookEntities.containsKey(id);

        if (isConnected) return;

        HashSet<GrapplinghookEntity> grapplehookEntities = PhysicsContextTracker.allGrapplehookEntities.get(id);

        if(grapplehookEntities != null) {
            for (GrapplinghookEntity hookEntity : grapplehookEntities)
                hookEntity.removeServer();

            grapplehookEntities.clear();
        }

        PhysicsContextTracker.attached.remove(id);

        GrapplehookItem.grapplehookEntitiesLeft.remove(deadEntity);
        GrapplehookItem.grapplehookEntitiesRight.remove(deadEntity);

        if(deadEntity instanceof Player)
            GrappleModUtils.sendToCorrectClient(new GrappleDetachMessage(id), id, deadEntity.level());

        return;
    }

    /** @return true if the death should be cancelled. */
    public static boolean handleDamage(Entity damagedEntity, DamageSource source) {
        if (!(damagedEntity instanceof Player player)) return false;

        for (ItemStack armor : player.getArmorSlots()) {
            if (armor != null && armor.getItem() instanceof LongFallBootsItem) continue;
            if (source.is(DamageTypes.FLY_INTO_WALL)) return true;
        }

        return false;
    }
}
