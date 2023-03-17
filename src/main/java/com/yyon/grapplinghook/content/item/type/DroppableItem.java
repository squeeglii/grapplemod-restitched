package com.yyon.grapplinghook.content.item.type;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface DroppableItem {

    void onDroppedByPlayer(ItemStack item, Player player);

}
