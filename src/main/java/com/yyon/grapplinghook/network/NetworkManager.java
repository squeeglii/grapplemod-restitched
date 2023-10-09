package com.yyon.grapplinghook.network;

import com.mojang.authlib.GameProfile;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.clientbound.*;
import com.yyon.grapplinghook.network.serverbound.*;
import com.yyon.grapplinghook.physics.ServerHookEntityTracker;
import com.yyon.grapplinghook.physics.io.IHookStateHolder;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.S2CPlayChannelEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Function;

public class NetworkManager {

    protected static ClientPlayNetworking.PlayChannelHandler generateClientPacketHandler(Function<FriendlyByteBuf, BaseMessageClient> packetFactory) {
        return (client, handler, buf, responseSender) -> {
            BaseMessageClient packet = packetFactory.apply(buf);
            NetworkContext context = new NetworkContext()
                    .setDestination(LogicalSide.FOR_CLIENT)
                    .setClient(client)
                    .setClientHandle(handler)
                    .setRespond(responseSender);

            packet.processMessage(context);
        };
    }

    protected static ServerPlayNetworking.PlayChannelHandler generateServerPacketHandler(Function<FriendlyByteBuf, BaseMessageServer> packetFactory) {
        return (server, player, handler, buf, responseSender) -> {
            BaseMessageServer packet = packetFactory.apply(buf);
            NetworkContext context = new NetworkContext()
                    .setDestination(LogicalSide.FOR_SERVER)
                    .setServer(server)
                    .setServerHandle(handler)
                    .setSender(player)
                    .setRespond(responseSender);

            packet.processMessage(context);
        };
    }


    public static void registerClient(String channelId, Function<FriendlyByteBuf, BaseMessageClient> etc) {
        ClientPlayNetworking.registerGlobalReceiver(GrappleMod.id(channelId), NetworkManager.generateClientPacketHandler(etc));
    }

    public static void registerServer(String channelId, Function<FriendlyByteBuf, BaseMessageServer> etc) {
        ServerPlayNetworking.registerGlobalReceiver(GrappleMod.id(channelId), NetworkManager.generateServerPacketHandler(etc));
    }

    public static void packetToServer(BaseMessageServer server) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        server.encode(buf);
        ClientPlayNetworking.send(server.getChannel(), buf);
    }

    public static void packetToClient(BaseMessageClient client, ServerPlayer... players) {
        if(players.length == 0) {
            GrappleMod.LOGGER.warn("Missing any players to send a packet to!");
            return;
        }

        FriendlyByteBuf buf = PacketByteBufs.create();
        client.encode(buf);

        for(ServerPlayer player: players)
            ServerPlayNetworking.send(player, client.getChannel(), buf);
    }

    public static void registerClientPacketListeners() {
        NetworkManager.registerClient("data", AddExtraDataMessage::new);
        NetworkManager.registerClient("detach_single_hook", DetachSingleHookMessage::new);
        NetworkManager.registerClient("grapple_attach", GrappleAttachMessage::new);
        NetworkManager.registerClient("grapple_attach_pos", GrappleAttachPosMessage::new);
        NetworkManager.registerClient("grapple_detach", GrappleDetachMessage::new);
        NetworkManager.registerClient("logged_in", LoggedInMessage::new);
        NetworkManager.registerClient("segment", SegmentMessage::new);
        NetworkManager.registerClient("restore_grapple_state", RestoreGrappleStateMessage::new);
    }

    public static void registerPacketListeners() {
        NetworkManager.registerServer("grapple_end", GrappleEndMessage::new);
        NetworkManager.registerServer("grapple_modifier", GrappleModifierMessage::new);
        NetworkManager.registerServer("keypress", KeypressMessage::new);
        NetworkManager.registerServer("player_movement", PlayerMovementMessage::new);
        NetworkManager.registerServer("physics_update", PhysicsUpdateMessage::new);
        NetworkManager.registerServer("save_grapple_state", SaveGrappleStateMessage::new);

        S2CPlayChannelEvents.REGISTER.register((handler, sender, server, channels) -> {
            ServerPlayer player = handler.player;

            // Sanity check
            if(player.level().isClientSide)
                return;

            if(ServerHookEntityTracker.isSavedHookStateValid(player))
                ServerHookEntityTracker.applyFromSavedHookState(player);

            IHookStateHolder hookStateHolder = (IHookStateHolder) player;
            hookStateHolder.grapplemod$resetLastHookState();
        });
    }
}
