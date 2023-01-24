package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.network.LogicalSide;
import com.yyon.grapplinghook.network.NetworkContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Supplier;

public abstract class BaseMessageClient {
	public BaseMessageClient(FriendlyByteBuf buf) {
		this.decode(buf);
	}
	
	public BaseMessageClient() {
	}
	
	public abstract void decode(FriendlyByteBuf buf);
	
	public abstract void encode(FriendlyByteBuf buf);

    @Environment(EnvType.CLIENT)
    public abstract void processMessage(NetworkContext ctx);
    
    public void onMessageReceived(Supplier<NetworkContext> ctxSupplier) {
        NetworkContext ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getReceptionSide();
        if (sideReceived != LogicalSide.FOR_CLIENT) {
			GrappleMod.LOGGER.warn("message received on wrong side:" + ctx.getReceptionSide());
			return;
        }
        
        ctx.handle();
        
        ctx.getClient().execute(() ->
        	GrappleModClient.get().onMessageReceivedClient(this, ctx)
        );
    }

}
