package com.yyon.grapplinghook.customization.data;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

/**
 * Used to make an item throwable as a grappling hook
 *
 * It should be paired with HookCustomization on item, else a default value will be used.
 */
public class DeployState {

    public static final Codec<DeployState> CODEC;
    public static final StreamCodec<? super RegistryFriendlyByteBuf, DeployState> STREAM_CODEC;

    private boolean thrown;

    public DeployState() {

    }

}
