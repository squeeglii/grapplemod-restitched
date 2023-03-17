package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.item.type.KeypressItem;
import com.yyon.grapplinghook.network.NetworkContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class KeypressMessage extends BaseMessageServer {
	
	KeypressItem.Keys key;
	boolean isDown;

    public KeypressMessage(FriendlyByteBuf buf) {
    	super(buf);
    }

    public KeypressMessage(KeypressItem.Keys thekey, boolean isDown) {
    	this.key = thekey;
    	this.isDown = isDown;
    }

	@Override
    public void decode(FriendlyByteBuf buf) {
    	this.key = KeypressItem.Keys.values()[buf.readInt()];
    	this.isDown = buf.readBoolean();
    }

	@Override
    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.key.ordinal());
    	buf.writeBoolean(this.isDown);
    }

	@Override
	public ResourceLocation getChannel() {
		return GrappleMod.id("keypress");
	}

	@Override
    public void processMessage(NetworkContext ctx) {
    	final ServerPlayer player = ctx.getSender();

		ctx.getServer().execute(() -> {
			if (player != null) {
				ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
				if (stack.getItem() instanceof KeypressItem keypressItem) {
					if (isDown) {
						keypressItem.onCustomKeyDown(stack, player, key, true);
					} else {
						keypressItem.onCustomKeyUp(stack, player, key, true);
					}

					return;
				}

				stack = player.getItemInHand(InteractionHand.OFF_HAND);
				if (stack.getItem() instanceof KeypressItem keypressItem) {
					if (isDown) {
						keypressItem.onCustomKeyDown(stack, player, key, false);
					} else {
						keypressItem.onCustomKeyUp(stack, player, key, false);
					}
				}
			}
		});

	}
}
