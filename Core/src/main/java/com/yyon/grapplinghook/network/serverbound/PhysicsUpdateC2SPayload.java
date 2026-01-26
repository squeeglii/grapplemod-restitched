package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.C2SPayload;
import com.yyon.grapplinghook.physics.PlayerPhysicsFrame;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record PhysicsUpdateC2SPayload(PlayerPhysicsFrame frame) implements C2SPayload {

    public static final ResourceLocation IDENTIFIER = GrappleMod.id("physics_update");
    public static final CustomPacketPayload.Type<PhysicsUpdateC2SPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

    public static final StreamCodec<RegistryFriendlyByteBuf, PhysicsUpdateC2SPayload> STREAM_CODEC = StreamCodec.composite(
            PlayerPhysicsFrame.STREAM_CODEC,
            PhysicsUpdateC2SPayload::frame,
            PhysicsUpdateC2SPayload::new
    );

    public PhysicsUpdateC2SPayload() {
        this(new PlayerPhysicsFrame());
    }

    @NotNull
    @Override
    public Type<PhysicsUpdateC2SPayload> type() {
        return PAYLOAD_TYPE;
    }

    @Override
    public void process(ServerPlayNetworking.Context ctx) {
        ctx.server().execute(() -> GrappleMod
                .get()
                .getServerPhysicsObserver()
                .receiveNewFrame(ctx.player(), this.frame)
        );
    }

}
