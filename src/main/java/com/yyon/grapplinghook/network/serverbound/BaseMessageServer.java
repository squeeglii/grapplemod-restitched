package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.LogicalSide;
import com.yyon.grapplinghook.network.NetworkContext;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Supplier;

public abstract class BaseMessageServer {
	public BaseMessageServer(FriendlyByteBuf buf) {
		this.decode(buf);
	}
	
	public BaseMessageServer() {
	}
	
	public abstract void decode(FriendlyByteBuf buf);
	
	public abstract void encode(FriendlyByteBuf buf);

    public abstract void processMessage(NetworkContext ctx);
    
    public void onMessageReceived(Supplier<NetworkContext> ctxSupplier) {
        NetworkContext ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getReceptionSide();
        if (sideReceived != LogicalSide.FOR_SERVER) {
			GrappleMod.LOGGER.warn("message received on wrong side:" + ctx.getReceptionSide());
			return;
        }
        
        ctx.handle();
        
        final ServerPlayer sendingPlayer = ctx.getSender();
        if (sendingPlayer == null) {
        	GrappleMod.LOGGER.warn("EntityPlayerMP was null when message was received");
        }

        ctx.getServer().execute(() -> processMessage(ctx));
    }
}
