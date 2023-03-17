package com.yyon.grapplinghook.content.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public abstract class AbstractRegistryReference<T> {

    private final ResourceLocation id;
    private final Supplier<T> factory;

    private T entry;

    protected AbstractRegistryReference(ResourceLocation id, Supplier<T> factory) {
        this.id = id;
        this.factory = factory;

        this.entry = null;
    }

    Supplier<T> getFactory() {
        return this.factory;
    }

    @SuppressWarnings("unchecked")
    protected void finalize(Object entry) {
        if(this.entry != null) throw new IllegalStateException("Item is already registered!");

        try {
            this.entry = (T) entry;
        } catch (ClassCastException err) {
            throw new IllegalStateException("Item is already registered by a different mod!");
        }

    }


    public ResourceLocation getIdentifier() {
        return this.id;
    }

    public T get() {
        return this.entry;
    }

    public boolean isRegistered() {
        return this.entry != null;
    }
}
