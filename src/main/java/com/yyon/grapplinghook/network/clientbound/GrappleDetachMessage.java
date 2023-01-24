package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.client.ClientControllerManager;
import com.yyon.grapplinghook.network.NetworkContext;
import com.yyon.grapplinghook.network.clientbound.BaseMessageClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;

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

public class GrappleDetachMessage extends BaseMessageClient {
   
	public int id;

    public GrappleDetachMessage(FriendlyByteBuf buf) {
    	super(buf);
    }

    public GrappleDetachMessage(int id) {
    	this.id = id;
    }

    public void decode(FriendlyByteBuf buf) {
    	this.id = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.id);
    }

    @Environment(EnvType.CLIENT)
    public void processMessage(NetworkContext ctx) {
    	ClientControllerManager.receiveGrappleDetach(this.id);
    }
}