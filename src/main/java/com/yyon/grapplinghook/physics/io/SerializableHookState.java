package com.yyon.grapplinghook.physics.io;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.physics.ServerHookEntityTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class SerializableHookState {

    private List<HookSnapshot> hooks;
    private CustomizationVolume volume;


    private SerializableHookState(ServerPlayer holder) {
        Set<GrapplinghookEntity> hooks = ServerHookEntityTracker.getHooksThrownBy(holder);
        List<HookSnapshot> hookList = new LinkedList<>();

        CustomizationVolume volumeToSave = null;
        long lastChecksum = -1;

        for(GrapplinghookEntity hook: hooks) {
            CustomizationVolume currentVol = hook.getCurrentCustomizations();
            long currentChecksum = currentVol.getChecksum();

            // The saving is only intended to stop players from falling when they join a game
            // so saving hooks that aren't attached isn't necessary.
            // - if motion data is added and physics are accurate, feel free to remove this.
            if(!hook.isAttachedToSurface())
                continue;

            if(volumeToSave == null) {
                volumeToSave = currentVol;
                lastChecksum = currentChecksum;

            } else if(currentChecksum != lastChecksum) {
                GrappleMod.LOGGER.warn("Holder's hooks have different customization checksums - they should match?");
                continue;
            }

            hookList.add(new HookSnapshot(hook));
        }

        this.volume = volumeToSave != null
                ? volumeToSave
                : new CustomizationVolume();
        this.hooks = hookList;
    }

    private SerializableHookState(CompoundTag tag) {
        //TODO: Decode
    }



    public CompoundTag toNBT() {
        CompoundTag finalTag = new CompoundTag();
        ListTag hookStates = new ListTag();

        for(HookSnapshot snapshot: this.hooks)
            hookStates.add(snapshot.saveToNBT());

        if (hookStates.isEmpty())
            return finalTag;

        finalTag.put("customization", this.volume.writeToNBT());
        finalTag.put("hooks", hookStates);

        return finalTag;
    }



    public static SerializableHookState saveNewFrom(ServerPlayer player) {
        return new SerializableHookState(player);
    }

    public static SerializableHookState serializeFrom(CompoundTag tag) {
        return new SerializableHookState(tag);
    }
}
