package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.network.S2CPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;

public record RestoreGrappleStateS2CPayload(Unit inst) implements S2CPayload {
    public static final ResourceLocation IDENTIFIER = GrappleMod.id("restore_grapple_state");
    public static final CustomPacketPayload.Type<RestoreGrappleStateS2CPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

    public static final StreamCodec<RegistryFriendlyByteBuf, RestoreGrappleStateS2CPayload> STREAM_CODEC = StreamCodec.composite(
            StreamCodec.unit(Unit.INSTANCE),
            RestoreGrappleStateS2CPayload::inst,
            RestoreGrappleStateS2CPayload::new
    );

    public RestoreGrappleStateS2CPayload() {
        this(Unit.INSTANCE);
    }

    @NotNull
    @Override
    public Type<RestoreGrappleStateS2CPayload> type() {
        return PAYLOAD_TYPE;
    }

    @Override
    public void process(ClientPlayNetworking.Context ctx) {
        //todo: reimplement.
    }



}
