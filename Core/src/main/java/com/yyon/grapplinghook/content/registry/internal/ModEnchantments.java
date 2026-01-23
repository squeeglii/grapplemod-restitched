package com.yyon.grapplinghook.content.registry.internal;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class ModEnchantments {

    private static Set<EnchantmentEffectRegistration<?>> enchantmentEffects;
    private static Set<EnchantmentDefinitionRegistration> enchantmentDefinitions;

    private static Set<ResourceKey<Enchantment>> availableEnchantments = null;

    static {
        enchantmentEffects = new HashSet<>();
        enchantmentDefinitions = new HashSet<>();
    }

    public static final DataComponentType<Unit> EFFECT_WALL_RUNNING = registerEffect("wall_running", builder -> builder.persistent(Unit.CODEC));
    public static final DataComponentType<Unit> EFFECT_SLIDING = registerEffect("sliding", builder -> builder.persistent(Unit.CODEC));
    public static final DataComponentType<Unit> EFFECT_DOUBLE_JUMP = registerEffect("double_jump", builder -> builder.persistent(Unit.CODEC));


    // TODO: ACTUALLY IMPLEMENT THE ENCHANTMENT DEFINITIONS
    private static final ResourceKey<Enchantment> DEF_WALL_RUNNING = registerExpectedEnchantment("wall_running");
    private static final ResourceKey<Enchantment> DEF_SLIDING = registerExpectedEnchantment("sliding");
    private static final ResourceKey<Enchantment> DEF_DOUBLE_JUMP = registerExpectedEnchantment("double_jump");

    private static <T> DataComponentType<T> registerEffect(String id, UnaryOperator<DataComponentType.Builder<T>> operator) {
        ResourceLocation enchantment = GrappleMod.id(id);
        DataComponentType<T> dataComponentType = operator.apply(DataComponentType.builder()).build();
        EnchantmentEffectRegistration<T> reg = new EnchantmentEffectRegistration<>(enchantment, dataComponentType);

        enchantmentEffects.add(reg);
        return dataComponentType;
    }

    private static ResourceKey<Enchantment> registerExpectedEnchantment(String id) {
        ResourceLocation enchId = GrappleMod.id(id);
        ResourceKey<Enchantment> enchKey = ResourceKey.create(Registries.ENCHANTMENT, enchId);
        EnchantmentDefinitionRegistration def = new EnchantmentDefinitionRegistration(enchKey);

        enchantmentDefinitions.add(def);

        return def.key();
    }

    public static void registerImmutable() {
        for(EnchantmentEffectRegistration<?> effect: enchantmentEffects) {
            DataComponentType<?> component = effect.operator();
            Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, effect.id(), component);
        }
    }

    // Enchantments can change per-world. I think they're registered seperately? See VanillaRegistries?
    public static void registerRuntime() {
        //todo: pre-gen these in code.
        //for(EnchantmentDefinitionRegistration def: enchantmentDefinitions)
        //    Registry.registerForHolder(Registries.ENCHANTMENT, def.enchantment);

        //TODO: check these on world load.
        availableEnchantments = Set.of(DEF_WALL_RUNNING, DEF_DOUBLE_JUMP, DEF_SLIDING);
    }

    @Deprecated
    public static ResourceKey<Enchantment> wallRunning() {
        return DEF_WALL_RUNNING;
    }

    @Deprecated
    public static ResourceKey<Enchantment> sliding() {
        return DEF_SLIDING;
    }

    @Deprecated
    public static ResourceKey<Enchantment> doubleJump() {
        return DEF_DOUBLE_JUMP;
    }

    /**
     * These enchantments are ones the mod includes by default, but are not
     * guaranteed to be included in all instances. You should check if they're
     * registered rather than blindly including them.
     *
     * See GrappleModItems & the creative menu as an example.
     */
    public static Set<ResourceKey<Enchantment>> getRecommendedEnchantments() {
        return availableEnchantments;
    }

    public record EnchantmentEffectRegistration<T>(ResourceLocation id, DataComponentType<T> operator) {

    }

    public record EnchantmentDefinitionRegistration(ResourceKey<Enchantment> enchantment) {

        public ResourceKey<Enchantment> key() {
            return this.enchantment;
        }

    }
}
