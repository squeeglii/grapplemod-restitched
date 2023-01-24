package com.yyon.grapplinghook.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.function.Supplier;

public class EnchantmentEntry<E extends Enchantment> {

    private final ResourceLocation id;
    private final Supplier<E> factory;

    private E enchantment;

    protected EnchantmentEntry(ResourceLocation id, Supplier<E> factory) {
        this.id = id;
        this.factory = factory;

        this.enchantment = null;
    }

    Supplier<E> getFactory() {
        return this.factory;
    }

    @SuppressWarnings("unchecked")
    void finalize(Enchantment enchantment) {
        if(this.enchantment != null) throw new IllegalStateException("Item is already registered!");

        try {
            this.enchantment = (E) enchantment;
        } catch (ClassCastException err) {
            throw new IllegalStateException("Item is already registered by a different mod!");
        }

    }


    public ResourceLocation getIdentifier() {
        return this.id;
    }

    public E getEnchantment() {
        return this.enchantment;
    }

    public boolean isRegistered() {
        return this.enchantment != null;
    }
}
