package com.yyon.grapplinghook.physics.io;

import com.yyon.grapplinghook.GrappleMod;
import com.yyon.grapplinghook.content.entity.grapplinghook.GrapplinghookEntity;
import com.yyon.grapplinghook.content.item.GrapplehookItem;
import com.yyon.grapplinghook.customization.CustomizationVolume;
import com.yyon.grapplinghook.physics.ServerHookEntityTracker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.*;

public class SerializableHookState {

    private static final String NBT_HOOKS = "hooks";
    private static final String NBT_CUSTOMIZATIONS = "customization";

    private final List<HookSnapshot> hooks;
    private final CustomizationVolume volume;


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
        CompoundTag customizationTag = tag.getCompound(NBT_CUSTOMIZATIONS);
        ListTag hooksTag = tag.getList(NBT_HOOKS, ListTag.TAG_COMPOUND);

        LinkedList<HookSnapshot> hooks = new LinkedList<>();

        for(int i = 0; i < hooksTag.size(); i++) {
            CompoundTag hookTag = hooksTag.getCompound(i);

            if(!HookSnapshot.isTagValid(hookTag))
                continue;

            HookSnapshot snapshot = new HookSnapshot(hookTag);
            hooks.add(snapshot);
        }

        CustomizationVolume vol = CustomizationVolume.fromNBT(customizationTag);

        this.hooks = hooks;
        this.volume = vol;
    }


    public CompoundTag toNBT() {
        CompoundTag finalTag = new CompoundTag();
        ListTag hookStates = new ListTag();

        for(HookSnapshot snapshot: this.hooks)
            hookStates.add(snapshot.saveToNBT());

        if (hookStates.isEmpty())
            return finalTag;

        finalTag.put(NBT_CUSTOMIZATIONS, this.volume.writeToNBT());
        finalTag.put(NBT_HOOKS, hookStates);

        return finalTag;
    }


    public void applyTo(ServerPlayer player) {
        List<GrapplinghookEntity> hookEntities = new LinkedList<>();

        boolean isFirstHook = true;

        for (HookSnapshot snapshot: this.hooks) {
            CustomizationVolume newVolume = CustomizationVolume.copyAllFrom(this.volume);
            GrapplinghookEntity e = new GrapplinghookEntity(snapshot, newVolume, player, isFirstHook, this.hooks.size() > 1);
            ServerHookEntityTracker.addGrappleEntity(player.getId(), e);

            HashMap<Entity, GrapplinghookEntity> grapplehookClientEntityTracker = isFirstHook
                    ? GrapplehookItem.grapplehookEntitiesRight
                    : GrapplehookItem.grapplehookEntitiesLeft;

            grapplehookClientEntityTracker.put(player, e);

            hookEntities.add(e);
            player.level().addFreshEntity(e);

            isFirstHook = false;
        }
    }

    public List<HookSnapshot> getHooks() {
        return Collections.unmodifiableList(this.hooks);
    }


    public static SerializableHookState saveNewFrom(ServerPlayer player) {
        return new SerializableHookState(player);
    }

    public static SerializableHookState serializeFrom(CompoundTag tag) {
        return new SerializableHookState(tag);
    }

    public static boolean isValidNBT(CompoundTag tag) {
        if(!tag.contains(NBT_CUSTOMIZATIONS, Tag.TAG_COMPOUND)) return false;
        if(!tag.contains(NBT_HOOKS, Tag.TAG_LIST)) return false;

        return true;
    }
}
