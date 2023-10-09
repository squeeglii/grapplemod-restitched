package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.content.item.GrapplehookItem;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class BlockBreakDetectorMixin {

    @Final
    @Shadow
    protected ServerPlayer player;

    @Inject(method = "destroyBlock(Lnet/minecraft/core/BlockPos;)Z", at = @At(value = "HEAD", shift = At.Shift.AFTER), cancellable = true)
    public void handleBlockBreak(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (player == null) return;

        ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
        Item item = stack.getItem();
        if (item instanceof GrapplehookItem)
            cir.setReturnValue(false);
    }

}
