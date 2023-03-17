package com.yyon.grapplinghook.content.item.type;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public interface KeypressItem {
	enum Keys {
		LAUNCHER, THROWLEFT, THROWRIGHT, THROWBOTH, ROCKET
	}
	
	void onCustomKeyDown(ItemStack stack, Player player, Keys key, boolean ismainhand);
	void onCustomKeyUp(ItemStack stack, Player player, Keys key, boolean ismainhand);
}
