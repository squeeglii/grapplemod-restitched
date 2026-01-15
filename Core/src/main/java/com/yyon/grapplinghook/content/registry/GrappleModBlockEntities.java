package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.blockentity.BlueprintShelfBlockEntity;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.content.registry.helper.AbstractRegistryReference;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class GrappleModBlockEntities {

    private static final HashMap<ResourceLocation, BlockEntityEntry<?>> blockEntities;

    static {
        blockEntities = new HashMap<>();
    }

    public static final BlockEntityEntry<BlockEntityType<GrappleModifierBlockEntity>> GRAPPLE_MODIFIER = GrappleModBlockEntities
            .blockEntity("modification_table",() -> BlockEntityType.Builder
                    .of(GrappleModifierBlockEntity::new, GrappleModBlocks.GRAPPLE_MODIFIER.get())
                    .build(null));

    public static final BlockEntityEntry<BlockEntityType<BlueprintShelfBlockEntity>> BLUEPRINT_SHELF = GrappleModBlockEntities
            .blockEntity("blueprint_shelf",() -> BlockEntityType.Builder
                    .of(BlueprintShelfBlockEntity::new, GrappleModBlocks.BLUEPRINT_SHELF.get())
                    .build(null));



    public static <E extends BlockEntityType<?>> BlockEntityEntry<E> blockEntity(String id, Supplier<E> type) {
        ResourceLocation qualId = GrappleMod.id(id);
        BlockEntityEntry<E> entry = new BlockEntityEntry<>(qualId, type);
        blockEntities.put(qualId, entry);
        return entry;
    }


    public static void registerAllBlockEntities() {
        for(Map.Entry<ResourceLocation, BlockEntityEntry<?>> def: blockEntities.entrySet()) {
            ResourceLocation id = def.getKey();
            BlockEntityEntry<?> data = def.getValue();
            BlockEntityType<?> it = data.getFactory().get();

            data.finalize(Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, it));
        }
    }


    public static class BlockEntityEntry<T extends BlockEntityType<?>> extends AbstractRegistryReference<T> {

        protected BlockEntityEntry(ResourceLocation id, Supplier<T> factory) {
            super(id, factory);
        }
    }

}


