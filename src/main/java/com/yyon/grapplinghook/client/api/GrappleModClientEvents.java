package com.yyon.grapplinghook.client.api;

import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Entity;

public class GrappleModClientEvents {

    public static final Event<HookAttach> HOOK_ATTACH = EventFactory.createArrayBacked(HookAttach.class,
            callbacks -> (serverPlayer, hookEntity) -> {
                for (HookAttach callback : callbacks)
                    callback.onHookAttach(serverPlayer, hookEntity);
            }
    );

    public static final Event<HookDetach> HOOK_DETACH = EventFactory.createArrayBacked(HookDetach.class,
            callbacks -> (serverPlayer) -> {
                for (HookDetach callback : callbacks)
                    callback.onHookDetach(serverPlayer);
            }
    );

    @FunctionalInterface
    public interface HookAttach {
        void onHookAttach(Entity thrower, GrapplinghookEntity hook);
    }

    @FunctionalInterface
    public interface HookDetach {
        void onHookDetach(Entity thrower);
    }

}
