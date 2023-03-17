package com.yyon.grapplinghook.content.block.modifierblock;

import com.yyon.grapplinghook.content.blockentity.GrappleModifierBlockEntity;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.config.GrappleModConfig;
import com.yyon.grapplinghook.content.item.GrapplehookItem;
import com.yyon.grapplinghook.content.item.upgrade.BaseUpgradeItem;
import com.yyon.grapplinghook.content.registry.GrappleModItems;
import com.yyon.grapplinghook.util.Check;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.util.Vec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
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
import java.util.Map;

public class GrappleModifierBlock extends BaseEntityBlock {

	public GrappleModifierBlock() {
		super(Block.Properties.copy(Blocks.STONE).strength(1.5f));
	}


	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new GrappleModifierBlockEntity(pos,state);
	}

	@Override
	public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
		List<ItemStack> drops = new ArrayList<>();
		drops.add(new ItemStack(this.asItem()));

		BlockEntity ent = builder.getParameter(LootContextParams.BLOCK_ENTITY);

		if (!(ent instanceof GrappleModifierBlockEntity tile)) return drops;

		for (CustomizationVolume.UpgradeCategory category : CustomizationVolume.UpgradeCategory.values()) {
			if (tile.unlockedCategories.containsKey(category) && tile.unlockedCategories.get(category)) {
				drops.add(new ItemStack(category.getItem()));
			}
		}
		return drops;
	}


    @Override
	@NotNull
	public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player playerIn, InteractionHand hand, BlockHitResult raytraceresult) {
		ItemStack helditemstack = playerIn.getItemInHand(hand);
		Item helditem = helditemstack.getItem();

		if (helditem instanceof BaseUpgradeItem upgradeItem) {
			if (worldIn.isClientSide)
				return InteractionResult.PASS;

			BlockEntity ent = worldIn.getBlockEntity(pos);
			GrappleModifierBlockEntity tile = (GrappleModifierBlockEntity) ent;

			if (Check.missingTileEntity(tile, playerIn, worldIn, pos))
				return InteractionResult.FAIL;

			CustomizationVolume.UpgradeCategory category = upgradeItem.category;
			if (category == null)
				return InteractionResult.FAIL;

			if (tile.isUnlocked(category)) {
				playerIn.sendSystemMessage(Component.literal("Already has upgrade: " + category.getName()));

			} else {
				if (!playerIn.isCreative())
					playerIn.setItemInHand(hand, ItemStack.EMPTY);

				tile.unlockCategory(category);

				playerIn.sendSystemMessage(Component.literal("Applied upgrade: " + category.getName()));
			}


		} else if (helditem instanceof GrapplehookItem) {
			if (worldIn.isClientSide)
				return InteractionResult.PASS;

			BlockEntity ent = worldIn.getBlockEntity(pos);
			GrappleModifierBlockEntity tile = (GrappleModifierBlockEntity) ent;

			if (Check.missingTileEntity(tile, playerIn, worldIn, pos))
				return InteractionResult.FAIL;

			CustomizationVolume custom = tile.customization;
			GrappleModItems.GRAPPLING_HOOK.get().setCustomOnServer(helditemstack, custom);

			playerIn.sendSystemMessage(Component.literal("Applied configuration"));

		} else if (helditem == Items.DIAMOND_BOOTS) {
			if (worldIn.isClientSide) {
				playerIn.sendSystemMessage(Component.literal("You are not permitted to make Long Fall Boots here.").withStyle(ChatFormatting.RED));
				return InteractionResult.PASS;
			}

			if (!GrappleModConfig.getConf().longfallboots.longfallbootsrecipe)
				return InteractionResult.SUCCESS;

			boolean gaveitem = false;

			if (!helditemstack.isEnchanted())
				return InteractionResult.FAIL;

			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(helditemstack);
			if (enchantments.getOrDefault(Enchantments.FALL_PROTECTION, -1) >= 4) {
				ItemStack newitemstack = new ItemStack(GrappleModItems.LONG_FALL_BOOTS.get());
				EnchantmentHelper.setEnchantments(enchantments, newitemstack);
				playerIn.setItemInHand(hand, newitemstack);
				gaveitem = true;
			}


			if (!gaveitem) {
				playerIn.sendSystemMessage(Component.literal("Right click with diamond boots enchanted with feather falling IV to get long fall boots"));
			}


		} else if (helditem == Items.DIAMOND) {
			this.easterEgg(worldIn, pos, playerIn);

		} else {
			if ((!worldIn.isClientSide) || hand != InteractionHand.MAIN_HAND)
				return InteractionResult.PASS;

			BlockEntity ent = worldIn.getBlockEntity(pos);
			GrappleModifierBlockEntity tile = (GrappleModifierBlockEntity) ent;

			GrappleModClient.get().openModifierScreen(tile);
		}

		return InteractionResult.SUCCESS;
	}
    
    @Override
	@NotNull
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

	public void easterEgg(Level worldIn, BlockPos pos, Player playerIn) {
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
	}


}
