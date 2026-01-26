package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.item.type.IGlobalKeyObserver;
import com.yyon.grapplinghook.network.C2SPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
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

public record KeypressC2SPayload(IGlobalKeyObserver.Keys key, boolean isDown) implements C2SPayload {

	public static final ResourceLocation IDENTIFIER = GrappleMod.id("keypress");
	public static final CustomPacketPayload.Type<KeypressC2SPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

	public static final StreamCodec<RegistryFriendlyByteBuf, KeypressC2SPayload> STREAM_CODEC = StreamCodec.composite(
			IGlobalKeyObserver.Keys.STREAM_CODEC,
			KeypressC2SPayload::key,
			ByteBufCodecs.BOOL,
			KeypressC2SPayload::isDown,
			KeypressC2SPayload::new
	);

	@NotNull
	@Override
	public Type<KeypressC2SPayload> type() {
		return PAYLOAD_TYPE;
	}

	@Override
	public void process(ServerPlayNetworking.Context ctx) {
		final ServerPlayer player = ctx.player();

		ctx.server().execute(() -> {
			if(player == null) return;

			ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
			if (stack.getItem() instanceof IGlobalKeyObserver IGlobalKeyObserver) {
				if (isDown) {
					IGlobalKeyObserver.onCustomKeyDown(stack, player, key, true);
				} else {
					IGlobalKeyObserver.onCustomKeyUp(stack, player, key, true);
				}

				return;
			}

			stack = player.getItemInHand(InteractionHand.OFF_HAND);
			if (stack.getItem() instanceof IGlobalKeyObserver IGlobalKeyObserver) {
				if (isDown) {
					IGlobalKeyObserver.onCustomKeyDown(stack, player, key, false);
				} else {
					IGlobalKeyObserver.onCustomKeyUp(stack, player, key, false);
				}
			}
		});
	}
}
