package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.network.NetworkContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;


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

public class GrappleAttachPosMessage extends BaseMessageClient {
   
	public int id;
	public double x;
	public double y;
	public double z;

    public GrappleAttachPosMessage(FriendlyByteBuf buf) {
    	super(buf);
    }

    public GrappleAttachPosMessage(int id, double x, double y, double z) {
    	this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
    	this.id = buf.readInt();
        this.x = buf.readDouble();
        this.y = buf.readDouble();
        this.z = buf.readDouble();
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.id);
        buf.writeDouble(this.x);
        buf.writeDouble(this.y);
        buf.writeDouble(this.z);
    }

    @Override
    public ResourceLocation getChannel() {
        return GrappleMod.id("grapple_attach_pos");
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void processMessage(NetworkContext ctx) {
    	Level world = Minecraft.getInstance().level;

        if(world == null) {
            GrappleMod.LOGGER.warn("Network Message received in invalid context (World not present | GrappleAttachPos)");
            return;
        }

    	if (world.getEntity(this.id) instanceof GrapplinghookEntity grapple) {
        	grapple.setAttachPos(this.x, this.y, this.z);
        }
    }
}
