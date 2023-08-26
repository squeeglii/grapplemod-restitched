package com.yyon.grapplinghook.content.advancement.trigger;

import com.google.gson.JsonObject;
import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.physics.PlayerPhysicsFrame;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.DeserializationContext;
import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public class PhysicsUpdateTrigger extends SimpleCriterionTrigger<PhysicsUpdateTrigger.TriggerInstance> {

    private static final ResourceLocation ID = GrappleMod.id("grapple_physics_changed");

    @NotNull
    @Override
    public ResourceLocation getId() {
        return ID;
    }


    @NotNull
    @Override
    protected TriggerInstance createInstance(JsonObject jsonObject, ContextAwarePredicate contextAwarePredicate, DeserializationContext deserializationContext) {
        return new TriggerInstance(contextAwarePredicate);
    }


    public void trigger(ServerPlayer player, PlayerPhysicsFrame frame) {
        this.trigger(player, triggerInstance -> triggerInstance.matches(frame));
    }

    public static class TriggerInstance extends AbstractCriterionTriggerInstance {

        private TriggerInstance(ContextAwarePredicate contextAwarePredicate) {
            super(ID, contextAwarePredicate);
        }

        public boolean matches(PlayerPhysicsFrame frame) {
            return true; //TODO: salkjdjlksjdlka
        }
    }
}
