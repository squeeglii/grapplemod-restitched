package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.C2SPayload;
import com.yyon.grapplinghook.physics.ServerHookEntityTracker;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

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

// previously GrappleEndMessage
public record HaltCustomPhysicsC2SPayload(int entityId, HashSet<Integer> hookEntityIds) implements C2SPayload {

	public static final ResourceLocation IDENTIFIER = GrappleMod.id("grapple_end");
	public static final CustomPacketPayload.Type<HaltCustomPhysicsC2SPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

	public static final StreamCodec<RegistryFriendlyByteBuf, HaltCustomPhysicsC2SPayload> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT,
			HaltCustomPhysicsC2SPayload::entityId,
			ByteBufCodecs.collection(HashSet::new, ByteBufCodecs.INT),
			HaltCustomPhysicsC2SPayload::hookEntityIds,
			HaltCustomPhysicsC2SPayload::new
	);

	@NotNull
	@Override
	public Type<HaltCustomPhysicsC2SPayload> type() {
		return PAYLOAD_TYPE;
	}

	@Override
	public void process(ServerPlayNetworking.Context ctx) {
		int id = this.entityId;
		ServerPlayer player = ctx.player();

		ctx.server().execute(() -> {
			if (player == null) return;
			ServerHookEntityTracker.handleGrappleEndFromClient(id, player.level(), this.hookEntityIds);
		});
	}
}
