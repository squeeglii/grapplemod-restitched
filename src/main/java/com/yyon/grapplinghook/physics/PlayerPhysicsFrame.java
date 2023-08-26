package com.yyon.grapplinghook.physics;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.resources.ResourceLocation;

import java.util.UUID;

public class PlayerPhysicsFrame {

    private final UUID playerUuid;

    private final ResourceLocation physicsControllerType;

    private final double speed;


    public PlayerPhysicsFrame(UUID playerId) {
        this.playerUuid = playerId;
        this.physicsControllerType = GrappleMod.id("none");
        this.speed = 0.0D;
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
}
