package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.entity.grapplinghook.IExtendedSpawnPacketEntity;
import com.yyon.grapplinghook.network.S2CPayload;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public record AddExtraEntityDataS2CPayload(int entityId, byte[] extraData) implements S2CPayload {
    public static final ResourceLocation IDENTIFIER = GrappleMod.id("spawn_data");
    public static final CustomPacketPayload.Type<AddExtraEntityDataS2CPayload> PAYLOAD_TYPE = new Type<>(IDENTIFIER);

    public static final StreamCodec<RegistryFriendlyByteBuf, AddExtraEntityDataS2CPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            AddExtraEntityDataS2CPayload::entityId,
            ByteBufCodecs.BYTE_ARRAY,
            AddExtraEntityDataS2CPayload::extraData,
            AddExtraEntityDataS2CPayload::new
    );

    public AddExtraEntityDataS2CPayload(Entity entity) {
        this(entity.getId(), extractExtraData(entity));
    }

    @NotNull
    @Override
    public Type<AddExtraEntityDataS2CPayload> type() {
        return PAYLOAD_TYPE;
    }

    @Override
    public void process(ClientPlayNetworking.Context ctx) {
        ctx.client().execute(() -> {
            if(Minecraft.getInstance().level == null)
                throw new IllegalStateException("World must not be null");

            Entity entity = Minecraft.getInstance().level.getEntity(this.entityId);

            if (entity instanceof IExtendedSpawnPacketEntity entityAdditionalSpawnData) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(this.extraData));
                entityAdditionalSpawnData.readSpawnData(buf);
                buf.release();
            }
        });
    }

    private static byte[] extractExtraData(Entity entity) {
        if(!(entity instanceof IExtendedSpawnPacketEntity exSpawn))
            return new byte[0];

        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.buffer());
        exSpawn.writeSpawnData(byteBuf);

        return byteBuf.array();
    }
}
