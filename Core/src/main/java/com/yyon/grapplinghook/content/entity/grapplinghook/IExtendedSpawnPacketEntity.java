package com.yyon.grapplinghook.content.entity.grapplinghook;

import net.minecraft.network.FriendlyByteBuf;

public interface IExtendedSpawnPacketEntity {

    void writeSpawnData(FriendlyByteBuf data);
    void readSpawnData(FriendlyByteBuf data);
}
