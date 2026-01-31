package com.yyon.grapplinghook.content.registry.internal;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.item.*;
import com.yyon.grapplinghook.content.item.smithing.LongFallBootsTemplateItem;
import com.yyon.grapplinghook.content.item.upgrade.*;
import com.yyon.grapplinghook.content.registry.helper.AbstractRegistryReference;
import com.yyon.grapplinghook.content.registry.helper.TabBuilder;
import com.yyon.grapplinghook.content.customization.HookTemplates;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public final class ModItems {

    private static final ArrayList<ResourceLocation> itemsInRegistryOrder;
    private static final HashMap<ResourceLocation, ItemEntry<?>> items;

    static {
        items = new HashMap<>();
        itemsInRegistryOrder = new ArrayList<>();
    }

    public static final ItemEntry<GrapplehookItem> GRAPPLING_HOOK = ModItems.item("grappling_hook", GrapplehookItem::new, ItemEntry.populateHookVariantsInTab());
    public static final ItemEntry<EnderStaffItem> ENDER_STAFF = ModItems.item("ender_staff", EnderStaffItem::new);
    public static final ItemEntry<ForcefieldItem> FORCE_FIELD = ModItems.item("forcefield", ForcefieldItem::new);
    public static final ItemEntry<RocketItem> ROCKET = ModItems.item("rocket", RocketItem::new);

    public static final ItemEntry<BaseUpgradeItem> BASE_UPGRADE = ModItems.item("base_upgrade", BaseUpgradeItem::new);
    public static final ItemEntry<DoubleUpgradeItem> DOUBLE_UPGRADE = ModItems.item("double_hook_upgrade", DoubleUpgradeItem::new);
    public static final ItemEntry<ForcefieldUpgradeItem> FORCE_FIELD_UPGRADE = ModItems.item("forcefield_upgrade", ForcefieldUpgradeItem::new);
    public static final ItemEntry<MagnetUpgradeItem> MAGNET_UPGRADE = ModItems.item("magnet_upgrade", MagnetUpgradeItem::new);
    public static final ItemEntry<MotorUpgradeItem> MOTOR_UPGRADE = ModItems.item("motor_upgrade", MotorUpgradeItem::new);
    public static final ItemEntry<RopeUpgradeItem> ROPE_UPGRADE = ModItems.item("rope_upgrade", RopeUpgradeItem::new);
    public static final ItemEntry<StaffUpgradeItem> ENDER_STAFF_UPGRADE = ModItems.item("ender_staff_upgrade", StaffUpgradeItem::new);
    public static final ItemEntry<SwingUpgradeItem> SWING_UPGRADE = ModItems.item("swing_upgrade", SwingUpgradeItem::new);
    public static final ItemEntry<ThrowUpgradeItem> HOOK_THROWER_UPGRADE = ModItems.item("hook_thrower_upgrade", ThrowUpgradeItem::new);
    public static final ItemEntry<LimitsUpgradeItem> LIMITS_UPGRADE = ModItems.item("limits_upgrade", LimitsUpgradeItem::new);
    public static final ItemEntry<RocketUpgradeItem> ROCKET_UPGRADE = ModItems.item("rocket_upgrade", RocketUpgradeItem::new);
    public static final ItemEntry<DyeBagUpgrade> DYE_BAG_UPGRADE = ModItems.item("dye_bag_upgrade", DyeBagUpgrade::new);

    public static final ItemEntry<BlueprintItem> BLUEPRINT = ModItems.item("blueprint", BlueprintItem::new);

    public static final ItemEntry<LongFallBootsItem> LONG_FALL_BOOTS = ModItems.item("long_fall_boots", LongFallBootsItem::new, ItemEntry.populateBootVariants());
    public static final ItemEntry<LongFallBootsTemplateItem> LONG_FALL_BOOTS_SMITHING_TEMPLATE = ModItems.item("long_fall_boots_smithing_template", LongFallBootsTemplateItem::new);

    public static final ModBlocks.BlockItemEntry<BlockItem> GRAPPLE_MODIFIER_BLOCK = reserve();
    public static final ModBlocks.BlockItemEntry<BlockItem> BLUEPRINT_SHELF_BLOCK = reserve();

    private static final CreativeModeTab.DisplayItemsGenerator MOD_TAB_GENERATOR = (displayParameters, output) -> {

        List<ItemStack> creativeMenu = itemsInRegistryOrder.stream()
                .map(items::get)
                .map(ItemEntry::getTabProvider)
                .map(provider -> provider.build(displayParameters))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        GrappleMod.LOGGER.info("All enchs: {}", Arrays.toString(displayParameters.holders().lookupOrThrow(Registries.ENCHANTMENT).listElementIds().map(ResourceKey::location).toArray()));

        // Add enchanted books to end of creative menu.
        ModEnchantments.getRecommendedEnchantments().stream()
                .map(enchantment -> tryGetEnchantment(displayParameters, enchantment))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(enchantment -> new EnchantmentInstance(enchantment, 1))
                .map(EnchantedBookItem::createForEnchantment)
                .forEach(creativeMenu::add);

        output.acceptAll(creativeMenu);
    };

    private static final ResourceKey<CreativeModeTab> ITEM_GROUP_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, GrappleMod.id("main"));

    private static final CreativeModeTab ITEM_GROUP = FabricItemGroup.builder()
            .title(Component.translatable("itemGroup.grapplemod.main"))
            .icon(() -> new ItemStack(GRAPPLING_HOOK.get()))
            .displayItems(MOD_TAB_GENERATOR)
            .build();

    public static <I extends Item> ItemEntry<I> item(String id, Supplier<I> item) {
        return item(id, item, null);
    }

    public static <I extends Item> ItemEntry<I> item(String id, Supplier<I> item, TabBuilder tabProvider) {
        return item(id, item, tabProvider, false);
    }

    public static <I extends Item> ItemEntry<I> item(String id, Supplier<I> item, TabBuilder tabProvider, boolean placeFirstInCreative) {
        ResourceLocation qualId = GrappleMod.id(id);
        ItemEntry<I> entry = new ItemEntry<>(qualId, item, tabProvider);

        if(ModItems.items.containsKey(qualId))
            throw new IllegalStateException("Duplicate item registered");

        ModItems.items.put(qualId, entry);

        if(placeFirstInCreative) {
            ModItems.itemsInRegistryOrder.add(0, qualId);
        } else {
            ModItems.itemsInRegistryOrder.add(qualId);
        }

        return entry;
    }

    public static <B extends BlockItem> ModBlocks.BlockItemEntry<B> reserve() {
        return new ModBlocks.BlockItemEntry<>();
    }

    // Enchantment registry could change at any point -
    // I cba to check that perfectly.
    @Deprecated(since = "1.21.1")
    public static void invalidateCreativeTabCache() {

    }

    @Deprecated(since = "1.21.1")
    public static boolean isCreativeCacheInvalid() {
        return false;
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

        protected TabBuilder tabProvider;

        protected ItemEntry(ResourceLocation id, Supplier<I> factory, TabBuilder creativeTabProvider) {
            super(id, factory);

            this.tabProvider = creativeTabProvider == null
                    ? this.defaultInTab()
                    : creativeTabProvider;
        }

        public TabBuilder getTabProvider() {
            return this.tabProvider;
        }

        private TabBuilder defaultInTab() {
            return displayParams -> List.of(this.get().getDefaultInstance());
        }

        private static TabBuilder hiddenInTab() {
            return displayParams -> new ArrayList<>();
        }

        private static TabBuilder populateBootVariants() {
            return displayParams -> {
                //todo: this may vary depending on datapacks.
                LinkedList<ItemStack> variants = new LinkedList<>();

                // Always include plain. The feather falling is for aesthetic value anyway.
                ItemStack plainItem = LONG_FALL_BOOTS.get().getDefaultInstance();
                tryApplyEnchantment(displayParams, plainItem, Enchantments.FEATHER_FALLING, 4);
                variants.add(plainItem);

                ItemStack doubleJumpItem = LONG_FALL_BOOTS.get().getDefaultInstance();
                boolean appliedAllDJ =
                    tryApplyEnchantment(displayParams, doubleJumpItem, Enchantments.FEATHER_FALLING, 4) &&
                    tryApplyEnchantment(displayParams, doubleJumpItem, ModEnchantments.doubleJump(), 1);
                if(appliedAllDJ)
                    variants.add(doubleJumpItem);

                ItemStack allEnchantsItem = LONG_FALL_BOOTS.get().getDefaultInstance();
                boolean appliedAllFull =
                    tryApplyEnchantment(displayParams, allEnchantsItem, Enchantments.FEATHER_FALLING, 4) &&
                    tryApplyEnchantment(displayParams, allEnchantsItem, ModEnchantments.doubleJump(), 1) &&
                    tryApplyEnchantment(displayParams, allEnchantsItem, ModEnchantments.sliding(), 1) &&
                    tryApplyEnchantment(displayParams, allEnchantsItem, ModEnchantments.wallRunning(), 1);
                if(appliedAllFull)
                    variants.add(allEnchantsItem);

                return variants;
            };
        }

        private static TabBuilder populateHookVariantsInTab() {
            return displayParams -> {
                ArrayList<ItemStack> grappleHookVariants = new ArrayList<>();
                grappleHookVariants.add(ModItems.GRAPPLING_HOOK.get().getDefaultInstance());

                HookTemplates.getTemplates().stream()
                        .filter(HookTemplates.Template::isEnabled)
                        .map(HookTemplates.Template::getAsStack)
                        .forEachOrdered(grappleHookVariants::add);

                return grappleHookVariants;
            };
        }
    }

    private static boolean tryApplyEnchantment(CreativeModeTab.ItemDisplayParameters tabParams, ItemStack item, ResourceKey<Enchantment> enchantment, int level) {
        Optional<Holder.Reference<Enchantment>> optRegEnch = tabParams.holders()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(enchantment);

        if(optRegEnch.isPresent()) {
            item.enchant(optRegEnch.get(), level);
            return true;
        }

        return false;
    }

    private static Optional<Holder.Reference<Enchantment>> tryGetEnchantment(CreativeModeTab.ItemDisplayParameters tabParams, ResourceKey<Enchantment> enchantment) {
        GrappleMod.LOGGER.info("ench: {}", enchantment.location());

        return tabParams.holders()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(enchantment);
    }
}
