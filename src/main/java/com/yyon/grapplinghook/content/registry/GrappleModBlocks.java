package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.block.modifierblock.GrappleModifierBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class GrappleModBlocks {

    private static HashMap<ResourceLocation, BlockEntry<?>> blocks;

    static {
        GrappleModBlocks.blocks = new HashMap<>();
    }

    public static <B extends Block> Flow<B> block(String id, Supplier<B> block) {
        ResourceLocation qualId = GrappleMod.id(id);
        BlockEntry<B> entry = new BlockEntry<>(qualId, block);
        GrappleModBlocks.blocks.put(qualId, entry);
        return new Flow<>(entry);
    }


    public static void registerAllBlocks() {
        for(Map.Entry<ResourceLocation, BlockEntry<?>> def: blocks.entrySet()) {
            ResourceLocation id = def.getKey();
            BlockEntry<?> data = def.getValue();
            Block it = data.getFactory().get();

            data.finalize(Registry.register(BuiltInRegistries.BLOCK, id, it));
        }
    }

    public static HashMap<ResourceLocation, BlockEntry<?>> getBlocks() {
        return new HashMap<>(GrappleModBlocks.blocks);
    }

    public static final BlockEntry<GrappleModifierBlock> GRAPPLE_MODIFIER = GrappleModBlocks
            .block("block_grapple_modifier", GrappleModifierBlock::new)
            .withConfiguredItem(GrappleModItems.GRAPPLE_MODIFIER_BLOCK, new Item.Properties().stacksTo(64))
            .define();



    public static class Flow<B extends Block> {

        private final BlockEntry<B> context;

        public Flow(BlockEntry<B> context) {
            this.context = context;
        }

        public BlockEntry<B> define() {
            return this.context;
        }

        public Flow<B> withItem(Consumer<GrappleModItems.ItemEntry<BlockItem>>  destination) {
            return this.withConfiguredItem(destination, new Item.Properties());
        }

        public Flow<B> withConfiguredItem(Consumer<GrappleModItems.ItemEntry<BlockItem>> destination, Item.Properties properties) {
            return this.withCustomItem(destination, () -> new BlockItem(context.get(), properties));
        }

        public <I extends BlockItem> Flow<B> withCustomItem(Consumer<GrappleModItems.ItemEntry<I>> destination, Supplier<I> factory) {
            GrappleModItems.ItemEntry<I> item = GrappleModItems.item(context.getIdentifier().getPath(), factory);
            destination.accept(item);
            return this;
        }
    }

    public static class BlockEntry<B extends Block> extends AbstractRegistryReference<B> {
        protected BlockEntry(ResourceLocation id, Supplier<B> factory) {
            super(id, factory);
        }
    }


    public static class BlockItemEntry<I extends BlockItem> extends GrappleModItems.ItemEntry<I> implements Consumer<GrappleModItems.ItemEntry<I>> {
        private GrappleModItems.ItemEntry<I> source = null;

        protected BlockItemEntry() {
            super(null, () -> null, null);
        }

        @Override
        public void accept(GrappleModItems.ItemEntry<I> item) {
            if(this.source != null) throw new IllegalStateException("The original item source cannot be defined more that once.");
            this.source = item;
        }

        @Override
        public Supplier<I> getFactory() {
            return this.source.getFactory();
        }

        @Override
        public I get() {
            return this.source.get();
        }

        @Override
        protected void finalize(Object entry) {
            this.source.finalize(entry);
        }

        @Override
        public ResourceLocation getIdentifier() {
            return this.source.getIdentifier();
        }

        public GrappleModItems.ItemEntry<I> getSource() {
            return this.source;
        }
    }
}
