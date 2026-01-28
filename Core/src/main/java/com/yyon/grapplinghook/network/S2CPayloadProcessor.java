package com.yyon.grapplinghook.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import java.util.function.Supplier;

public interface S2CPayloadProcessor {

    void process(ClientPlayNetworking.Context ctx);

}
