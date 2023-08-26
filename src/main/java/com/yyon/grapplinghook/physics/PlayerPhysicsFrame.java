package com.yyon.grapplinghook.physics;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

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


    public PlayerPhysicsFrame() {
        this.physicsControllerType = GrappleMod.id("none");
        this.speed = 0.0D;
    }

    public PlayerPhysicsFrame setPhysicsControllerType(ResourceLocation physicsControllerType) {
        this.physicsControllerType = physicsControllerType;
        return this;
    }

    public PlayerPhysicsFrame setSpeed(double speed) {
        this.speed = speed;
        return this;
    }


    public ResourceLocation getPhysicsControllerType() {
        return this.physicsControllerType;
    }

    public double getSpeed() {
        return this.speed;
    }

    @Override
    public String toString() {
        return "PhysFrame { Type: %s, Speed: %.02f }".formatted(
                this.getPhysicsControllerType(),
                this.getSpeed()
        );
    }

    public static PlayerPhysicsFrame fromBuffer(FriendlyByteBuf buf) {
        PlayerPhysicsFrame frame = new PlayerPhysicsFrame();

        frame.setPhysicsControllerType(buf.readResourceLocation())
             .setSpeed(buf.readDouble());

        return frame;
    }
}
