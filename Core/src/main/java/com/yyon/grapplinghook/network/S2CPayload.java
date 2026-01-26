package com.yyon.grapplinghook.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/*
public record ...S2CPayload() implements S2CPayload {
	public static final ResourceLocation IDENTIFIER = GrappleMod.id();
	public static final CustomPacketPayload.Type<> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

	public static final StreamCodec<RegistryFriendlyByteBuf, > STREAM_CODEC = StreamCodec.composite(

	);

	@NotNull
	@Override
	public Type<> type() {
		return PAYLOAD_TYPE;
	}

	@Override
	public void process(ClientPlayNetworking.Context ctx) {

	}
}
 */
public interface S2CPayload extends CustomPacketPayload, S2CPayloadProcessor {

}
