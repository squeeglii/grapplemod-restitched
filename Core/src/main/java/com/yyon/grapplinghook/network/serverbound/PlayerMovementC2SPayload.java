package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.C2SPayload;
import com.yyon.grapplinghook.util.Vec;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

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

public record PlayerMovementC2SPayload(int entityId, Vector3f pos, Vector3f motion) implements C2SPayload {
	public static final ResourceLocation IDENTIFIER = GrappleMod.id("player_movement");
	public static final CustomPacketPayload.Type<PlayerMovementC2SPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

	public static final StreamCodec<RegistryFriendlyByteBuf, PlayerMovementC2SPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			PlayerMovementC2SPayload::entityId,
			ByteBufCodecs.VECTOR3F,
			PlayerMovementC2SPayload::pos,
			ByteBufCodecs.VECTOR3F,
			PlayerMovementC2SPayload::motion,

			PlayerMovementC2SPayload::new
	);

	@NotNull
	@Override
	public Type<PlayerMovementC2SPayload> type() {
		return PAYLOAD_TYPE;
	}

	@Override
	public void process(ServerPlayNetworking.Context ctx) {
		final ServerPlayer referencedPlayer = ctx.player();

		ctx.server().execute(() -> {
			if(referencedPlayer.getId() == this.entityId) {
				new Vec(this.pos()).applyAsPositionTo(referencedPlayer);
				new Vec(this.motion()).applyAsMotionTo(referencedPlayer);

				referencedPlayer.connection.resetPosition();

				if (!referencedPlayer.onGround()) {
					if (this.motion.y() >= 0) {
						referencedPlayer.fallDistance = 0;
					} else {
						double gravity = 0.05 * 2;
						// d = v^2 / 2g
						referencedPlayer.fallDistance = (float) (Math.pow(this.motion.y(), 2) / (2 * gravity));
					}
				}
			}
		});
	}
}
