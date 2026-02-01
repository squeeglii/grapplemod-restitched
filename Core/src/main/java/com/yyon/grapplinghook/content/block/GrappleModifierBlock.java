package com.yyon.grapplinghook.content.block;

import com.mojang.serialization.MapCodec;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.config.ServerFeatures;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.content.item.type.IAuthorable;
import com.yyon.grapplinghook.content.item.type.ICustomizationApplicable;
import com.yyon.grapplinghook.content.item.upgrade.BaseUpgradeItem;
import com.yyon.grapplinghook.content.registry.internal.ModItems;
import com.yyon.grapplinghook.content.customization.CustomizationCategory;
import com.yyon.grapplinghook.content.customization.data.HookCustomization;
import com.yyon.grapplinghook.util.Vec;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GrappleModifierBlock extends BaseEntityBlock {

	public static final MapCodec<GrappleModifierBlock> CODEC = simpleCodec(GrappleModifierBlock::new);

	public GrappleModifierBlock(Properties properties) {
		super(properties);
	}

	public GrappleModifierBlock() {
		this(Block.Properties.ofFullCopy(Blocks.FLETCHING_TABLE));
	}

	@NotNull
	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GrappleModifierBlockEntity(pos,state);
	}

	@NotNull
	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> drops = new ArrayList<>();
		drops.add(new ItemStack(this.asItem()));

		BlockEntity ent = builder.getParameter(LootContextParams.BLOCK_ENTITY);

		if (!(ent instanceof GrappleModifierBlockEntity tile)) return drops;

		tile.getUnlockedCategories().stream()
				.map(CustomizationCategory::getUpgradeItem)
				.map(ItemStack::new)
				.forEach(drops::add);

		return drops;
	}

	@Override
	@NotNull
	public RenderShape getRenderShape(BlockState pState) {
		return RenderShape.MODEL;
	}



	@Override
	@NotNull
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		ItemStack heldStack = player.getItemInHand(hand);
		Item heldItem = heldStack.getItem();

		if (heldItem instanceof BaseUpgradeItem upgradeItem)
			return this.handleUpgradeItem(level, pos, player, hand, upgradeItem);

		if (heldItem instanceof ICustomizationApplicable customItem)
			return this.handleApplyCustomizations(customItem, level, pos, player, heldStack);

		if (heldItem == Items.DIAMOND_BOOTS) {

			if (level.isClientSide)
				return ItemInteractionResult.SUCCESS;

			if (ServerFeatures.get().isBlockingOldLongFallBootsRecipe()) {
				Component msg = Component.translatable("feedback.grapplemod.modifier.long_fall_boots.disabled")
						.withStyle(ChatFormatting.RED);

				player.sendSystemMessage(msg);
				level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 1f, 0.3f);

				return ItemInteractionResult.CONSUME;
			}

			return this.handleDiamondBoots(level, pos, player, hand, heldStack);
		}

		if (heldItem == Items.DIAMOND)
			return this.handleEasterEgg(level, pos, player);

		return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
	}

	@Override
	@NotNull
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		if (!level.isClientSide)
			return InteractionResult.CONSUME;

		BlockEntity ent = level.getBlockEntity(pos);

		if (!(ent instanceof GrappleModifierBlockEntity tile))
			return InteractionResult.FAIL;

		GrappleModClient.get().openModifierScreen(tile);

		return InteractionResult.SUCCESS;
	}

	/**
	 * @deprecated Will be removed in 1.22. Replaced by the smithing table and the LongFallBootsTemplateItem.
	 */
	@Deprecated
	private ItemInteractionResult handleDiamondBoots(Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, ItemStack heldStack) {
		if (worldIn.isClientSide)
			return ItemInteractionResult.SUCCESS;

		ItemEnchantments enchantments = heldStack.getEnchantments();

		// disgusting. Replace this ASAP.
		Optional< Holder.Reference<Enchantment>> enchLookup = VanillaRegistries.createLookup()
				.lookup(Registries.ENCHANTMENT)
				.orElseThrow()
				.get(Enchantments.FEATHER_FALLING);

		if(enchLookup.isEmpty())
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		boolean invalidForReplacement = enchantments.getLevel(enchLookup.get()) < 4;

		if (invalidForReplacement) {
			Component msg = Component.translatable("grappletooltip.longfallbootsrecipe.desc")
					.withStyle(ChatFormatting.RED);

			playerIn.sendSystemMessage(msg);
			worldIn.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 1f, 0.3f);

			return ItemInteractionResult.CONSUME;
		}

		Component msg = Component.translatable("feedback.grapplemod.modifier.long_fall_boots");
		ItemStack replacementStack = new ItemStack(ModItems.LONG_FALL_BOOTS.get());
		EnchantmentHelper.setEnchantments(replacementStack, enchantments); // apply old enchantments to new stack

		playerIn.setItemInHand(hand, replacementStack);
		playerIn.sendSystemMessage(msg);
		worldIn.playSound(null, pos, SoundEvents.VILLAGER_WORK_TOOLSMITH, SoundSource.BLOCKS, 1f, 1.0f);

		return ItemInteractionResult.CONSUME;
	}

	private ItemInteractionResult handleApplyCustomizations(ICustomizationApplicable item, Level worldIn, BlockPos pos, Player playerIn, ItemStack heldStack) {
		if (worldIn.isClientSide)
			return ItemInteractionResult.SUCCESS;

		BlockEntity ent = worldIn.getBlockEntity(pos);

		if (!(ent instanceof GrappleModifierBlockEntity blockEntity))
			return ItemInteractionResult.FAIL;

		HookCustomization custom = blockEntity.getCurrentCustomizations();

		ItemStack newStack = heldStack.split(1);

		if(item instanceof IAuthorable authorable)
			authorable.commit(newStack, null, playerIn);

		item.applyCustomizations(newStack, custom);

		if(!playerIn.addItem(newStack))
			playerIn.drop(newStack, true);

		Component msg = item.getOverwriteMessage();

		playerIn.sendSystemMessage(msg);
		worldIn.playSound(null, pos, item.getOverwriteSoundEffect(), SoundSource.BLOCKS, 1f, 1.0f);

		return ItemInteractionResult.CONSUME;
	}

	private ItemInteractionResult handleUpgradeItem(Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BaseUpgradeItem upgradeItem) {
		if (worldIn.isClientSide)
			return ItemInteractionResult.SUCCESS;

		BlockEntity ent = worldIn.getBlockEntity(pos);

		if (!(ent instanceof GrappleModifierBlockEntity tile))
			return ItemInteractionResult.FAIL;

		CustomizationCategory category = upgradeItem.getCategory();

		if (category == null)
			return ItemInteractionResult.CONSUME;

		if (tile.isUnlocked(category)) {
			Component msg = Component.translatable("feedback.grapplemod.modifier.upgrade_already_applied")
									 .withStyle(ChatFormatting.RED);
			playerIn.sendSystemMessage(msg);
			worldIn.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 1f, 0.3f);

			return ItemInteractionResult.CONSUME;
		}

		if (!playerIn.isCreative())
			playerIn.setItemInHand(hand, ItemStack.EMPTY);

		Component msg = Component.translatable("feedback.grapplemod.modifier.applied_new_upgrade")
				.append(" ")
				.append(category.getEmbed());

		tile.unlockCategory(category);
		playerIn.sendSystemMessage(msg);
		worldIn.playSound(null, pos, SoundEvents.ARMOR_EQUIP_CHAIN.value(), SoundSource.BLOCKS, 1f, 1.0f);

		return ItemInteractionResult.CONSUME;
	}

	private ItemInteractionResult handleEasterEgg(Level worldIn, BlockPos pos, Player playerIn) {
		int spacing = 3;
		Vec[] positions = new Vec[] {new Vec(-spacing*2, 0, 0), new Vec(-spacing, 0, 0), new Vec(0, 0, 0), new Vec(spacing, 0, 0), new Vec(2*spacing, 0, 0)};
		int[] colors = new int[] {0x5bcffa, 0xf5abb9, 0xffffff, 0xf5abb9, 0x5bcffa};
		
		for (int i = 0; i < positions.length; i++) {
			Vec newpos = new Vec(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			Vec toPlayer = Vec.positionVec(playerIn).sub(newpos);
			double angle = toPlayer.length() == 0 ? 0 : toPlayer.getYaw();
			newpos = newpos.add(positions[i].rotateYaw(Math.toRadians(angle)));

			FireworkExplosion fExplode = new FireworkExplosion(
					FireworkExplosion.Shape.SMALL_BALL,
					IntList.of(colors[i]),    // colours
					IntList.of(),    // fade colours
					true,  // trail
					false  // flicker
			);
			Fireworks fireworkEffect = new Fireworks(15, List.of(fExplode));

	        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
	        stack.set(DataComponents.FIREWORKS, fireworkEffect);

			FireworkRocketEntity firework = new FireworkRocketEntity(worldIn, playerIn, newpos.x, newpos.y, newpos.z, stack);
			CompoundTag fireworkSave = new CompoundTag();
			firework.addAdditionalSaveData(fireworkSave);
			fireworkSave.putInt("LifeTime", 15);           // todo: 1.21.1 -- idk if this is still necessary
			firework.readAdditionalSaveData(fireworkSave);
			worldIn.addFreshEntity(firework);
		}

		return ItemInteractionResult.sidedSuccess(worldIn.isClientSide);
	}


}
