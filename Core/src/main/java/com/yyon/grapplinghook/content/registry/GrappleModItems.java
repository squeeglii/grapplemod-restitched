package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.item.*;
import com.yyon.grapplinghook.content.item.upgrade.*;
import com.yyon.grapplinghook.customization.template.GrapplingHookTemplate;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public final class GrappleModItems {

    private static final ArrayList<ResourceLocation> itemsInRegistryOrder;
    private static final HashMap<ResourceLocation, ItemEntry<?>> items;

    private static List<ItemStack> creativeMenuCache;
    private static boolean creativeCacheInvalid;

    static {
        items = new HashMap<>();
        itemsInRegistryOrder = new ArrayList<>();
        creativeMenuCache = null;
    }

    public static final ItemEntry<GrapplehookItem> GRAPPLING_HOOK = GrappleModItems.item("grapplinghook", GrapplehookItem::new, ItemEntry.populateHookVariantsInTab());
    public static final ItemEntry<EnderStaffItem> ENDER_STAFF = GrappleModItems.item("launcheritem", EnderStaffItem::new);
    public static final ItemEntry<ForcefieldItem> FORCE_FIELD = GrappleModItems.item("repeller", ForcefieldItem::new);
    public static final ItemEntry<RocketItem> ROCKET = GrappleModItems.item("rocket", RocketItem::new);

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

    public static final ItemEntry<LongFallBootsItem> LONG_FALL_BOOTS = GrappleModItems.item("longfallboots", () -> new LongFallBootsItem(ArmorMaterials.DIAMOND));

    public static final ItemEntry<BlueprintItem> BLUEPRINT = GrappleModItems.item("blueprint", BlueprintItem::new);



    public static final GrappleModBlocks.BlockItemEntry<BlockItem> GRAPPLE_MODIFIER_BLOCK = reserve();
    public static final GrappleModBlocks.BlockItemEntry<BlockItem> TEMPLATE_TABLE_BLOCK = reserve();

    private static final CreativeModeTab.DisplayItemsGenerator MOD_TAB_GENERATOR = (displayParameters, output) -> {

        if(creativeMenuCache == null || creativeCacheInvalid) {
            GrappleModItems.creativeCacheInvalid = false;
            creativeMenuCache = itemsInRegistryOrder.stream()
                    .map(items::get)
                    .map(ItemEntry::getTabProvider)
                    .map(Supplier::get)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList());
        }

        creativeMenuCache.forEach(output::accept);
    };

    private static final ResourceKey<CreativeModeTab> ITEM_GROUP_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, GrappleMod.id("main"));

    private static final CreativeModeTab ITEM_GROUP = FabricItemGroup.builder()
            .title(Component.translatable("itemgroup.grapplemod.main"))
            .icon(() -> new ItemStack(GRAPPLING_HOOK.get()))
            .displayItems(MOD_TAB_GENERATOR)
            .build();

    public static <I extends Item> ItemEntry<I> item(String id, Supplier<I> item) {
        return item(id, item, null);
    }

    public static <I extends Item> ItemEntry<I> item(String id, Supplier<I> item, Supplier<List<ItemStack>> tabProvider) {
        ResourceLocation qualId = GrappleMod.id(id);
        ItemEntry<I> entry = new ItemEntry<>(qualId, item, tabProvider);

        if(GrappleModItems.items.containsKey(qualId))
            throw new IllegalStateException("Duplicate item registered");

        GrappleModItems.items.put(qualId, entry);
        GrappleModItems.itemsInRegistryOrder.add(qualId);
        return entry;
    }

    public static <B extends BlockItem> GrappleModBlocks.BlockItemEntry<B> reserve() {
        return new GrappleModBlocks.BlockItemEntry<>();
    }

    public static void invalidateCreativeTabCache() {
        GrappleModItems.creativeCacheInvalid = true;
    }

    public static boolean isCreativeCacheInvalid() {
        return GrappleModItems.creativeCacheInvalid;
    }

    public static void registerAllItems() {
        for(Map.Entry<ResourceLocation, ItemEntry<?>> def: items.entrySet()) {
            ResourceLocation id = def.getKey();
            ItemEntry<?> data = def.getValue();
            Item it = data.getFactory().get();

            data.finalize(Registry.register(BuiltInRegistries.ITEM, id, it));
        }

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, ITEM_GROUP_KEY, ITEM_GROUP);
    }



    public static class ItemEntry<I extends Item> extends AbstractRegistryReference<I> {

        protected Supplier<List<ItemStack>> tabProvider;

        protected ItemEntry(ResourceLocation id, Supplier<I> factory, Supplier<List<ItemStack>> creativeTabProvider) {
            super(id, factory);

            this.tabProvider = creativeTabProvider == null
                    ? this.defaultInTab()
                    : creativeTabProvider;
        }

        public Supplier<List<ItemStack>> getTabProvider() {
            return tabProvider;
        }

        private Supplier<List<ItemStack>> defaultInTab() {
            return () -> List.of(this.get().getDefaultInstance());
        }

        private static Supplier<List<ItemStack>> hiddenInTab() {
            return ArrayList::new;
        }

        private static Supplier<List<ItemStack>> populateHookVariantsInTab() {
            return () -> {
                ArrayList<ItemStack> grappleHookVariants = new ArrayList<>();
                grappleHookVariants.add(GrappleModItems.GRAPPLING_HOOK.get().getDefaultInstance());

                GrapplingHookTemplate.getTemplates().stream()
                        .filter(GrapplingHookTemplate::isEnabled)
                        .map(GrapplingHookTemplate::getAsStack)
                        .forEachOrdered(grappleHookVariants::add);

                return grappleHookVariants;
            };
        }
    }
}
