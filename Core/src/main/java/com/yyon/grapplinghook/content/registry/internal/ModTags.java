package com.yyon.grapplinghook.content.registry.internal;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static final TagKey<Block> HOOK_BREAKS = TagKey.create(Registries.BLOCK, GrappleMod.id("hook_breaks"));

    public static final TagKey<Block> HOOK_DISALLOWED = TagKey.create(Registries.BLOCK, GrappleMod.id("hook_disallowed"));
    public static final TagKey<Block> LIMITED_HOOK_ALLOWED = TagKey.create(Registries.BLOCK, GrappleMod.id("limited_hook_allowed"));

    // Run to ensure these are loaded.
    public static void bump() {}
}
