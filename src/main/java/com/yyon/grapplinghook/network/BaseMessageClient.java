package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.client.GrappleModClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.FriendlyByteBuf;

public abstract class BaseMessageClient {
	public BaseMessageClient(FriendlyByteBuf buf) {
		this.decode(buf);
	}
	
	public BaseMessageClient() {
	}
	
	public abstract void decode(FriendlyByteBuf buf);
	
	public abstract void encode(FriendlyByteBuf buf);

    @Environment(EnvType.CLIENT)
    public abstract void processMessage(NetworkEvent.Context ctx);
    
    public void onMessageReceived(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
        if (sideReceived != LogicalSide.CLIENT) {
			GrappleMod.LOGGER.warn("message received on wrong side:" + ctx.getDirection().getReceptionSide());
			return;
        }
        
        ctx.setPacketHandled(true);
        
        ctx.enqueueWork(() -> 
        	GrappleModClient.get().onMessageReceivedClient(this, ctx)
        );
    }

}
