package com.yyon.grapplinghook.content.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.yyon.grapplinghook.content.registry.internal.ModEnchantments;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;

import static com.mojang.brigadier.builder.LiteralArgumentBuilder.literal;

public class GrappleModCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        LiteralArgumentBuilder<CommandSourceStack> result = literal("grapplemod");
        result.then(debugBranch());

        return result;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> debugBranch() {
        LiteralArgumentBuilder<CommandSourceStack> result = literal("debug");
        result.then(printOnGround());

        return result;
    }

    private static LiteralArgumentBuilder<CommandSourceStack> printOnGround() {
        LiteralArgumentBuilder<CommandSourceStack> result = literal("is_on_ground");
        result.requires(CommandSourceStack::isPlayer).executes(context -> {
            Player player = context.getSource().getPlayerOrException();

            if(player.onGround()) {
                context.getSource().sendSuccess(() -> Component.literal("You're on ground!"), true);
                return 1;
            } else {
                context.getSource().sendFailure(Component.literal("You're not on ground!"));
                return 0;
            }
        });

        return result;
    }

}
