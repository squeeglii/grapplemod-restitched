package com.yyon.grapplinghook.network;

import com.yyon.grapplinghook.GrappleMod;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public interface C2SPayloadProcessor {

    void process(ServerPlayNetworking.Context ctx);

}
