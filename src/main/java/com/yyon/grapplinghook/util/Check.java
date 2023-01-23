package com.yyon.grapplinghook.util;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.logging.Logger;

public class Check {

    public static boolean missingTileEntity(BlockEntity blockEntity, Player player, Level level, BlockPos pos) {
        if(blockEntity == null) {
            player.sendSystemMessage(Component.literal("Uh oh! Something went wrong. Check the server log.").withStyle(ChatFormatting.RED));
            Logger.getGlobal().warning(String.format(
                    "Missing a tile entity for BlockGrappleModifier @ %s (%s,%s,%s)",
                    level.dimension(),
                    pos.getX(), pos.getZ(), pos.getZ()
            ));

            return true;
        }

        return false;
    }
}
