package com.yyon.grapplinghook.content.item.type;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IAuthorable {

    default void commit(ItemStack stack, Component displayName, Player author) {
        this.commit(stack, displayName, author.getDisplayName());
    }

    void commit(ItemStack stack, Component displayName, Component author);

}
