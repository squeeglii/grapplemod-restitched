package com.yyon.grapplinghook.content.block;

import com.mojang.serialization.MapCodec;
import com.yyon.grapplinghook.content.blockentity.BlueprintShelfBlockEntity;
import com.yyon.grapplinghook.content.item.type.ICustomizationApplicable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;

public class BlueprintShelfBlock extends BaseEntityBlock {

	public static final MapCodec<BlueprintShelfBlock> CODEC = simpleCodec(BlueprintShelfBlock::new);

	public static final IntegerProperty TEMPLATES_HELD = IntegerProperty.create("shelves_filled", 0, 4);

	public static final int FULL = 4;
	public static final int EMPTY = 0;


	public BlueprintShelfBlock(Properties properties) {
		super(properties);
		BlockState defaultState = this.stateDefinition.any()
				.setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
				.setValue(TEMPLATES_HELD, EMPTY);

		this.registerDefaultState(defaultState);
	}

	public BlueprintShelfBlock() {
		this(Properties.ofFullCopy(Blocks.CHISELED_BOOKSHELF));
	}


	@NotNull
	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return CODEC;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (!(blockEntity instanceof BlueprintShelfBlockEntity blueprintShelfBlockEntity))
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;

		ItemStack heldStack = player.getItemInHand(hand);
		Item heldItem = heldStack.getItem();

		// TemplateTable has no 'primary blueprint' so there's nothing to quick-apply from - open UI
		if(blueprintShelfBlockEntity.isEmpty()) {
			if(level.isClientSide) return ItemInteractionResult.SUCCESS;

			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		// Item can't recieve upgrades - open UI
		if(!(heldItem instanceof ICustomizationApplicable customizationReciever)) {
			return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
		}

		// TODO : Apply main blueprint

		return ItemInteractionResult.sidedSuccess(level.isClientSide);
	}

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
		BlockEntity blockEntity = level.getBlockEntity(pos);

		if (!(blockEntity instanceof BlueprintShelfBlockEntity blueprintShelfBlockEntity))
			return InteractionResult.PASS;

		if(level.isClientSide)
			return InteractionResult.SUCCESS;

		player.openMenu(blueprintShelfBlockEntity);
		return InteractionResult.CONSUME;
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {

		// if block isn't removed/replaced, don't onRemove
		if (state.is(newState.getBlock()))
			return;

		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!(blockEntity instanceof BlueprintShelfBlockEntity blueprintShelfBlockEntity)) {
			super.onRemove(state, level, pos, newState, isMoving);
			return;
		}

		if(blueprintShelfBlockEntity.isEmpty()) {
			super.onRemove(state, level, pos, newState, isMoving);
			return;
		}

		for (int i = 0; i < blueprintShelfBlockEntity.getContainerSize(); i++) {
			ItemStack itemStack = blueprintShelfBlockEntity.getItem(i);

			if (itemStack.isEmpty())
				continue;

			Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
		}

		blueprintShelfBlockEntity.clearContent();
		level.updateNeighbourForOutputSignal(pos, this);

		super.onRemove(state, level, pos, newState, isMoving);
	}

	@NotNull
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new BlueprintShelfBlockEntity(pos, state);
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

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		if (level.isClientSide())
			return 0;

		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!(blockEntity instanceof BlueprintShelfBlockEntity blueprintShelfBlockEntity))
			return 0;

		int templateCount = blueprintShelfBlockEntity.getTemplateCount();
		int maxTemplates = blueprintShelfBlockEntity.getContainerSize();

		return Math.floorDiv(templateCount, maxTemplates);
	}
}
