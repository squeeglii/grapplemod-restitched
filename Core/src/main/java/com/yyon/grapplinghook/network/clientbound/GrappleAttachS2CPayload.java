package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.entity.grapplinghook.RopeSegmentHandler;
import com.yyon.grapplinghook.content.physics.PhysicsControllers;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.network.S2CPayload;
import com.yyon.grapplinghook.physics.io.RopeSnapshot;
import com.yyon.grapplinghook.util.Vec;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
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

public record GrappleAttachS2CPayload(int hookId, Vector3f hookPos, int holderId, BlockPos hookedBlock, RopeSnapshot ropeState, HookCustomization customization) implements S2CPayload {
    public static final ResourceLocation IDENTIFIER = GrappleMod.id("grapple_attach");
    public static final CustomPacketPayload.Type<GrappleAttachS2CPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

    public static final StreamCodec<RegistryFriendlyByteBuf, GrappleAttachS2CPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            GrappleAttachS2CPayload::hookId,
            ByteBufCodecs.VECTOR3F,
            GrappleAttachS2CPayload::hookPos,
            ByteBufCodecs.INT,
            GrappleAttachS2CPayload::holderId,
            BlockPos.STREAM_CODEC,
            GrappleAttachS2CPayload::hookedBlock,
            RopeSnapshot.STREAM_CODEC,
            GrappleAttachS2CPayload::ropeState,
            HookCustomization.STREAM_CODEC,
            GrappleAttachS2CPayload::customization,

            GrappleAttachS2CPayload::new
    );

    @NotNull
    @Override
    public Type<GrappleAttachS2CPayload> type() {
        return PAYLOAD_TYPE;
    }

    @Override
    public void process(ClientPlayNetworking.Context ctx) {
        ctx.client().execute(() -> {
            Level world = Minecraft.getInstance().level;

            if(world == null) {
                GrappleMod.LOGGER.warn("Network Message received in invalid context (World not present | GrappleAttach)");
                return;
            }

            Entity e = world.getEntity(this.hookId());

            if (e == null) {
                GrappleMod.LOGGER.warn("GrappleAttachMessage received for a hook that doesn't exist on the client side! (yet?)");
                return;
            }

            if (e instanceof GrapplinghookEntity grapple) {

                grapple.clientAttach(this.hookPos);
                RopeSegmentHandler segmentHandler = grapple.getSegmentHandler();
                segmentHandler.segments = this.ropeState.getSegments();
                segmentHandler.segmentTopSides = this.ropeState.getTopSides();
                segmentHandler.segmentBottomSides = this.ropeState.getBottomSides();

                Entity holder = world.getEntity(this.holderId);

                if (holder == null) {
                    GrappleMod.LOGGER.warn("Network Message received in invalid context (Holder does not exist | GrappleAttach)");
                    return;
                }

                segmentHandler.forceSetPos(new Vec(this.hookPos), Vec.positionVec(holder));
                GrappleModClient.get()
                        .getClientControllerManager()
                        .createControl(PhysicsControllers.GRAPPLING_HOOK, this.hookId(), this.holderId(), world, this.hookedBlock, this.customization);
            }
        });
    }
}
