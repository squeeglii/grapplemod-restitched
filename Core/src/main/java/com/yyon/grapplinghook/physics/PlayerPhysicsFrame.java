package com.yyon.grapplinghook.physics;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

/**
 * Side: Both
 *
 * Stores a snapshot of the player's current physics for use in the
 * ServerPhysicsObserver. Generated on the client side and synced with
 * a packet.
 */
public final class PlayerPhysicsFrame {

    public static final Codec<PlayerPhysicsFrame> CODEC = RecordCodecBuilder.create();

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerPhysicsFrame> STREAM_CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC,
            PlayerPhysicsFrame::getPhysicsControllerType,
            ByteBufCodecs.FLOAT,
            PlayerPhysicsFrame::getSpeed,
            ByteBufCodecs.BOOL,
            PlayerPhysicsFrame::isUsingRocket,
            PlayerPhysicsFrame::new
    );

    private ResourceLocation physicsControllerType;

    private float speed;
    private boolean isUsingRocket;


    public PlayerPhysicsFrame() {
        this.physicsControllerType = GrappleMod.id("none");
        this.speed = 0.0f;
        this.isUsingRocket = false;
    }

    // designed for codecs, not really got any checks
    private PlayerPhysicsFrame(ResourceLocation physicsControllerType, float speed, boolean isUsingRocket) {
        this.physicsControllerType = physicsControllerType;
        this.speed = speed;
        this.isUsingRocket = isUsingRocket;
    }

    public PlayerPhysicsFrame setPhysicsControllerType(ResourceLocation physicsControllerType) {
        this.physicsControllerType = physicsControllerType;
        return this;
    }

    public PlayerPhysicsFrame setSpeed(double speed) {
        return this.setSpeed((float) speed);
    }

    public PlayerPhysicsFrame setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public PlayerPhysicsFrame setUsingRocket(boolean usingRocket) {
        this.isUsingRocket = usingRocket;
        return this;
    }

    public ResourceLocation getPhysicsControllerType() {
        return this.physicsControllerType;
    }

    public float getSpeed() {
        return this.speed;
    }

    public boolean isUsingRocket() {
        return this.isUsingRocket;
    }

    @Override
    public String toString() {
        return "PhysFrame { Type: %s, Speed: %.02f }".formatted(
                this.getPhysicsControllerType(),
                this.getSpeed()
        );
    }

    public void writeToBuffer(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.getPhysicsControllerType());
        buf.writeDouble(this.getSpeed());
        buf.writeBoolean(this.isUsingRocket());
    }

    public static PlayerPhysicsFrame fromBuffer(FriendlyByteBuf buf) {
        PlayerPhysicsFrame frame = new PlayerPhysicsFrame();

        frame.setPhysicsControllerType(buf.readResourceLocation())
             .setSpeed(buf.readFloat())
             .setUsingRocket(buf.readBoolean());

        return frame;
    }
}
