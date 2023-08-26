package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.advancement.trigger.PhysicsUpdateTrigger;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.Supplier;

public class GrappleModAdvancementTriggers {

    private static final LinkedHashSet<TriggerEntry<?>> advancementTriggers;

    static {
        advancementTriggers = new LinkedHashSet<>();
    }


    public static final TriggerEntry<PhysicsUpdateTrigger> PHYSICS_UPDATE_TRIGGER = trigger(PhysicsUpdateTrigger::new);


    public static <T extends CriterionTrigger<?>> TriggerEntry<T> trigger(Supplier<T> trigger) {
        TriggerEntry<T> entry = new TriggerEntry<>(trigger);
        advancementTriggers.add(entry);

        return entry;
    }


    public static void registerAllTriggers() {
        for(TriggerEntry<?> entry: advancementTriggers) {
            CriterionTrigger<?> it = entry.getFactory().get();
            entry.finalize(CriteriaTriggers.register(it));
        }
    }

    public static class TriggerEntry<T extends CriterionTrigger<?>> extends AbstractRegistryReference<T> {
        protected TriggerEntry(Supplier<T> factory) {
            super(null, factory);
        }

        @Override
        public ResourceLocation getIdentifier() {
            return this.get().getId();
        }
    }

}
