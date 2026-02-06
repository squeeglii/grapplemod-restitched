package com.yyon.grapplinghook.physics;

import com.yyon.grapplinghook.content.registry.internal.ModAdvancementTriggers;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;


/**
 * All the custom physics is handled on the client side, however
 * advancements and other trackers run on the server-side.
 * This class acts as a bridge between the two, allowing server-side
 * access to some of the calculations made with the physics. This data
 * could be stale, so it should not be used for simulating physics on
 * the server side.
 * Currently though, it's not stored - just passed through to the advancement
 * trigger.
 */
public class ServerPhysicsObserver {

    private final HashMap<UUID, PlayerPhysicsFrame> lastFrame = new HashMap<>();

    public void receiveNewFrame(Player player, PlayerPhysicsFrame frame) {
        this.lastFrame.put(player.getUUID(), frame);

        if(!player.level().isClientSide)
            ModAdvancementTriggers.PHYSICS_UPDATE_TRIGGER.get().trigger(player, frame);
    }

    public Optional<PlayerPhysicsFrame> getMostRecentFrame(Player player) {
        return Optional.ofNullable(this.lastFrame.get(player.getUUID()));
    }

    public void resetHistory() {
        this.lastFrame.clear();
    }
}
