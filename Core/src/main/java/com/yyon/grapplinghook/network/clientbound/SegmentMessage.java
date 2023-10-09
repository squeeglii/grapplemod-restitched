package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.entity.grapplinghook.RopeSegmentHandler;
import com.yyon.grapplinghook.network.NetworkContext;
import com.yyon.grapplinghook.util.Vec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
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

public class SegmentMessage extends BaseMessageClient {
   
	private int id;
	private boolean add;
	private int index;
	private Vec pos;
	private Direction topFacing;
	private Direction bottomFacing;

    public SegmentMessage(FriendlyByteBuf buf) {
    	super(buf);
    }

    public SegmentMessage(int id, boolean add, int index, Vec pos, Direction topfacing, Direction bottomfacing) {
    	this.id = id;
    	this.add = add;
    	this.index = index;
    	this.pos = pos;
    	this.topFacing = topfacing;
    	this.bottomFacing = bottomfacing;
    }

	@Override
    public void decode(FriendlyByteBuf buf) {
    	this.id = buf.readInt();
    	this.add = buf.readBoolean();
    	this.index = buf.readInt();
    	this.pos = new Vec(buf.readDouble(), buf.readDouble(), buf.readDouble());
    	this.topFacing = buf.readEnum(Direction.class);
    	this.bottomFacing = buf.readEnum(Direction.class);
    }

	@Override
    public void encode(FriendlyByteBuf buf) {
    	buf.writeInt(this.id);
    	buf.writeBoolean(this.add);
    	buf.writeInt(this.index);
    	buf.writeDouble(pos.x);
    	buf.writeDouble(pos.y);
    	buf.writeDouble(pos.z);
    	buf.writeEnum(this.topFacing);
    	buf.writeEnum(this.bottomFacing);
    }

	@Override
	public ResourceLocation getChannel() {
		return GrappleMod.id("segment");
	}

    @Environment(EnvType.CLIENT)
	@Override
    public void processMessage(NetworkContext ctx) {
    	Level world = Minecraft.getInstance().level;
    	Entity grapple = world.getEntity(this.id);
    	if (grapple == null) {
    		return;
    	}
    	
    	if (grapple instanceof GrapplinghookEntity hookEntity) {
    		RopeSegmentHandler segmentHandler = hookEntity.getSegmentHandler();
    		if (this.add) {
    			segmentHandler.actuallyAddSegment(this.index, this.pos, this.bottomFacing, this.topFacing);
    		} else {
    			segmentHandler.removeSegment(this.index);
    		}
    	}
    }
}
