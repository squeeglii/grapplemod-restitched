package com.yyon.grapplinghook.network.clientbound;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.entity.grapplehook.GrapplehookEntity;
import com.yyon.grapplinghook.entity.grapplehook.IExtendedSpawnPacketEntity;
import com.yyon.grapplinghook.network.NetworkContext;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;
import java.util.UUID;

public class AddGrappleHookEntityMessage extends BaseMessageClient {

    public Entity entity;
    public int typeId;
    public int entityId;
    public UUID uuid;
    public double posX, posY, posZ;
    public byte pitch, yaw, headYaw;
    public int velX, velY, velZ;
    public FriendlyByteBuf buf;

    public AddGrappleHookEntityMessage(FriendlyByteBuf buf) { this.decode(buf); }

    public AddGrappleHookEntityMessage(GrapplehookEntity e) {
        this.entity = e;
        this.typeId = BuiltInRegistries.ENTITY_TYPE.getId(e.getType());
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
        this.buf = null;
    }

    @Override
    public void decode(FriendlyByteBuf buf) {
        this.entity = null;
        this.typeId = buf.readVarInt();
        this.entityId = buf.readInt();
        this.uuid = new UUID(buf.readLong(), buf.readLong());
        this.posX = buf.readDouble();
        this.posY = buf.readDouble();
        this.posZ = buf.readDouble();
        this.pitch = buf.readByte();
        this.yaw = buf.readByte();
        this.headYaw = buf.readByte();
        this.velX = buf.readShort();
        this.velY = buf.readShort();
        this.velZ = buf.readShort();
        this.buf = buf;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeVarInt(this.typeId);
        buf.writeInt(this.entityId);
        buf.writeLong(this.uuid.getMostSignificantBits());
        buf.writeLong(this.uuid.getLeastSignificantBits());
        buf.writeDouble(this.posX);
        buf.writeDouble(this.posY);
        buf.writeDouble(this.posZ);
        buf.writeByte(this.pitch);
        buf.writeByte(this.yaw);
        buf.writeByte(this.headYaw);
        buf.writeShort(this.velX);
        buf.writeShort(this.velY);
        buf.writeShort(this.velZ);
        if (this.entity instanceof IExtendedSpawnPacketEntity entityAdditionalSpawnData)
        {
            final FriendlyByteBuf spawnDataBuffer = new FriendlyByteBuf(Unpooled.buffer());

            entityAdditionalSpawnData.writeSpawnData(spawnDataBuffer);

            buf.writeVarInt(spawnDataBuffer.readableBytes());
            buf.writeBytes(spawnDataBuffer);

            spawnDataBuffer.release();
        } else
        {
            buf.writeVarInt(0);
        }
    }

    @Override
    public ResourceLocation getChannel() {
        return GrappleMod.id("add_grapplehook_entity");
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void processMessage(NetworkContext ctx) {
        ctx.getClient().execute(() -> {
            try
            {
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.byId(this.typeId);
                ClientLevel world = ctx.getClient().level;
                Entity e =  type.create(world); // type.customClientSpawn(this, world);

                if (e == null) return;

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
                    entityAdditionalSpawnData.readSpawnData(this.buf);
                }
            } finally
            {
                this.buf.release();
            }
        });

        ctx.handle();
    }
}
