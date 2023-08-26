package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.NetworkContext;
import com.yyon.grapplinghook.physics.PlayerPhysicsFrame;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class PhysicsUpdateMessage extends BaseMessageServer {

    private PlayerPhysicsFrame frame;


    public PhysicsUpdateMessage(FriendlyByteBuf buf) {
        this.decode(buf);
    }

    public PhysicsUpdateMessage(PlayerPhysicsFrame frame) {
        this.frame = frame;
    }

    public PhysicsUpdateMessage() {
        this.frame = new PlayerPhysicsFrame();
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.frame = PlayerPhysicsFrame.fromBuffer(buf);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.frame.getPhysicsControllerType());
        buf.writeDouble(this.frame.getSpeed());
    }

    @Override
    public ResourceLocation getChannel() {
        return GrappleMod.id("physics_update");
    }

    @Override
    public void processMessage(NetworkContext ctx) {
        ctx.getServer().execute(() -> GrappleMod
                .get()
                .getServerPhysicsObserver()
                .receiveNewFrame(ctx.getSender(), this.frame)
        );
    }
}
