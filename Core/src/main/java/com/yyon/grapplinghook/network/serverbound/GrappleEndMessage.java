package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.NetworkContext;
import com.yyon.grapplinghook.physics.ServerHookEntityTracker;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;

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

public class GrappleEndMessage extends BaseMessageServer {
   
	public int entityId;
	public HashSet<Integer> hookEntityIds;

    public GrappleEndMessage(FriendlyByteBuf buf) {
    	super(buf);
    }

    public GrappleEndMessage(int entityId, HashSet<Integer> hookEntityIds) {
    	this.entityId = entityId;
    	this.hookEntityIds = hookEntityIds;
    }

	@Override
    public void decode(FriendlyByteBuf buf) {
    	this.entityId = buf.readInt();
    	int size = buf.readInt();
    	this.hookEntityIds = new HashSet<>();
    	for (int i = 0; i < size; i++) {
    		this.hookEntityIds.add(buf.readInt());
    	}
    }

	@Override
    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.entityId);
    	buf.writeInt(this.hookEntityIds.size());
    	for (int id : this.hookEntityIds) {
        	buf.writeInt(id);
    	}
    }

	@Override
	public ResourceLocation getChannel() {
		return GrappleMod.id("grapple_end");
	}

	@Override
    public void processMessage(NetworkContext ctx) {
		int id = this.entityId;
		ServerPlayer player = ctx.getSender();

		ctx.getServer().execute(() -> {
			if (player == null) return;
			ServerHookEntityTracker.handleGrappleEndFromClient(id, player.level(), this.hookEntityIds);
		});
    }
}
