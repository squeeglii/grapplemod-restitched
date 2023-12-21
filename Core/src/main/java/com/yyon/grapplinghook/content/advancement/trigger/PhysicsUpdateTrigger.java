package com.yyon.grapplinghook.content.advancement.trigger;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yyon.grapplinghook.content.advancement.PhysicsFramePredicate;
import com.yyon.grapplinghook.physics.PlayerPhysicsFrame;
import net.minecraft.advancements.critereon.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PhysicsUpdateTrigger extends SimpleCriterionTrigger<PhysicsUpdateTrigger.TriggerInstance> {

    private static final String PHYSICS_PREDICATE_ID = "physics";


    @NotNull
    @Override
    public Codec<TriggerInstance> codec() {
        return TriggerInstance.CODEC;
    }

    public void trigger(ServerPlayer player, PlayerPhysicsFrame frame) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(frame));
    }


    public record TriggerInstance(Optional<ContextAwarePredicate> player, Optional<PhysicsFramePredicate> physics) implements SimpleInstance {

        public static final Codec<PhysicsUpdateTrigger.TriggerInstance> CODEC = RecordCodecBuilder.create((instance) -> instance
                .group(
                        ExtraCodecs.strictOptionalField(EntityPredicate.ADVANCEMENT_CODEC, "player").forGetter(TriggerInstance::player),
                        ExtraCodecs.strictOptionalField(PhysicsFramePredicate.CODEC, PHYSICS_PREDICATE_ID).forGetter(TriggerInstance::physics)
                )
                .apply(instance, TriggerInstance::new));

        public boolean matches(PlayerPhysicsFrame frame) {
            return this.physics.isPresent() && this.physics.get().matches(frame);
        }

        public Optional<PhysicsFramePredicate> physics() {
            return this.physics;
        }
    }
}
