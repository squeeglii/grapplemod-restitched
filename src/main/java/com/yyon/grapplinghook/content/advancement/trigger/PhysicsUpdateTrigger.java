package com.yyon.grapplinghook.content.advancement.trigger;

import com.google.gson.JsonObject;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.advancement.PhysicsFramePredicate;
import com.yyon.grapplinghook.physics.PlayerPhysicsFrame;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class PhysicsUpdateTrigger extends SimpleCriterionTrigger<PhysicsUpdateTrigger.TriggerInstance> {

    private static final ResourceLocation ID = GrappleMod.id("grapple_physics_changed");
    private static final String PHYSICS_PREDICATE = "physics";

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @NotNull
    @Override
    protected TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
        PhysicsFramePredicate predicate = PhysicsFramePredicate.fromJson(jsonObject.get(PHYSICS_PREDICATE));
        return new TriggerInstance(contextAwarePredicate, predicate);
    }

    public void trigger(ServerPlayer player, PlayerPhysicsFrame frame) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(frame));
    }


    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        private final PhysicsFramePredicate physics;

        private TriggerInstance(ContextAwarePredicate contextAwarePredicate, PhysicsFramePredicate predicate) {
            super(ID, contextAwarePredicate);
            this.physics = predicate;
        }

        public boolean matches(PlayerPhysicsFrame frame) {
            return this.physics.matches(frame);
        }

        @NotNull
        @Override
        public JsonObject serializeToJson(SerializationContext context) {
            JsonObject base = super.serializeToJson(context);
            base.add(PHYSICS_PREDICATE, this.physics.toJson());
            return base;
        }
    }
}
