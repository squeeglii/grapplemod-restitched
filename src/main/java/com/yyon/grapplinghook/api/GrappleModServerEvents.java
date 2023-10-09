package com.yyon.grapplinghook.api;

import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

public class GrappleModServerEvents {

    public static final Event<HookThrown> HOOK_THROW = EventFactory.createArrayBacked(HookThrown.class,
            callbacks -> (serverPlayer, hookEntity) -> {
                for (HookThrown callback : callbacks)
                    callback.onHookThrown(serverPlayer, hookEntity);
            }
    );

    public static final Event<HookRetracted> HOOK_RETRACT = EventFactory.createArrayBacked(HookRetracted.class,
            callbacks -> (serverPlayer) -> {
                for (HookRetracted callback : callbacks)
                    callback.onHookRetracted(serverPlayer);
            }
    );

    public static final Event<HookAttach> HOOK_ATTACH = EventFactory.createArrayBacked(HookAttach.class,
            callbacks -> (serverPlayer, hookEntity) -> {
                for (HookAttach callback : callbacks)
                    callback.onHookAttach(serverPlayer, hookEntity);
            }
    );



    @FunctionalInterface
    public interface HookThrown {
        void onHookThrown(Entity thrower, GrapplinghookEntity hook);
    }

    @FunctionalInterface
    public interface HookRetracted {
        void onHookRetracted(Entity thrower);
    }

    @FunctionalInterface
    public interface HookAttach {
        void onHookAttach(Entity thrower, GrapplinghookEntity hook);
    }

}
