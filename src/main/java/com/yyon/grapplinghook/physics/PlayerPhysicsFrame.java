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

    private final UUID playerUuid;

    private ResourceLocation physicsControllerType;

    private double speed;


    public PlayerPhysicsFrame(UUID playerId) {
        if(playerId == null)
            throw new IllegalArgumentException("Player ID must not be null");

        this.playerUuid = playerId;
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

    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    public ResourceLocation getPhysicsControllerType() {
        return this.physicsControllerType;
    }

    public double getSpeed() {
        return this.speed;
    }

    @Override
    public String toString() {
        return "PhysFrame { ID: %s, Type: %s, Speed: %.02f }".formatted(
                this.getPlayerUuid(),
                this.getPhysicsControllerType(),
                this.getSpeed()
        );
    }

    public static PlayerPhysicsFrame fromBuffer(FriendlyByteBuf buf) {
        UUID playerUUID = buf.readUUID();
        PlayerPhysicsFrame frame = new PlayerPhysicsFrame(playerUUID);

        frame.setPhysicsControllerType(buf.readResourceLocation())
             .setSpeed(buf.readDouble());

        return frame;
    }
}
