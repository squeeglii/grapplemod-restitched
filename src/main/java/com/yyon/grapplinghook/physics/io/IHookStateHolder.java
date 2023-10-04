package com.yyon.grapplinghook.physics.io;

import org.spongepowered.asm.mixin.Unique;

import java.util.Optional;

public interface IHookStateHolder {

    void grapplemod$resetLastHookState();

    void grapplemod$overwriteLastHookState(SerializableHookState hookState);

    Optional<SerializableHookState> grapplemod$getLastHookState();
}
