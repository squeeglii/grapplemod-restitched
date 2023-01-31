package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.entity.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.entity.grapplehook.IExtendedSpawnPacketEntity;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public class AddGrappleHookEntityPacket implements Packet<ClientGamePacketListener> {

    public Entity entity;
    public int typeId;
    public int entityId;
    public UUID uuid;
    public double posX, posY, posZ;
    public byte pitch, yaw, headYaw;
    public int velX, velY, velZ;

    public byte[] extraData;


    public AddGrappleHookEntityPacket(GrapplehookEntity e) {
        this.entity = e;
        this.typeId = Registry.ENTITY_TYPE.getId(e.getType());
        this.entityId = e.getId();
        this.uuid = e.getUUID();
        this.posX = e.getX();
        this.posY = e.getY();
        this.posZ = e.getZ();
        this.pitch = (byte) Mth.floor(e.getXRot() * 256.0F / 360.0F);
        this.yaw = (byte) Mth.floor(e.getYRot() * 256.0F / 360.0F);
        this.headYaw = (byte) (e.getYHeadRot() * 256.0F / 360.0F);
        Vec3 vec3d = e.getDeltaMovement();
        double d1 = Mth.clamp(vec3d.x, -3.9D, 3.9D);
        double d2 = Mth.clamp(vec3d.y, -3.9D, 3.9D);
        double d3 = Mth.clamp(vec3d.z, -3.9D, 3.9D);
        this.velX = (int) (d1 * 8000.0D);
        this.velY = (int) (d2 * 8000.0D);
        this.velZ = (int) (d3 * 8000.0D);

        if (this.entity instanceof IExtendedSpawnPacketEntity entityAdditionalSpawnData)
        {
            final FriendlyByteBuf spawnDataBuffer = new FriendlyByteBuf(Unpooled.buffer());

            entityAdditionalSpawnData.writeSpawnData(spawnDataBuffer);

            int byteCount = spawnDataBuffer.readableBytes();
            this.extraData = new byte[byteCount];
            spawnDataBuffer.getBytes(0, this.extraData);

            spawnDataBuffer.release();
        }
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.typeId);
        buffer.writeInt(this.entityId);
        buffer.writeLong(this.uuid.getMostSignificantBits());
        buffer.writeLong(this.uuid.getLeastSignificantBits());
        buffer.writeDouble(this.posX);
        buffer.writeDouble(this.posY);
        buffer.writeDouble(this.posZ);
        buffer.writeByte(this.pitch);
        buffer.writeByte(this.yaw);
        buffer.writeByte(this.headYaw);
        buffer.writeShort(this.velX);
        buffer.writeShort(this.velY);
        buffer.writeShort(this.velZ);

        if (this.entity instanceof IExtendedSpawnPacketEntity entityAdditionalSpawnData)
        {
            final FriendlyByteBuf spawnDataBuffer = new FriendlyByteBuf(Unpooled.buffer());

            entityAdditionalSpawnData.writeSpawnData(spawnDataBuffer);

            int byteCount = spawnDataBuffer.readableBytes();
            buffer.writeVarInt(byteCount);
            buffer.writeBytes(spawnDataBuffer);

            spawnDataBuffer.release();
        } else
        {
            buffer.writeVarInt(0);
        }
    }

    @Override
    public void handle(ClientGamePacketListener handler) {
        try
        {
            EntityType<?> type = Registry.ENTITY_TYPE.byId(this.typeId);
            ClientLevel world = Minecraft.getInstance().level;

            if (world == null) return;

            Entity e =  type.create(world); // type.customClientSpawn(this, world);

            if (e == null) return;

            this.entity = e;
            /*
             * Sets the postiion on the client, Mirrors what
             * Entity#recreateFromPacket and LivingEntity#recreateFromPacket does.
             */
            e.syncPacketPositionCodec(this.posX, this.posY, this.posZ);
            e.absMoveTo(this.posX, this.posY, this.posZ, (this.yaw * 360) / 256.0F, (this.pitch * 360) / 256.0F);
            e.setYHeadRot((this.headYaw * 360) / 256.0F);
            e.setYBodyRot((this.headYaw * 360) / 256.0F);

            e.setId(this.entityId);
            e.setUUID(this.uuid);
            world.putNonPlayerEntity(this.entityId, e);
            e.lerpMotion(this.velX / 8000.0, this.velY / 8000.0, this.velZ / 8000.0);
            if (e instanceof IExtendedSpawnPacketEntity entityAdditionalSpawnData)
            {
                FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(this.extraData));
                entityAdditionalSpawnData.readSpawnData(buf);
                buf.release();
            }

        } catch (Exception err) {
            err.printStackTrace();
        }
    }
}
