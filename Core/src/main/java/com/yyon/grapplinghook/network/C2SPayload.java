package com.yyon.grapplinghook.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/*
public record PlayerMovementC2SPayload() implements C2SPayload {
	public static final ResourceLocation IDENTIFIER = GrappleMod.id();
	public static final CustomPacketPayload.Type<PlayerMovementC2SPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

	public static final StreamCodec<RegistryFriendlyByteBuf, PlayerMovementC2SPayload> STREAM_CODEC = StreamCodec.composite(

	);

	@NotNull
	@Override
	public Type<PlayerMovementC2SPayload> type() {
		return PAYLOAD_TYPE;
	}

	@Override
	public void process(ServerPlayNetworking.Context ctx) {

	}
}
 */

public interface C2SPayload extends CustomPacketPayload, C2SPayloadProcessor {
}
