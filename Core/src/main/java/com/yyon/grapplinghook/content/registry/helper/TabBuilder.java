package com.yyon.grapplinghook.content.registry.helper;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.List;

@FunctionalInterface
public interface TabBuilder {

    List<ItemStack> build(CreativeModeTab.ItemDisplayParameters displayParams);

}
