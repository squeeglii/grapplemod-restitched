package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.network.S2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
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

public record DetachSingleHookS2CPayload(int id, int hookId) implements S2CPayload {
    public static final ResourceLocation IDENTIFIER = GrappleMod.id("detach_single_hook");
    public static final CustomPacketPayload.Type<DetachSingleHookS2CPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

    public static final StreamCodec<RegistryFriendlyByteBuf, DetachSingleHookS2CPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            DetachSingleHookS2CPayload::id,
            ByteBufCodecs.INT,
            DetachSingleHookS2CPayload::hookId,
            DetachSingleHookS2CPayload::new
    );

    @NotNull
    @Override
    public Type<DetachSingleHookS2CPayload> type() {
        return PAYLOAD_TYPE;
    }

    @Override
    public void process(ClientPlayNetworking.Context ctx) {
        GrappleModClient.get().getClientControllerManager().receiveGrappleDetachHook(this.id(), this.hookId());
    }
}
