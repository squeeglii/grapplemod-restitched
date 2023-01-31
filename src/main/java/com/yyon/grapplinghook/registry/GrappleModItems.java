package com.yyon.grapplinghook.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.item.EnderStaffItem;
import com.yyon.grapplinghook.item.ForcefieldItem;
import com.yyon.grapplinghook.item.GrapplehookItem;
import com.yyon.grapplinghook.item.LongFallBoots;
import com.yyon.grapplinghook.item.upgrade.*;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class GrappleModItems {

    private static HashMap<ResourceLocation, ItemEntry<?>> items;

    static {
        GrappleModItems.items = new HashMap<>();
    }

    public static final ItemEntry<GrapplehookItem> GRAPPLING_HOOK = GrappleModItems.item("grapplinghook", GrapplehookItem::new);
    public static final ItemEntry<EnderStaffItem> ENDER_STAFF = GrappleModItems.item("launcheritem", EnderStaffItem::new);
    public static final ItemEntry<ForcefieldItem> FORCE_FIELD = GrappleModItems.item("repeller", ForcefieldItem::new);

    public static final ItemEntry<BaseUpgradeItem> BASE_UPGRADE = GrappleModItems.item("baseupgradeitem", BaseUpgradeItem::new);
    public static final ItemEntry<DoubleUpgradeItem> DOUBLE_UPGRADE = GrappleModItems.item("doubleupgradeitem", DoubleUpgradeItem::new);
    public static final ItemEntry<ForcefieldUpgradeItem> FORCE_FIELD_UPGRADE = GrappleModItems.item("forcefieldupgradeitem", ForcefieldUpgradeItem::new);
    public static final ItemEntry<MagnetUpgradeItem> MAGNET_UPGRADE = GrappleModItems.item("magnetupgradeitem", MagnetUpgradeItem::new);
    public static final ItemEntry<MotorUpgradeItem> MOTOR_UPGRADE = GrappleModItems.item("motorupgradeitem", MotorUpgradeItem::new);
    public static final ItemEntry<RopeUpgradeItem> ROPE_UPGRADE = GrappleModItems.item("ropeupgradeitem", RopeUpgradeItem::new);
    public static final ItemEntry<StaffUpgradeItem> ENDER_STAFF_UPGRADE = GrappleModItems.item("staffupgradeitem", StaffUpgradeItem::new);
    public static final ItemEntry<SwingUpgradeItem> SWING_UPGRADE = GrappleModItems.item("swingupgradeitem", SwingUpgradeItem::new);
    public static final ItemEntry<ThrowUpgradeItem> THROW_UPGRADE = GrappleModItems.item("throwupgradeitem", ThrowUpgradeItem::new);
    public static final ItemEntry<LimitsUpgradeItem> LIMITS_UPGRADE = GrappleModItems.item("limitsupgradeitem", LimitsUpgradeItem::new);
    public static final ItemEntry<RocketUpgradeItem> ROCKET_UPGRADE = GrappleModItems.item("rocketupgradeitem", RocketUpgradeItem::new);

    public static final ItemEntry<LongFallBoots> LONG_FALL_BOOTS = GrappleModItems.item("longfallboots", () -> new LongFallBoots(ArmorMaterials.DIAMOND, 3));


    public static final GrappleModBlocks.BlockItemEntry<BlockItem> GRAPPLE_MODIFIER_BLOCK = reserve();


    public static <I extends Item> ItemEntry<I> item(String id, Supplier<I> item) {
        ResourceLocation qualId = GrappleMod.id(id);
        ItemEntry<I> entry = new ItemEntry<>(qualId, item);
        GrappleModItems.items.put(qualId, entry);
        return entry;
    }

    public static <B extends BlockItem> GrappleModBlocks.BlockItemEntry<B> reserve() {
        return new GrappleModBlocks.BlockItemEntry<>();
    }


    public static void registerAllItems() {
        for(Map.Entry<ResourceLocation, ItemEntry<?>> def: items.entrySet()) {
            ResourceLocation id = def.getKey();
            ItemEntry<?> data = def.getValue();
            Item it = data.getFactory().get();

            data.finalize(Registry.register(Registry.ITEM, id, it));
        }
    }



    public static class ItemEntry<I extends Item> extends AbstractRegistryReference<I> {
        protected ItemEntry(ResourceLocation id, Supplier<I> factory) {
            super(id, factory);
        }
    }
}
