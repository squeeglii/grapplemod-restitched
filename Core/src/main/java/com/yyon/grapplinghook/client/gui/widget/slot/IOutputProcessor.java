package com.yyon.grapplinghook.client.gui.widget.slot;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.function.BiConsumer;

@FunctionalInterface
public interface IOutputProcessor extends BiConsumer<Player, ItemStack> {
}
