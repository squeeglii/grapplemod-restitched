package com.yyon.grapplinghook.content.item;

import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.keybind.MinecraftKey;
import com.yyon.grapplinghook.physics.context.GrapplingHookPhysicsContext;
import com.yyon.grapplinghook.util.GrappleModUtils;
import com.yyon.grapplinghook.util.Vec;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class ForcefieldItem extends Item {
	public ForcefieldItem() {
		super(new Item.Properties().stacksTo(1));
	}
	
	public void doRightClick(ItemStack stack, Level worldIn, Player player) {
		if (worldIn.isClientSide) {
			int playerid = player.getId();
			GrapplingHookPhysicsContext oldController = GrappleModClient.get().unregisterController(playerid);
			if (oldController == null || oldController.controllerId == GrappleModUtils.AIR_FRICTION_ID) {
				GrappleModClient.get().createControl(GrappleModUtils.REPEL_ID, -1, playerid, worldIn, new Vec(0,0,0), null, null);
			}
		}
	}

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
    	ItemStack stack = playerIn.getItemInHand(hand);
        this.doRightClick(stack, worldIn, playerIn);
        
    	return InteractionResultHolder.success(stack);
	}
    
	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag par4) {
		list.add(Component.translatable("grappletooltip.repelleritem.desc"));
		list.add(Component.translatable("grappletooltip.repelleritem2.desc"));
		list.add(Component.literal(""));
		list.add(Component.literal(GrappleModClient.get().getKeyname(MinecraftKey.keyBindUseItem) + Component.translatable("grappletooltip.repelleritemon.desc").getString()));
		list.add(Component.literal(GrappleModClient.get().getKeyname(MinecraftKey.keyBindUseItem) + Component.translatable("grappletooltip.repelleritemoff.desc").getString()));
		list.add(Component.literal(GrappleModClient.get().getKeyname(MinecraftKey.keyBindSneak) + Component.translatable("grappletooltip.repelleritemslow.desc").getString()));
		list.add(Component.literal(GrappleModClient.get().getKeyname(MinecraftKey.keyBindForward) + ", " +
				GrappleModClient.get().getKeyname(MinecraftKey.keyBindLeft) + ", " +
				GrappleModClient.get().getKeyname(MinecraftKey.keyBindBack) + ", " +
				GrappleModClient.get().getKeyname(MinecraftKey.keyBindRight) +
				" " + Component.translatable("grappletooltip.repelleritemmove.desc").getString()));
	}
}
