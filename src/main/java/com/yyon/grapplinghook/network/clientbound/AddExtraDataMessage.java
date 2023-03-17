package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.entity.grapplinghook.IExtendedSpawnPacketEntity;
import com.yyon.grapplinghook.network.NetworkContext;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class AddExtraDataMessage extends BaseMessageClient {

    private Entity entity;

    private int entityId;
    private byte[] extraData;

    public AddExtraDataMessage(Entity entity) {
        this.entity = entity;
        this.extraData = new byte[0];
    }

    public AddExtraDataMessage(FriendlyByteBuf buf) {
        super(buf);
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.entityId = buf.readVarInt();

        int readableBytes = buf.readVarInt();
        this.extraData = new byte[readableBytes];
        buf.readBytes(this.extraData);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.entity.getId());

        if (entity instanceof IExtendedSpawnPacketEntity entityAdditionalSpawnData) {
            final FriendlyByteBuf spawnDataBuffer = new FriendlyByteBuf(Unpooled.buffer());

            entityAdditionalSpawnData.writeSpawnData(spawnDataBuffer);

            int byteCount = spawnDataBuffer.readableBytes();
            buf.writeVarInt(byteCount);
            buf.writeBytes(spawnDataBuffer);

            spawnDataBuffer.release();

        } else {
            buf.writeVarInt(0);
        }
    }

    @Override
    public ResourceLocation getChannel() {
        return GrappleMod.id("data");
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void processMessage(NetworkContext ctx) {
        ctx.getClient().execute(() -> {
            if(Minecraft.getInstance().level == null)
                throw new IllegalStateException("World must not be null");

            this.entity = Minecraft.getInstance().level.getEntity(this.entityId);

            if (this.entity instanceof IExtendedSpawnPacketEntity entityAdditionalSpawnData) {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(this.extraData));
                entityAdditionalSpawnData.readSpawnData(buf);
                buf.release();
            }
        });
    }
}
