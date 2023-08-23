package com.yyon.grapplinghook.content.item;

import com.yyon.grapplinghook.client.GrappleModClient;
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

public class EnderStaffItem extends Item {
	
	public EnderStaffItem() {
		super(new Item.Properties().stacksTo(1));
	}

	
    @Override
	@NotNull
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
    	ItemStack stack = playerIn.getItemInHand(hand);

		if (!worldIn.isClientSide)
			return InteractionResultHolder.consume(stack);

		GrappleModClient.get().launchPlayer(playerIn);

    	return InteractionResultHolder.success(stack);
	}
    
	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag par4) {
		Options options = Minecraft.getInstance().options;

		list.add(Component
				.translatable("grappletooltip.launcheritem.desc")
				.withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
		list.add(Component.literal(""));


		list.add(Component
				.translatable("grappletooltip.controls.title")
				.withStyle(ChatFormatting.GRAY, ChatFormatting.BOLD, ChatFormatting.UNDERLINE)
		);
		list.add(Component.empty().withStyle(ChatFormatting.DARK_GRAY)
				.append(options.keyUse.getTranslatedKeyMessage())
				.append(Component.translatable("grappletooltip.launcheritemcontrols.desc"))
		);
	}
}
