package com.yyon.grapplinghook.customization.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import org.jetbrains.annotations.NotNull;

/**
 * Used to make an item throwable as a grappling hook
 *
 * It should be paired with HookCustomization on item, else a default value will be used.
 */
public class HookDeployment {

    private VisualState visualState;

    public HookDeployment(VisualState visualState) {
        this.visualState = visualState;
    }

    // todo: could this actually hold the hook id and tie it to the item better?

    public static final Codec<HookDeployment> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            VisualState.CODEC
             .fieldOf("visual_state")
             .forGetter(HookDeployment::getVisualState)
    ).apply(builder, HookDeployment::new));

    public static final StreamCodec<? super RegistryFriendlyByteBuf, HookDeployment> STREAM_CODEC = ;

    public void thrownAsHolder() {
        this.visualState = VisualState.THROWN_ROPE;
    }

    public void retractedByHolder() {
        this.visualState = VisualState.HELD;
    }

    public boolean isThrown() {
        return this.getVisualState() != VisualState.HELD;
    }

    public VisualState getVisualState() {
        return this.visualState;
    }

    /** State shown in creative inventory, and generally the default state. */
    public static HookDeployment baseState() {
        return new HookDeployment(VisualState.HELD);
    }

    public static HookDeployment entityState() {
        return new HookDeployment(VisualState.THROWN_HOOK);
    }


    public enum VisualState implements StringRepresentable {
        HELD,
        THROWN_HOOK,
        THROWN_ROPE;

        public static Codec<VisualState> CODEC = StringRepresentable.fromValues(VisualState::values);

        @Override
        @NotNull
        public String getSerializedName() {
            return this.name();
        }
    }

}
