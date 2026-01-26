package com.yyon.grapplinghook.network.serverbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.C2SPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

public record SaveGrappleStateC2SPayload(Unit inst) implements C2SPayload {
    public static final ResourceLocation IDENTIFIER = GrappleMod.id("save_grapple_state");
    public static final CustomPacketPayload.Type<SaveGrappleStateC2SPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

    public static final StreamCodec<RegistryFriendlyByteBuf, SaveGrappleStateC2SPayload> STREAM_CODEC = StreamCodec.composite(
            StreamCodec.unit(Unit.INSTANCE),
            SaveGrappleStateC2SPayload::inst,
            SaveGrappleStateC2SPayload::new
    );

    public SaveGrappleStateC2SPayload() {
        this(Unit.INSTANCE);
    }

    @NotNull
    @Override
    public Type<SaveGrappleStateC2SPayload> type() {
        return PAYLOAD_TYPE;
    }

    @Override
    public void process(ServerPlayNetworking.Context ctx) {
        //todo: implement
    }
}
