package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.clientbound.*;
import com.yyon.grapplinghook.network.serverbound.*;
import com.yyon.grapplinghook.physics.ServerHookEntityTracker;
import com.yyon.grapplinghook.physics.io.IHookStateHolder;
import com.yyon.grapplinghook.util.scheduling.Ticker;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public class NetworkManager {

    public static <T extends C2SPayload> void registerC2SPacket(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, C2SPayloadProcessor::process);
    }

    public static <T extends S2CPayload> void registerS2CPacket(CustomPacketPayload.Type<T> type, StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        PayloadTypeRegistry.playS2C().register(type, codec);
        ClientPlayNetworking.registerGlobalReceiver(type, S2CPayloadProcessor::process);
    }

    public static void registerAll() {
        registerC2SPacket(SaveGrappleStateC2SPayload.PAYLOAD_TYPE, SaveGrappleStateC2SPayload.STREAM_CODEC);
        registerC2SPacket(PlayerMovementC2SPayload.PAYLOAD_TYPE, PlayerMovementC2SPayload.STREAM_CODEC);
        registerC2SPacket(PhysicsUpdateC2SPayload.PAYLOAD_TYPE, PhysicsUpdateC2SPayload.STREAM_CODEC);
        registerC2SPacket(KeypressC2SPayload.PAYLOAD_TYPE, KeypressC2SPayload.STREAM_CODEC);
        registerC2SPacket(SyncModifierTableC2SPayload.PAYLOAD_TYPE,  SyncModifierTableC2SPayload.STREAM_CODEC);
        registerC2SPacket(HaltCustomPhysicsC2SPayload.PAYLOAD_TYPE,  HaltCustomPhysicsC2SPayload.STREAM_CODEC);

        registerS2CPacket(AddExtraEntityDataS2CPayload.PAYLOAD_TYPE, AddExtraEntityDataS2CPayload.STREAM_CODEC);
        registerS2CPacket(DetachSingleHookS2CPayload.PAYLOAD_TYPE, DetachSingleHookS2CPayload.STREAM_CODEC);

        S2CPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> {
            ServerPlayer player = handler.player;

            // Sanity check
            if(player.level().isClientSide)
                return;

            Ticker.grappleMod().queue(3, () -> {
                if(ServerHookEntityTracker.isSavedHookStateValid(player))
                    ServerHookEntityTracker.applyFromSavedHookState(player);

                IHookStateHolder hookStateHolder = (IHookStateHolder) player;
                hookStateHolder.grapplemod$resetLastHookState();
            });
        });
    }

    public static void registerClientPacketListeners() {
        //NetworkManager.addClientSideListener("data", AddExtraEntityDataS2CPacket::new);
        //NetworkManager.addClientSideListener("detach_single_hook", DetachSingleHookS2CPayload::new);
        NetworkManager.addClientSideListener("grapple_attach", GrappleAttachMessage::new);
        NetworkManager.addClientSideListener("grapple_attach_pos", GrappleAttachPosMessage::new);
        NetworkManager.addClientSideListener("grapple_detach", GrappleDetachMessage::new);
        NetworkManager.addClientSideListener("logged_in", LoggedInMessage::new);
        NetworkManager.addClientSideListener("segment", SegmentMessage::new);
        NetworkManager.addClientSideListener("restore_grapple_state", RestoreGrappleStateMessage::new);
    }

    public static void packetToServer(C2SPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    public static void packetToClient(S2CPayload payload, ServerPlayer... players) {
        if(players.length == 0) {
            GrappleMod.LOGGER.warn("Missing any players to send a packet to!");
            return;
        }

        for(ServerPlayer player: players)
            ServerPlayNetworking.send(player, payload);
    }
}
