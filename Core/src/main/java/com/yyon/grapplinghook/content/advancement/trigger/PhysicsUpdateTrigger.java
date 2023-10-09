package com.yyon.grapplinghook.content.advancement.trigger;

import com.google.gson.JsonObject;
import com.yyon.grapplinghook.content.advancement.PhysicsFramePredicate;
import com.yyon.grapplinghook.physics.PlayerPhysicsFrame;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class PhysicsUpdateTrigger extends SimpleCriterionTrigger<PhysicsUpdateTrigger.TriggerInstance> {
    private static final String PHYSICS_PREDICATE = "physics";

    @NotNull
    @Override
    protected TriggerInstance createInstance(JsonObject jsonObject, Optional<ContextAwarePredicate> contextAwarePredicate, DeserializationContext deserializationContext) {
        PhysicsFramePredicate predicate = PhysicsFramePredicate.fromJson(jsonObject.get(PHYSICS_PREDICATE));
        return new TriggerInstance(contextAwarePredicate, predicate);
    }

    public void trigger(ServerPlayer player, PlayerPhysicsFrame frame) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(frame));
    }


    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final PhysicsFramePredicate physics;

        private TriggerInstance(Optional<ContextAwarePredicate> optContextAwarePred, PhysicsFramePredicate predicate) {
            super(optContextAwarePred);
            this.physics = predicate;
        }

        public boolean matches(PlayerPhysicsFrame frame) {
            return this.physics.matches(frame);
        }

        @NotNull
        @Override
        public JsonObject serializeToJson() {
            JsonObject base = super.serializeToJson();
            base.add(PHYSICS_PREDICATE, this.physics.toJson());
            return base;
        }
    }
}
