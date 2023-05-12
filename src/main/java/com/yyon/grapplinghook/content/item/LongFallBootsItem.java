package com.yyon.grapplinghook.content.item;

import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

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

public class LongFallBootsItem extends ArmorItem {

	public LongFallBootsItem(ArmorMaterials material) {
	    super(material, Type.BOOTS, new Item.Properties().stacksTo(1));
	}
	
	@Override
	@Environment(EnvType.CLIENT)
	public void appendHoverText(ItemStack stack, Level world, List<Component> list, TooltipFlag par4) {
		if (!stack.isEnchanted()) {
			if (GrappleModLegacyConfig.getConf().longfallboots.longfallbootsrecipe) {
				list.add(Component.translatable("grappletooltip.longfallbootsrecipe.desc"));
			}
		}
		list.add(Component.translatable("grappletooltip.longfallboots.desc"));
	}
}
