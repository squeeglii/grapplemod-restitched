package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.entity.grapplinghook.RopeSegmentHandler;
import com.yyon.grapplinghook.network.S2CPayload;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

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

// Previously SegmentMessage
public record RopeSegmentUpdateS2CPayload(int hookId, boolean shouldAdd, int index, Vec pos, Direction topFacing, Direction bottomFacing) implements S2CPayload {
	public static final ResourceLocation IDENTIFIER = GrappleMod.id("rope_segment_update");
	public static final CustomPacketPayload.Type<RopeSegmentUpdateS2CPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

	public static final StreamCodec<RegistryFriendlyByteBuf, RopeSegmentUpdateS2CPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			RopeSegmentUpdateS2CPayload::hookId,
			ByteBufCodecs.BOOL,
			RopeSegmentUpdateS2CPayload::shouldAdd,
			ByteBufCodecs.INT,
			RopeSegmentUpdateS2CPayload::index,
			Vec.STREAM_CODEC,
			RopeSegmentUpdateS2CPayload::pos,
			GrappleModUtils.DIRECTION_STREAM_CODEC,
			RopeSegmentUpdateS2CPayload::topFacing,
			GrappleModUtils.DIRECTION_STREAM_CODEC,
			RopeSegmentUpdateS2CPayload::bottomFacing,
			RopeSegmentUpdateS2CPayload::new
	);

	@NotNull
	@Override
	public Type<RopeSegmentUpdateS2CPayload> type() {
		return PAYLOAD_TYPE;
	}

	@Override
	public void process(ClientPlayNetworking.Context ctx) {
		Level world = Minecraft.getInstance().level;
		Entity grapple = world.getEntity(this.hookId);
		if (grapple == null)
			return;


		if (grapple instanceof GrapplinghookEntity hookEntity) {
			RopeSegmentHandler segmentHandler = hookEntity.getSegmentHandler();
			if (this.shouldAdd) {
				segmentHandler.actuallyAddSegment(this.index, this.pos, this.bottomFacing, this.topFacing);
			} else {
				segmentHandler.removeSegment(this.index);
			}
		}
	}

}
