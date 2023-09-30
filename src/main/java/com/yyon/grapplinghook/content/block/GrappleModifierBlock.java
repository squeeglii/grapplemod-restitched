package com.yyon.grapplinghook.content.block;

import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.content.item.type.IAuthorable;
import com.yyon.grapplinghook.content.item.type.ICustomizationApplicable;
import com.yyon.grapplinghook.content.item.upgrade.BaseUpgradeItem;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import com.yyon.grapplinghook.customization.CustomizationCategory;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.item.FireworkRocketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GrappleModifierBlock extends BaseEntityBlock {

	public GrappleModifierBlock() {
		super(Block.Properties.copy(Blocks.FLETCHING_TABLE));
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
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult raytraceresult) {
		ItemStack heldStack = playerIn.getItemInHand(hand);
		Item heldItem = heldStack.getItem();

		if (heldItem instanceof BaseUpgradeItem upgradeItem)
			return this.handleUpgradeItem(worldIn, pos, playerIn, hand, upgradeItem);

		if (heldItem instanceof ICustomizationApplicable customItem)
			return this.handleApplyCustomizations(customItem, worldIn, pos, playerIn, heldStack);

		if (heldItem == Items.DIAMOND_BOOTS)
			return this.handleDiamondBoots(worldIn, pos, playerIn, hand, heldStack);

		if (heldItem == Items.DIAMOND)
			return this.handleEasterEgg(worldIn, pos, playerIn);

		if (!worldIn.isClientSide)
			return InteractionResult.CONSUME;

		BlockEntity ent = worldIn.getBlockEntity(pos);

		if (!(ent instanceof GrappleModifierBlockEntity tile))
			return InteractionResult.FAIL;

		GrappleModClient.get().openModifierScreen(tile);

		return InteractionResult.SUCCESS;
	}

	private InteractionResult handleDiamondBoots(Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, ItemStack heldStack) {
		if (worldIn.isClientSide)
			return InteractionResult.SUCCESS;

		if (!GrappleModLegacyConfig.getConf().longfallboots.longfallbootsrecipe) {
			Component msg = Component.translatable("feedback.grapplemod.modifier.long_fall_boots.disabled")
									 .withStyle(ChatFormatting.RED);

			playerIn.sendSystemMessage(msg);
			worldIn.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 1f, 0.3f);

			return InteractionResult.CONSUME;
		}

		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(heldStack);

		boolean invalidForReplacement = enchantments.getOrDefault(Enchantments.FALL_PROTECTION, -1) < 4;

		if (invalidForReplacement) {
			Component msg = Component.translatable("grappletooltip.longfallbootsrecipe.desc")
					.withStyle(ChatFormatting.RED);

			playerIn.sendSystemMessage(msg);
			worldIn.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 1f, 0.3f);

			return InteractionResult.CONSUME;
		}

		Component msg = Component.translatable("feedback.grapplemod.modifier.long_fall_boots");
		ItemStack replacementStack = new ItemStack(GrappleModItems.LONG_FALL_BOOTS.get());
		EnchantmentHelper.setEnchantments(enchantments, replacementStack);

		playerIn.setItemInHand(hand, replacementStack);
		playerIn.sendSystemMessage(msg);
		worldIn.playSound(null, pos, SoundEvents.VILLAGER_WORK_TOOLSMITH, SoundSource.BLOCKS, 1f, 1.0f);

		return InteractionResult.CONSUME;
	}

	private InteractionResult handleApplyCustomizations(ICustomizationApplicable item, Level worldIn, BlockPos pos, Player playerIn, ItemStack heldStack) {
		if (worldIn.isClientSide)
			return InteractionResult.SUCCESS;

		BlockEntity ent = worldIn.getBlockEntity(pos);

		if (!(ent instanceof GrappleModifierBlockEntity blockEntity))
			return InteractionResult.FAIL;

		CustomizationVolume custom = blockEntity.getCurrentCustomizations();

		ItemStack newStack = heldStack.split(1);
		item.applyCustomizations(newStack, custom);

		if(item instanceof IAuthorable authorable)
			authorable.commit(newStack, null, playerIn);

		if(!playerIn.addItem(newStack))
			playerIn.drop(newStack, true);

		Component msg = item.getOverwriteMessage();

		playerIn.sendSystemMessage(msg);
		worldIn.playSound(null, pos, item.getOverwriteSoundEffect(), SoundSource.BLOCKS, 1f, 1.0f);

		return InteractionResult.CONSUME;
	}

	private InteractionResult handleUpgradeItem(Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BaseUpgradeItem upgradeItem) {
		if (worldIn.isClientSide)
			return InteractionResult.SUCCESS;

		BlockEntity ent = worldIn.getBlockEntity(pos);

		if (!(ent instanceof GrappleModifierBlockEntity tile))
			return InteractionResult.FAIL;

		CustomizationCategory category = upgradeItem.getCategory();

		if (category == null)
			return InteractionResult.CONSUME;

		if (tile.isUnlocked(category)) {
			Component msg = Component.translatable("feedback.grapplemod.modifier.upgrade_already_applied")
									 .withStyle(ChatFormatting.RED);
			playerIn.sendSystemMessage(msg);
			worldIn.playSound(null, pos, SoundEvents.NOTE_BLOCK_BASS.value(), SoundSource.BLOCKS, 1f, 0.3f);

			return InteractionResult.CONSUME;
		}

		if (!playerIn.isCreative())
			playerIn.setItemInHand(hand, ItemStack.EMPTY);

		Component msg = Component.translatable("feedback.grapplemod.modifier.applied_new_upgrade")
				.append(" ")
				.append(category.getEmbed());

		tile.unlockCategory(category);
		playerIn.sendSystemMessage(msg);
		worldIn.playSound(null, pos, SoundEvents.ARMOR_EQUIP_CHAIN, SoundSource.BLOCKS, 1f, 1.0f);

		return InteractionResult.CONSUME;
	}

	private InteractionResult handleEasterEgg(Level worldIn, BlockPos pos, Player playerIn) {
		int spacing = 3;
		Vec[] positions = new Vec[] {new Vec(-spacing*2, 0, 0), new Vec(-spacing, 0, 0), new Vec(0, 0, 0), new Vec(spacing, 0, 0), new Vec(2*spacing, 0, 0)};
		int[] colors = new int[] {0x5bcffa, 0xf5abb9, 0xffffff, 0xf5abb9, 0x5bcffa};
		
		for (int i = 0; i < positions.length; i++) {
			Vec newpos = new Vec(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
			Vec toPlayer = Vec.positionVec(playerIn).sub(newpos);
			double angle = toPlayer.length() == 0 ? 0 : toPlayer.getYaw();
			newpos = newpos.add(positions[i].rotateYaw(Math.toRadians(angle)));
			
			CompoundTag explosion = new CompoundTag();
	        explosion.putByte("Type", (byte) FireworkRocketItem.Shape.SMALL_BALL.getId());
	        explosion.putBoolean("Trail", true);
	        explosion.putBoolean("Flicker", false);
	        explosion.putIntArray("Colors", new int[] {colors[i]});
	        explosion.putIntArray("FadeColors", new int[] {});
	        ListTag list = new ListTag();
	        list.add(explosion);

	        CompoundTag fireworks = new CompoundTag();
	        fireworks.put("Explosions", list);

	        CompoundTag nbt = new CompoundTag();
	        nbt.put("Fireworks", fireworks);

	        ItemStack stack = new ItemStack(Items.FIREWORK_ROCKET);
	        stack.setTag(nbt);

			FireworkRocketEntity firework = new FireworkRocketEntity(worldIn, playerIn, newpos.x, newpos.y, newpos.z, stack);
			CompoundTag fireworkSave = new CompoundTag();
			firework.addAdditionalSaveData(fireworkSave);
			fireworkSave.putInt("LifeTime", 15);
			firework.readAdditionalSaveData(fireworkSave);
			worldIn.addFreshEntity(firework);
		}

		return InteractionResult.sidedSuccess(worldIn.isClientSide);
	}


}
