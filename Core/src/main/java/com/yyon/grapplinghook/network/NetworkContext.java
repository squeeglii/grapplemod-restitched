package com.yyon.grapplinghook.network;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public final class NetworkContext {

    // Server
    private MinecraftServer server = null;
    private ServerPlayer sender = null;
    private ServerGamePacketListenerImpl serverHandle = null;

    // Client
    private Minecraft client = null;
    private ClientPacketListener clientHandle = null;

    // Read Only
    private PacketSender respond = null;
    private LogicalSide destination = LogicalSide.NONE;
    private boolean packetHandled = false;



    // Server
    public MinecraftServer getServer() {
        return server;
    }

    public ServerPlayer getSender() {
        return this.sender;
    }

    public ServerGamePacketListenerImpl getServerHandle() {
        return serverHandle;
    }


    // Client
    public Minecraft getClient() {
        return client;
    }

    public ClientPacketListener getClientHandle() {
        return clientHandle;
    }


    // Read Only
    public PacketSender respond() {
        return respond;
    }

    public LogicalSide getReceptionSide() {
        return this.destination;
    }

    public boolean isPacketHandled() {
        return packetHandled;
    }



    NetworkContext setSender(ServerPlayer sender) {
        this.sender = sender;
        return this;
    }

    NetworkContext setDestination(LogicalSide destination) {
        this.destination = destination;
        return this;
    }

    NetworkContext setServer(MinecraftServer server) {
        this.server = server;
        return this;
    }

    NetworkContext setClient(Minecraft client) {
        this.client = client;
        return this;
    }

    NetworkContext setRespond(PacketSender respond) {
        this.respond = respond;
        return this;
    }

    NetworkContext setServerHandle(ServerGamePacketListenerImpl serverHandle) {
        this.serverHandle = serverHandle;
        return this;
    }

    NetworkContext setClientHandle(ClientPacketListener clientHandle) {
        this.clientHandle = clientHandle;
        return this;
    }

    public void handle() {
        this.packetHandled = true;
    }
}
