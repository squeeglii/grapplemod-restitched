package com.yyon.grapplinghook.util;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

public class Check {

    public static boolean missingTileEntity(Block block, BlockEntity blockEntity, Player player, Level level, BlockPos pos) {
        if(blockEntity != null) return false;
        if(block == null) throw new IllegalStateException("Bad Block Entity check. This is a bug!");

        if(player != null)
            player.sendSystemMessage(Component.literal("Uh oh! Something went wrong. Check the server log.").withStyle(ChatFormatting.RED));


        if(level != null && pos != null) {
            GrappleMod.LOGGER.warn(String.format(
                    "Missing a tile entity for BlockGrappleModifier @ %s (%s,%s,%s)",
                    level.dimension(),
                    pos.getX(), pos.getZ(), pos.getZ()
            ));
        } else GrappleMod.LOGGER.warn("Missing a tile entity for BlockGrappleModifier");

        return true;
    }
}
