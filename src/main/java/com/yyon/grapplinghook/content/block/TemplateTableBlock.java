package com.yyon.grapplinghook.content.block;

import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.content.blockentity.TemplateTableBlockEntity;
import com.yyon.grapplinghook.content.item.GrapplehookItem;
import com.yyon.grapplinghook.content.item.upgrade.BaseUpgradeItem;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import com.yyon.grapplinghook.customization.CustomizationCategory;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemplateTableBlock extends BaseEntityBlock {

	private static final IntegerProperty TEMPLATES_HELD = IntegerProperty.create("shelves_filled", 0, 4);

	public TemplateTableBlock() {
		super(Properties.copy(Blocks.FLETCHING_TABLE));
		BlockState defaultState = this.stateDefinition.any()
				.setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
				.setValue(TEMPLATES_HELD, 0);

		this.registerDefaultState(defaultState);
	}


    @Override
	@NotNull
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult rayResult) {
		return InteractionResult.sidedSuccess(worldIn.isClientSide);
	}


	@NotNull
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new TemplateTableBlockEntity(pos, state);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(HorizontalDirectionalBlock.FACING);
		builder.add(TEMPLATES_HELD);
	}

	@NotNull
	@Override
	public BlockState rotate(BlockState state, Rotation rotation) {
		Direction rotatedDir = rotation.rotate(state.getValue(HorizontalDirectionalBlock.FACING));
		return state.setValue(HorizontalDirectionalBlock.FACING, rotatedDir);
	}

	@NotNull
	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		Rotation rotatedDir = mirror.getRotation(state.getValue(HorizontalDirectionalBlock.FACING));
		return state.rotate(rotatedDir);
	}

	@NotNull
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		Direction opposite = context.getHorizontalDirection().getOpposite();
		return this.defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, opposite);
	}
}
