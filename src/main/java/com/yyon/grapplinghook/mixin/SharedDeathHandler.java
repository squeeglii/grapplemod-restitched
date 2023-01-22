package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.entity.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.item.GrapplehookItem;
import com.yyon.grapplinghook.item.LongFallBoots;
import com.yyon.grapplinghook.network.GrappleDetachMessage;
import com.yyon.grapplinghook.server.ServerControllerManager;
import com.yyon.grapplinghook.util.GrapplemodUtils;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;

public class SharedDeathHandler {


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

            GrapplemodUtils.sendToCorrectClient(new GrappleDetachMessage(id), id, deadEntity.level);
        }

        return false;
    }

}
