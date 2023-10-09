package com.yyon.grapplinghook.util;

import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.item.GrapplehookItem;
import com.yyon.grapplinghook.content.item.LongFallBootsItem;
import com.yyon.grapplinghook.network.clientbound.GrappleDetachMessage;
import com.yyon.grapplinghook.physics.ServerHookEntityTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SharedDamageHandler {

    public static void handleDeath(Entity deadEntity) {
        Level level = deadEntity.level();

        if (level.isClientSide)
            return;

        if(deadEntity instanceof GrapplinghookEntity)
            return;

        if (ServerHookEntityTracker.isAttachedToHooks(deadEntity))
            return;

        ServerHookEntityTracker.removeAllHooksFor(deadEntity);
        GrapplehookItem.grapplehookEntitiesLeft.remove(deadEntity);
        GrapplehookItem.grapplehookEntitiesRight.remove(deadEntity);

        if(deadEntity instanceof Player) {
            int id = deadEntity.getId();
            GrappleDetachMessage detachPacket = new GrappleDetachMessage(id);
            GrappleModUtils.sendToCorrectClient(detachPacket, id, level);
        }
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
