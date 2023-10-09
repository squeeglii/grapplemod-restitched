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

    public Supplier<T> getFactory() {
        return this.factory;
    }

    @SuppressWarnings("unchecked")
    protected void finalize(Object entry) {
        if(entry == null) throw new IllegalStateException("Entry cannot be null!");
        if(this.isRegistered()) throw new IllegalStateException("Entry is already registered!");

        try {
            this.entry = (T) entry;
        } catch (ClassCastException err) {
            throw new IllegalStateException("Entry is already registered by a different mod!");
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
