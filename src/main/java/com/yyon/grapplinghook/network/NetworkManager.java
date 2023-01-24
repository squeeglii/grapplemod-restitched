package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.clientbound.*;
import com.yyon.grapplinghook.network.serverbound.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

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


    public static boolean registerClient(String channelId, Function<FriendlyByteBuf, BaseMessageClient> etc) {
        return ClientPlayNetworking.registerGlobalReceiver(GrappleMod.id(channelId), NetworkManager.generateClientPacketHandler(etc));
    }

    public static boolean registerServer(String channelId, Function<FriendlyByteBuf, BaseMessageServer> etc) {
        return ServerPlayNetworking.registerGlobalReceiver(GrappleMod.id(channelId), NetworkManager.generateServerPacketHandler(etc));
    }

    public static void registerPacketListeners() {
        NetworkManager.registerClient("detach_single_hook", DetachSingleHookMessage::new);
        NetworkManager.registerClient("grapple_attach", GrappleAttachMessage::new);
        NetworkManager.registerClient("grapple_attach_pos", GrappleAttachPosMessage::new);
        NetworkManager.registerClient("grapple_detatch", GrappleDetachMessage::new);
        NetworkManager.registerClient("logged_in", LoggedInMessage::new);
        NetworkManager.registerClient("segment", SegmentMessage::new);

        NetworkManager.registerServer("grapple_end", GrappleEndMessage::new);
        NetworkManager.registerServer("grapple_modifier", GrappleModifierMessage::new);
        NetworkManager.registerServer("keypress", KeypressMessage::new);
        NetworkManager.registerServer("player_movement", PlayerMovementMessage::new);
    }
}
