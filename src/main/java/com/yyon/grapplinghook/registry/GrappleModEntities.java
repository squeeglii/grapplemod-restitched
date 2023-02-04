package com.yyon.grapplinghook.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.entity.grapplehook.GrapplehookEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GrappleModEntities {

    private static HashMap<ResourceLocation, EntityEntry<?>> entities;

    static {
        GrappleModEntities.entities = new HashMap<>();
    }

    public static <E extends EntityType<?>> EntityEntry<E> entity(String id, Supplier<E> type) {
        ResourceLocation qualId = GrappleMod.id(id);
        EntityEntry<E> entry = new EntityEntry<>(qualId, type);
        GrappleModEntities.entities.put(qualId, entry);
        return entry;
    }


    public static void registerAllEntities() {
        for(Map.Entry<ResourceLocation, EntityEntry<?>> def: entities.entrySet()) {
            ResourceLocation id = def.getKey();
            EntityEntry<?> data = def.getValue();
            EntityType<?> it = data.getFactory().get();

            data.finalize(Registry.register(Registry.ENTITY_TYPE, id, it));
        }
    }

    public static final EntityEntry<EntityType<GrapplehookEntity>> GRAPPLE_HOOK = GrappleModEntities
            .entity("grapplehook", () -> FabricEntityTypeBuilder.<GrapplehookEntity>
                     create(MobCategory.MISC, GrapplehookEntity::new)
                    .dimensions(EntityDimensions.fixed(0.25f, 0.25f))
                    .build()
            );



    public static class EntityEntry<T extends EntityType<?>> extends AbstractRegistryReference<T> {

        protected EntityEntry(ResourceLocation id, Supplier<T> factory) {
            super(id, factory);
        }
    }

}


