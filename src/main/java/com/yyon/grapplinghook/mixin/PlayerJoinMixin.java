package com.yyon.grapplinghook.mixin;

import com.yyon.grapplinghook.config.GrappleModLegacyConfig;
import com.yyon.grapplinghook.network.NetworkManager;
import com.yyon.grapplinghook.network.clientbound.LoggedInMessage;
import com.yyon.grapplinghook.physics.ServerHookEntityTracker;
import com.yyon.grapplinghook.physics.io.IHookStateHolder;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.storage.PlayerDataStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class PlayerJoinMixin {

    @Shadow @Final private PlayerDataStorage playerIo;

    @Inject(method = "placeNewPlayer(Lnet/minecraft/network/Connection;Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/server/network/CommonListenerCookie;)V",
            at = @At("TAIL"))
    public void onLogin(Connection connection, ServerPlayer player, CommonListenerCookie commonListenerCookie, CallbackInfo ci) {
        NetworkManager.packetToClient(new LoggedInMessage(GrappleModLegacyConfig.getConf()), player);

        if(ServerHookEntityTracker.isSavedHookStateValid(player))
            ServerHookEntityTracker.applyFromSavedHookState(player);

        IHookStateHolder hookStateHolder = (IHookStateHolder) player;
        hookStateHolder.grapplemod$resetLastHookState();
    }

}
