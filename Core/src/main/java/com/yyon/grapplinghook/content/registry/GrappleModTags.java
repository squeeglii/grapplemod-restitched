package com.yyon.grapplinghook.content.registry;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class GrappleModTags {

    public static final TagKey<Block> HOOK_BREAKS = TagKey.create(Registries.BLOCK, GrappleMod.id("hook_breaks"));

}
