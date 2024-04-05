package com.yyon.grapplinghook.content.registry;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;

import static net.minecraft.world.level.GameRules.Key;
import static net.minecraft.world.level.GameRules.Category;
import static net.minecraft.world.level.GameRules.BooleanValue;

public class GrappleModGamerules {

    public static final Key<BooleanValue> USE_LIMITED_HOOK = GameRuleRegistry.register("useLimitedHook", Category.PLAYER, GameRuleFactory.createBooleanRule(false));

    // Run to ensure these are loaded.
    public static void bump() {}

}
