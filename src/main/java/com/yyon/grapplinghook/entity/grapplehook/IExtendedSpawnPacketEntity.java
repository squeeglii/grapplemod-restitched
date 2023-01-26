package com.yyon.grapplinghook.entity.grapplehook;

import com.yyon.grapplinghook.GrappleMod;
import net.minecraft.network.FriendlyByteBuf;

public interface IExtendedSpawnPacketEntity {

    void writeSpawnData(FriendlyByteBuf data);
    void readSpawnData(FriendlyByteBuf data);
}
