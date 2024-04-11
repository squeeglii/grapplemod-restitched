package com.yyon.grapplinghook.content.item;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

import java.util.List;

public class LongFallBootsSmithingTemplate extends SmithingTemplateItem {
    public LongFallBootsSmithingTemplate() {
        super(
                Component.translatable(),
                Component.translatable(),
                Component.translatable(),
                Component.translatable(),
                Component.translatable(),
                List.of(new ResourceLocation("item/empty_armor_slot_boots")),
                List.of()
        );
    }
}
