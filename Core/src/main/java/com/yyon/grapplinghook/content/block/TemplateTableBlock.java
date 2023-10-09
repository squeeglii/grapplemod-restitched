package com.yyon.grapplinghook.content.block;

import com.yyon.grapplinghook.content.blockentity.TemplateTableBlockEntity;
import com.yyon.grapplinghook.content.item.type.ICustomizationApplicable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

public class TemplateTableBlock extends BaseEntityBlock {

	public static final IntegerProperty TEMPLATES_HELD = IntegerProperty.create("shelves_filled", 0, 4);

	public static final int FULL = 4;
	public static final int EMPTY = 0;

	public TemplateTableBlock() {
		super(Properties.copy(Blocks.FLETCHING_TABLE));
		BlockState defaultState = this.stateDefinition.any()
				.setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
				.setValue(TEMPLATES_HELD, EMPTY);

		this.registerDefaultState(defaultState);
	}


    @Override
	@NotNull
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult rayResult) {
		BlockEntity blockEntity = worldIn.getBlockEntity(pos);

		if (!(blockEntity instanceof TemplateTableBlockEntity templateTableBlockEntity))
			return InteractionResult.PASS;

		ItemStack heldStack = playerIn.getItemInHand(hand);
		Item heldItem = heldStack.getItem();



		if(templateTableBlockEntity.isEmpty()) {
			if(worldIn.isClientSide) return InteractionResult.PASS;

			// TODO: Open UI - there is no 'primary blueprint' to quick-apply from.
			return InteractionResult.CONSUME;
		}

		if(!(heldItem instanceof ICustomizationApplicable customizationReciever)) {
			if(worldIn.isClientSide) return InteractionResult.PASS;

			// TODO: Open UI - item can't recieve upgrades
			return InteractionResult.CONSUME;
		}

		// TODO : Apply main blueprint

		return InteractionResult.sidedSuccess(worldIn.isClientSide);
	}

	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {

		// if block isn't removed/replaced, don't onRemove
		if (state.is(newState.getBlock()))
			return;

		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!(blockEntity instanceof TemplateTableBlockEntity templateTableBlockEntity)) {
			super.onRemove(state, level, pos, newState, isMoving);
			return;
		}

		if(templateTableBlockEntity.isEmpty()) {
			super.onRemove(state, level, pos, newState, isMoving);
			return;
		}

		for (int i = 0; i < templateTableBlockEntity.getContainerSize(); i++) {
			ItemStack itemStack = templateTableBlockEntity.getItem(i);

			if (itemStack.isEmpty())
				continue;

			Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), itemStack);
		}

		templateTableBlockEntity.clearContent();
		level.updateNeighbourForOutputSignal(pos, this);

		super.onRemove(state, level, pos, newState, isMoving);
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

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos) {
		if (level.isClientSide())
			return 0;

		BlockEntity blockEntity = level.getBlockEntity(pos);
		if (!(blockEntity instanceof TemplateTableBlockEntity templateTableBlockEntity))
			return 0;

		int templateCount = templateTableBlockEntity.getTemplateCount();
		int maxTemplates = templateTableBlockEntity.getContainerSize();

		return Math.floorDiv(templateCount, maxTemplates);
	}
}
