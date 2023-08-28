package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.content.blockentity.TemplateTableBlockEntity;
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
            .blockEntity("block_grapple_modifier",() -> BlockEntityType.Builder
                    .of(GrappleModifierBlockEntity::new, GrappleModBlocks.GRAPPLE_MODIFIER.get())
                    .build(null));

    public static final BlockEntityEntry<BlockEntityType<TemplateTableBlockEntity>> TEMPLATE_TABLE = GrappleModBlockEntities
            .blockEntity("template_table",() -> BlockEntityType.Builder
                    .of(TemplateTableBlockEntity::new, GrappleModBlocks.TEMPLATE_TABLE.get())
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


