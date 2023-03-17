package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.config.GrappleModConfig;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.LoggedInMessage;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class ServerConfigSyncMixin {

    @Inject(method = "placeNewPlayer(Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;)V", at = @At("TAIL"))
    public void onLogin(Connection netManager, ServerPlayer player, CallbackInfo ci) {
        NetworkManager.packetToClient(new LoggedInMessage(GrappleModConfig.getConf()), player);
    }

}
