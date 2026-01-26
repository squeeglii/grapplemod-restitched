package com.yyon.grapplinghook.content.item.type;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;


public interface IGlobalKeyObserver {
	enum Keys {
		LAUNCHER, THROWLEFT, THROWRIGHT, THROWBOTH, ROCKET;

		public static final StreamCodec<ByteBuf, Keys> STREAM_CODEC = ByteBufCodecs.idMapper(id -> Keys.values()[id], IGlobalKeyObserver.Keys::ordinal);
	}
	
	void onCustomKeyDown(ItemStack stack, Player player, Keys key, boolean ismainhand);
	void onCustomKeyUp(ItemStack stack, Player player, Keys key, boolean ismainhand);
}
