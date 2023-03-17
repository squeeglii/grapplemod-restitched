package com.yyon.grapplinghook.util;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class Check {

    public static boolean missingTileEntity(BlockEntity blockEntity, Player player, Level level, BlockPos pos) {
        if(blockEntity != null) return false;

        player.sendSystemMessage(Component.literal("Uh oh! Something went wrong. Check the server log.").withStyle(ChatFormatting.RED));
        GrappleMod.LOGGER.warn(String.format(
                "Missing a tile entity for BlockGrappleModifier @ %s (%s,%s,%s)",
                level.dimension(),
                pos.getX(), pos.getZ(), pos.getZ()
        ));
        return true;
    }
}
