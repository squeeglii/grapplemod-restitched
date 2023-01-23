package com.yyon.grapplinghook.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public class ItemEntry<I extends Item> {

    private final ResourceLocation id;
    private final Supplier<I> itemFactory;

    private I item;

    protected ItemEntry(ResourceLocation id, Supplier<I> factory) {
        this.id = id;
        this.itemFactory = factory;

        this.item = null;
    }

    Supplier<I> getItemConfiguration() {
        return this.itemFactory;
    }

    @SuppressWarnings("unchecked")
    void finalize(Item item) {
        if(this.item != null) throw new IllegalStateException("Item is already registered!");

        try {
            this.item = (I) item;
        } catch (ClassCastException err) {
            throw new IllegalStateException("Item is already registered by a different mod!");
        }

    }


    public ResourceLocation getIdentifier() {
        return this.id;
    }

    public I getItem() {
        return this.item;
    }

    public boolean isRegistered() {
        return this.item != null;
    }
}
