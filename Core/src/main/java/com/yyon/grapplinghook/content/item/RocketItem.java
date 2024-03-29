package com.yyon.grapplinghook.content.item;

import com.google.common.collect.ImmutableSet;
import com.yyon.grapplinghook.client.GrappleModClient;
import com.yyon.grapplinghook.client.physics.context.GrapplingHookPhysicsController;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.util.TextUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static com.yyon.grapplinghook.client.physics.context.AirFrictionPhysicsController.AIR_FRICTION_CONTROLLER;
import static com.yyon.grapplinghook.client.physics.context.ForcefieldPhysicsController.FORCEFIELD_CONTROLLER;
import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.IS_EQUIPMENT_OVERRIDE;
import static com.yyon.grapplinghook.content.registry.GrappleModCustomizationProperties.ROCKET_ATTACHED;

/*
 * This file is part of GrappleMod.

    GrappleMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GrappleMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GrappleMod.  If not, see <http://www.gnu.org/licenses/>.
 */

public class RocketItem extends Item {

	private static final Set<ResourceLocation> SUPPORTED_TYPES = ImmutableSet.of(
			AIR_FRICTION_CONTROLLER,
			FORCEFIELD_CONTROLLER
	);

	public RocketItem() {
		super(new Properties().stacksTo(1));
	}

	
    @Override
	@NotNull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
    	ItemStack stack = playerIn.getItemInHand(hand);

		if (!worldIn.isClientSide)
			return InteractionResultHolder.consume(stack);

		GrapplingHookPhysicsController controller = GrappleModClient.get()
				.getClientControllerManager()
				.getController(playerIn.getId());

		if(controller != null && !SUPPORTED_TYPES.contains(controller.getType()))
			return InteractionResultHolder.pass(stack);

		// If we've already created/modified a controller for this item, we don't want
		// to create another one as this'll just mess with regen time.
		boolean foundClashingController = controller != null &&
										  controller.areControlsOverridenByEquipment();

		if(foundClashingController)
			return InteractionResultHolder.pass(stack);


		CustomizationVolume volume = new CustomizationVolume();
		volume.set(ROCKET_ATTACHED.get(), true);
		volume.set(IS_EQUIPMENT_OVERRIDE.get(), true);

		GrappleModClient.get().getClientControllerManager().startRocket(playerIn, volume);
		return InteractionResultHolder.success(stack);

	}
    
	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag par4) {
		Options options = Minecraft.getInstance().options;

		list.add(Component
				.translatable("grapple_tooltip.rocket_item.description")
				.withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
		list.add(Component.literal(""));


		list.add(Component
				.translatable("grappletooltip.controls.title")
				.withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD, ChatFormatting.UNDERLINE)
		);
		list.add(TextUtils.keybinding("grapple_tooltip.rocket_item.controls", options.keyUse));
	}
}
