package com.yyon.grapplinghook.content.registry.internal;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.registry.helper.HoldingRegistryReference;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

// yes, the correct british "armour".
public class ArmourMaterials {

    private static final LinkedHashSet<MaterialEntry<?>> armourMaterials;

    static {
        armourMaterials = new LinkedHashSet<>();
    }


    public static final MaterialEntry<ArmorMaterial> LONG_FALL_BOOTS = material("long_fall_boot_ish", (id) -> {
        GrappleMod.LOGGER.info("Defining armour material: {}", id);

        Supplier<Ingredient> repairIngredient = () -> Ingredient.of(new ItemStack(Items.QUARTZ, 1));
        List<ArmorMaterial.Layer> armourLayers = List.of(new ArmorMaterial.Layer(id));

        return new ArmorMaterial(
                Map.of(
                        ArmorItem.Type.HELMET, 0,
                        ArmorItem.Type.CHESTPLATE, 0,
                        ArmorItem.Type.LEGGINGS,  0,
                        ArmorItem.Type.BOOTS, 3,
                        ArmorItem.Type.BODY, 11
                ),
                10, // int enchantmentValue,
                SoundEvents.ARMOR_EQUIP_CHAIN, repairIngredient, armourLayers,
                2.0f, 0 // float toughness, float knockbackResistance
        );
    });


    public static <T extends ArmorMaterial> MaterialEntry<T> material(String name, Function<ResourceLocation, T> material) {
        ResourceLocation id = GrappleMod.id(name);
        MaterialEntry<T> entry = new MaterialEntry<>(id, () -> material.apply(id));
        armourMaterials.add(entry);

        return entry;
    }

    public static void registerAllMaterials() {
        for(MaterialEntry<?> entry: armourMaterials)
            entry.register();
    }

    // this registration is just not fit for purpose anymore.
    // make something that places nicer with holders.
    public static class MaterialEntry<T extends ArmorMaterial> extends HoldingRegistryReference<ArmorMaterial, T> {

        protected MaterialEntry(ResourceLocation id, Supplier<T> factory) {
            super(id, factory, BuiltInRegistries.ARMOR_MATERIAL);
        }

    }

}
