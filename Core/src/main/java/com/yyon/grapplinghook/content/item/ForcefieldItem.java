package com.yyon.grapplinghook.content.item;

import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.physics.context.GrapplingHookPhysicsController;
import com.yyon.grapplinghook.util.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.yyon.grapplinghook.client.physics.context.AirFrictionPhysicsController.AIR_FRICTION_CONTROLLER;
import static com.yyon.grapplinghook.client.physics.context.ForcefieldPhysicsController.FORCEFIELD_CONTROLLER;

public class ForcefieldItem extends Item {
	public ForcefieldItem() {
		super(new Item.Properties().stacksTo(1));
	}


	@NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
    	ItemStack stack = playerIn.getItemInHand(hand);

		if(!worldIn.isClientSide)
			return InteractionResultHolder.consume(stack);

		int playerId = playerIn.getId();
		GrapplingHookPhysicsController oldController = GrappleModClient.get()
				.getClientControllerManager()
				.getController(playerId);

		if (oldController == null || oldController.getType() == AIR_FRICTION_CONTROLLER) {
			GrappleModClient.get()
					.getClientControllerManager()
					.createControl(FORCEFIELD_CONTROLLER, -1, playerId, worldIn, null, null);
		} else {
			oldController.disable();
		}
        
    	return InteractionResultHolder.success(stack);
	}
    
	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag par4) {
		Options options = Minecraft.getInstance().options;

		list.add(Component.translatable("grappletooltip.repelleritem.desc")
				  .withStyle(ChatFormatting.ITALIC, ChatFormatting.DARK_GRAY)
		);
		list.add(Component.literal(""));

		list.add(Component.translatable("grappletooltip.controls.title")
				  .withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD, ChatFormatting.UNDERLINE)
		);

		list.add(TextUtils.keybinding("grappletooltip.repelleritemon.desc", options.keyUse));
		list.add(TextUtils.keybinding("grappletooltip.repelleritemoff.desc", options.keyUse));
		list.add(TextUtils.keybinding("grappletooltip.repelleritemslow.desc", options.keyShift));
		list.add(TextUtils.keybinding("grappletooltip.repelleritemmove.desc",
				options.keyUp, options.keyLeft, options.keyDown, options.keyRight)
		);
	}
}
