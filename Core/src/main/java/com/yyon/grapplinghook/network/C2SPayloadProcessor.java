package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public interface C2SPayloadProcessor {

    void process(ServerPlayNetworking.Context ctx);
    
    default void onMessageReceived(Supplier<ServerPlayNetworking.Context> ctxSupplier) {
        ServerPlayNetworking.Context ctx = ctxSupplier.get();
        
        final Player sendingPlayer = ctx.player();
        if (sendingPlayer == null) {
        	GrappleMod.LOGGER.warn("EntityPlayerMP was null when message was received");
        }

        ctx.server().execute(() -> this.process(ctx));
    }
}
