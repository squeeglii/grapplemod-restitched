package com.yyon.grapplinghook.entity.grapplehook;

import net.minecraft.network.FriendlyByteBuf;

public interface IExtendedSpawnPacketEntity {

    void writeSpawnData(FriendlyByteBuf data);
    void readSpawnData(FriendlyByteBuf data);
}
