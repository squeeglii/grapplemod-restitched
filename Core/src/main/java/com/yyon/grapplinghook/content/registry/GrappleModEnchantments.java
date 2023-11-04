package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.enchantment.DoubleJumpEnchantment;
import com.yyon.grapplinghook.content.enchantment.SlidingEnchantment;
import com.yyon.grapplinghook.content.enchantment.WallRunEnchantment;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class GrappleModEnchantments {

    private static final HashMap<ResourceLocation, EnchantmentEntry<?>> enchantments;

    static {
        enchantments = new HashMap<>();
    }

    public static <E extends Enchantment> EnchantmentEntry<E> enchantment(String id, Supplier<E> ench) {
        ResourceLocation qualId = GrappleMod.id(id);
        EnchantmentEntry<E> entry = new EnchantmentEntry<>(qualId, ench);
        enchantments.put(qualId, entry);
        return entry;
    }


    public static void registerAllEnchantments() {
        for(Map.Entry<ResourceLocation, EnchantmentEntry<?>> def: enchantments.entrySet()) {
            ResourceLocation id = def.getKey();
            EnchantmentEntry<?> data = def.getValue();
            Enchantment it = data.getFactory().get();

            data.finalize(Registry.register(BuiltInRegistries.ENCHANTMENT, id, it));
        }
    }

    public static final EnchantmentEntry<WallRunEnchantment> WALL_RUN = GrappleModEnchantments.enchantment("wall_running", WallRunEnchantment::new);
    public static final EnchantmentEntry<DoubleJumpEnchantment> DOUBLE_JUMP = GrappleModEnchantments.enchantment("double_jump", DoubleJumpEnchantment::new);
    public static final EnchantmentEntry<SlidingEnchantment> SLIDING = GrappleModEnchantments.enchantment("sliding", SlidingEnchantment::new);


    public static List<? extends Enchantment> getEnchantments() {
        return enchantments.values().stream()
                .map(EnchantmentEntry::get)
                .toList();
    }

    public static class EnchantmentEntry<E extends Enchantment> extends AbstractRegistryReference<E> {
        protected EnchantmentEntry(ResourceLocation id, Supplier<E> factory) {
            super(id, factory);
        }
    }
}
