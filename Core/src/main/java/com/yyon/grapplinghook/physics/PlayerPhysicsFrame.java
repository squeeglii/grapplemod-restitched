package com.yyon.grapplinghook.physics;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

/**
 * Side: Both
 *
 * Stores a snapshot of the player's current physics for use in the
 * ServerPhysicsObserver. Generated on the client side and synced with
 * a packet.
 */
public final class PlayerPhysicsFrame {

    private ResourceLocation physicsControllerType;

    private double speed;
    private boolean isUsingRocket;


    public PlayerPhysicsFrame() {
        this.physicsControllerType = GrappleMod.id("none");
        this.speed = 0.0D;
        this.isUsingRocket = false;
    }

    public PlayerPhysicsFrame setPhysicsControllerType(ResourceLocation physicsControllerType) {
        this.physicsControllerType = physicsControllerType;
        return this;
    }

    public PlayerPhysicsFrame setSpeed(double speed) {
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

    public double getSpeed() {
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
             .setSpeed(buf.readDouble())
             .setUsingRocket(buf.readBoolean());

        return frame;
    }
}
