package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.NetworkContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class SaveGrappleStateMessage extends BaseMessageServer {

    public SaveGrappleStateMessage(ByteBuf packetIn) {

    }


    @Override
    public ResourceLocation getChannel() {
        return GrappleMod.id("save_grapple_state");
    }

    @Override
    public void decode(FriendlyByteBuf buf) {

    }

    @Override
    public void encode(FriendlyByteBuf buf) {

    }

    @Override
    public void processMessage(NetworkContext ctx) {

    }
}
